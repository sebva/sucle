<?php

require_once('Settings.php');
require_once('Model/Device.php');
require_once('Model/User.php');
require_once('Model/Message.php');
require_once('Model/Social.php');
require_once('Model/Token.php');

class DataBase
{
    private $pdo;

    public static function errorCode($code)
    {
        return json_encode(array('status' => 'KO','error_code' => $code));
    }

    private static function distance($lat1,$lon1,$lat2,$lon2)
    {
        $R = 6371;
        $dLat = ($lat2-$lat1) * M_PI / 180;
        $dLon = ($lon2-$lon1) * M_PI / 180;
        $a = sin($dLat/2) * sin($dLat/2) +
            cos($lat1 * M_PI / 180 ) * cos($lat2 * M_PI / 180 ) *
            sin($dLon/2) * sin($dLon/2);
        $c = 2 * atan2(sqrt($a), sqrt(1-$a));
        $d = $R * $c;
        return round($d*1000,2);
    }

    public function __construct()
    {
        $settings = Settings::getSettings();
        $dsn = 'mysql:dbname='.$settings[Settings::DATABASE].';host='.$settings[Settings::HOST];

        $parameters = array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8",PDO::ATTR_PERSISTENT => true);
        $this->pdo = new PDO($dsn, $settings[Settings::USER], $settings[Settings::PASSWORD], $parameters);
        $this->pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }

    public function getMessages($lat, $lon, $radius, $nb=null)
    {
        if($nb == null)
            $nb = Settings::getSettings()[Settings::DEFAULT_NB_MESSAGE];

        $q = $this->pdo->prepare('SELECT * FROM `message` ORDER BY `id` DESC');
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $messages = array();
        $distances = array();
        $i = 0;
        if(is_array($data))
        {
            foreach($data as $d)
            {
                $distance = DataBase::distance($lat, $lon, $d['lat'], $d['lon']);
                if($distance <= $radius)
                {

                    $messages[] = array_merge((new Message(array('user' => $this->selectUser($d['user_id']),
                                                    'device' => $this->selectDevice($d['device_id']),
                                                    'lat' => $d['lat'], 'lon' => $d['lon'], 'datetime' => $d['datetime'],
                                                    'mime' => $d['mime'], 'file' => $d['file'], 'message' => $d['message'])))->getInfo(), array('distance' => $distance));
                    $distances[] = $distance;
                    if(++$i > $nb)
                        break;
                }
            }

            array_multisort($distances, $messages);
        }
        return json_encode(array('status' => 'OK', 'messages' => $messages));
    }

    public function sendMessage($token, $device_id, Message $message)
    {
        if(!$this->existsRelation($token, $device_id, $message))
            return DataBase::errorCode(400);

        if(strlen(trim($message->getMessage())) > Settings::getSettings()[Settings::LEN_MESSAGE_MAX])
            return DataBase::errorCode(406);
        if(strlen(trim($message->getMessage())) == 0)
            return DataBase::errorCode(405);

        if($message->getFile() != null)
        {
            $folder = Settings::getSettings()[Settings::UPLOAD].$message->getUser()->getId().'/';
            if(!file_exists($folder))
                mkdir($folder, 0730, true);

            $filePath = $folder.uniqid().'.'.strtolower(substr(strrchr($message->getFile()['name'], '.'),1));
            if(move_uploaded_file($message->getFile()['tmp_name'], $filePath))
                $message->setFile($filePath);
            else
                return DataBase::errorCode(505);
        }
        $this->insertMessage($message);
        return json_encode(array('status' => 'OK'));
    }

    /*
     * Log in or sign up the user
     */
    public function logIn($social_id, $type, $token, Device $device_input)
    {
        if(!in_array($type, Settings::getSettings()[Settings::TYPE_SOCIAL]))
            return DataBase::errorCode(407);

        $user_exists = false;
        $user = $this->existsUser($social_id, $user_exists);

        if(!$user_exists)
            $user = new User(array('inscription' => date("Y-m-d H:i:s")));
        $this->insertUpdateUser($user);

        if($user == null)
            return DataBase::errorCode(501);

        $device_exists = false;
        $device = $this->existsDevice($device_input->getDeviceId(), $device_exists);
        if(!$device_exists)
            $device = new Device(array('device_id' => $device_input->getDeviceId(), 'type' => $device_input->getType(), 'os_version' => $device_input->getOsVersion()));
        $this->insertUpdateDevice($device);

        if($device == null)
            return DataBase::errorCode(502);

        $social_exists = false;
        $social = $this->existsSocial($social_id, $type, $social_exists);
        if(!$social_exists)
            $social = new Social(array('user' => $user, 'social_id' => $social_id, 'type' => $type));
        $this->insertUpdateSocial($social);

        if($social == null)
            return DataBase::errorCode(503);

        $token_exists = false;
        $token_temp = new Token(array('social' => $social, 'device' => $device, 'token' => $token));
        $token = $this->existsToken($token_temp, $token_exists);
        if(!$token_exists)
            $token = $token_temp;
        $this->insertUpdateToken($token);

        if($token == null)
            return DataBase::errorCode(504);

        return json_encode(array('status' => 'OK'));
    }

    /*
     * Exist
     */

    private function existsRelation($token, $device_id, Message &$message)
    {
        $q = $this->pdo->prepare('SELECT `token`,`device`.`id` AS id_device, `social`.`user_id` AS id_user FROM `token` JOIN `device` ON `token`.`device_id` = `device`.`id` JOIN `social` ON `social`.`id` = `token`.`social_id`WHERE `device`.`device_id` = :device_id and `token` = :token');
        $q->bindParam(':device_id', $device_id, PDO::PARAM_STR);
        $q->bindParam(':token', $token, PDO::PARAM_STR);
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $out = is_array($data);
        if($out)
        {
            $message->setUser($this->selectUser($data['id_user']));
            $message->setDevice($this->selectDevice($data['id_device']));

        }
        return $out;
    }

    private function existsToken(Token &$token, &$res)
    {
        $q = $this->pdo->prepare('SELECT `id`, `social_id` AS social, `device_id` AS device, `token` FROM `token` WHERE `social_id` = :social_id and `device_id` = :device_id and `token` = :token');
        $q->bindParam(':social_id', $token->getSocial()->getId(), PDO::PARAM_INT);
        $q->bindParam(':device_id', $token->getDevice()->getId(), PDO::PARAM_INT);
        $q->bindParam(':token', $token->getToken(), PDO::PARAM_STR);
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $res = is_array($data);
        if($res)
        {
            $data['social'] = $this->selectSocial($data['social']);
            $data['device'] = $this->selectDevice($data['device']);
            return new Token($data);
        }
        else
            return null;
    }

    private function existsSocial($social_id, $type, &$res)
    {
        $q = $this->pdo->prepare('SELECT `id`, `user_id` AS user, `social_id`, `type` FROM `social` WHERE `social_id` = :social_id and `type` = :type');
        $q->bindParam(':social_id', $social_id, PDO::PARAM_STR);
        $q->bindParam(':type', $type, PDO::PARAM_STR);
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $res = is_array($data);
        if($res)
        {
            $data['user'] = $this->selectUser($data['user']);
            return new Social($data);
        }
        else
            return null;
    }

    private function existsDevice($device_id, &$res)
    {
        $q = $this->pdo->prepare('SELECT `id`, `device_id`, `type`, `os_version` FROM `device` WHERE `device_id` = :device_id');
        $q->bindParam(':device_id', $device_id, PDO::PARAM_STR);
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $res = is_array($data);
        return $res ? new Device($data) : null;
    }

    private function existsUser($social_id, &$res)
    {
        $q = $this->pdo->prepare('SELECT `user`.`id` AS id, `user`.`inscription` AS inscription FROM `user` JOIN `social` ON `user`.`id` = `social`.`user_id` WHERE `social`.`social_id` = :social_id');
        $q->bindParam(':social_id', $social_id, PDO::PARAM_INT);
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $res = is_array($data);
        return $res ? new User($data) : null;
    }

    /*
     * SELECT
     */

    private function selectUser($id)
    {
        $q = $this->pdo->prepare('SELECT * FROM `user` WHERE `id` = :id');
        $q->bindParam(':id', $id, PDO::PARAM_INT);
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        return new User($data);
    }

    private function selectSocial($id)
    {
        $q = $this->pdo->prepare('SELECT `id`, `user_id` AS user, `social_id`, `type` FROM `social` WHERE `id` = :id');
        $q->bindParam(':id', $id, PDO::PARAM_INT);
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $data['user'] = $this->selectUser($data['user']);
        return new Social($data);
    }

    private function selectDevice($id)
    {
        $q = $this->pdo->prepare('SELECT * FROM `device` WHERE `id` = :id');
        $q->bindParam(':id', $id, PDO::PARAM_INT);
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        return new Device($data);
    }

    /*
     * INSERT OR UPDATE
     */

    private function insertUpdateDevice(Device &$device)
    {
        if($device->getId() == null)
        {
            $q = $this->pdo->prepare('INSERT INTO `device` VALUES(NULL, :device_id, :type, :os_version)');
            $q->bindParam(':device_id', $device->getDeviceId(), PDO::PARAM_STR);
            $q->bindParam(':type', $device->getType(), PDO::PARAM_STR);
            $q->bindParam(':os_version', $device->getOsVersion(), PDO::PARAM_STR);
            $q->execute();

            $device->setId($this->pdo->lastInsertId());
        }
        else
        {
            $q = $this->pdo->prepare('UPDATE `device` SET `device_id`=:device_id, `type`=:type, `os_version`=:os_version WHERE `id`=:id');
            $q->bindParam(':device_id', $device->getDeviceId(), PDO::PARAM_STR);
            $q->bindParam(':type', $device->getType(), PDO::PARAM_STR);
            $q->bindParam(':os_version', $device->getOsVersion(), PDO::PARAM_STR);
            $q->bindParam(':id', $device->getId(), PDO::PARAM_INT);
            $q->execute();
        }
    }

    private function insertUpdateSocial(Social &$social)
    {
        if($social->getId() == null)
        {
            $q = $this->pdo->prepare('INSERT INTO `social` VALUES(NULL, :user_id, :social_id, :type)');
            $q->bindParam(':user_id', $social->getUser()->getId(), PDO::PARAM_INT);
            $q->bindParam(':social_id', $social->getSocialId(), PDO::PARAM_STR);
            $q->bindParam(':type', $social->getType(), PDO::PARAM_STR);
            $q->execute();

            $social->setId($this->pdo->lastInsertId());
        }
        //We never update !
    }

    private function insertUpdateUser(User &$user)
    {
        if($user->getId() == null)
        {
            $q = $this->pdo->prepare('INSERT INTO `user` VALUES(NULL, :inscription)');
            $q->bindParam(':inscription', $user->getInscription(), PDO::PARAM_STR);
            $q->execute();

            $user->setId($this->pdo->lastInsertId());
        }
        else
        {
            $q = $this->pdo->prepare('UPDATE `user` SET `inscription`=:inscription WHERE `id`=:id');
            $q->bindParam(':id', $user->getId(), PDO::PARAM_INT);
            $q->bindParam(':inscription', $user->getInscription(), PDO::PARAM_STR);
            $q->execute();
        }
    }

    private function insertUpdateToken(Token &$token)
    {
        if($token->getId() == null)
        {
            $q = $this->pdo->prepare('INSERT INTO `token` VALUES(NULL, :social_id, :device_id, :token)');
            $q->bindParam(':social_id', $token->getSocial()->getId(), PDO::PARAM_INT);
            $q->bindParam(':device_id', $token->getDevice()->getId(), PDO::PARAM_INT);
            $q->bindParam(':token', $token->getToken(), PDO::PARAM_STR);
            $q->execute();

            $token->setId($this->pdo->lastInsertId());
        }
        else
        {
            $q = $this->pdo->prepare('UPDATE `token` SET `social_id`=:social_id, `device_id`=:device_id ,`token`=:token WHERE `id`=:id');
            $q->bindParam(':social_id', $token->getSocial()->getId(), PDO::PARAM_INT);
            $q->bindParam(':device_id', $token->getDevice()->getId(), PDO::PARAM_INT);
            $q->bindParam(':token', $token->getToken(), PDO::PARAM_STR);
            $q->bindParam(':id', $token->getId(), PDO::PARAM_INT);
            $q->execute();
        }
    }

    private function insertMessage(Message &$message)
    {
        $q = $this->pdo->prepare('INSERT INTO `message` VALUES(NULL, :user_id, :device_id, :lat, :lon, :datetime, :message, :mime, :file)');
        $q->bindParam(':user_id', $message->getUser()->getId(), PDO::PARAM_INT);
        $q->bindParam(':device_id', $message->getDevice()->getId(), PDO::PARAM_INT);
        $q->bindParam(':lat', $message->getLat(), PDO::PARAM_STR);
        $q->bindParam(':lon', $message->getLon(), PDO::PARAM_STR);
        $q->bindParam(':datetime', $message->getDatetime(), PDO::PARAM_STR);
        $q->bindParam(':message', $message->getMessage(), PDO::PARAM_STR);
        $q->bindParam(':mime', $message->getMime(), PDO::PARAM_STR);
        $q->bindParam(':file', $message->getFile(), PDO::PARAM_STR);
        $q->execute();
    }
} 