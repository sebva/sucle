<?php

require_once('Settings.php');
require_once('Model/Device.php');
require_once('Model/User.php');
require_once('Model/Message.php');
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
        {
            $settings = Settings::getSettings();
            $nb = $settings[Settings::DEFAULT_NB_MESSAGE];
        }
        $q = $this->pdo->prepare('SELECT * FROM `message` WHERE `parent` IS NULL ORDER BY `id` DESC');
        $q->execute();

        $data = $q->fetchAll(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $messages = array();
        $distances = array();
        $i = 0;
        $settings = Settings::getSettings();
        if(is_array($data))
        {
            foreach($data as $d)
            {
                $distance = DataBase::distance(floatval($lat), floatval($lon), floatval($d['lat']), floatval($d['lon']));
                if($distance <= $radius)
                {
                    $message = new Message(array('id' => $d['id'],'user' => $this->selectUser($d['user_id']),
                        'device' => $this->selectDevice($d['device_id']),
						'parent' => $d['parent'],
                        'lat' => $d['lat'], 'lon' => $d['lon'], 'datetime' => $d['datetime'],
                        'mime' => $d['mime'], 'file' => $settings[Settings::WEBSITE].'/'.$d['file'], 'message' => $d['message']));
                    $messages[] = array_merge($message->getInfo(), array('distance' => $distance));
                    $distances[] = $distance;
                    if(++$i > $nb)
                        break;
                }
            }

            array_multisort($distances, $messages);
        }
        return json_encode(array('status' => 'OK', 'messages' => $messages));
    }

    public function getComments($parent)
    {
        $q = $this->pdo->prepare('SELECT * FROM `message` WHERE `parent`=:parent ORDER BY `id` DESC');
        $q->bindParam(':parent', $parent, PDO::PARAM_INT);
        $q->execute();

        $data = $q->fetchAll(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $messages = array();
        $settings = Settings::getSettings();
        if(is_array($data))
            foreach($data as $d)
            {
                $message = new Message(array('id' => $d['id'],'user' => $this->selectUser($d['user_id']),
                    'device' => $this->selectDevice($d['device_id']),
                    'parent' => $d['parent'],
                    'lat' => $d['lat'], 'lon' => $d['lon'], 'datetime' => $d['datetime'],
                    'mime' => $d['mime'], 'file' => $settings[Settings::WEBSITE].'/'.$d['file'], 'message' => $d['message']));
                $messages[] = $message->getInfo();
            }

        return json_encode(array('status' => 'OK', 'messages' => $messages));
    }

    public function sendMessage($token, $device_id, Message $message)
    {
        if(!$this->existsRelation($token, $device_id, $message))
            return DataBase::errorCode(400);

        $settings = Settings::getSettings();
        if(strlen(trim($message->getMessage())) > $settings[Settings::LEN_MESSAGE_MAX])
            return DataBase::errorCode(406);
        if(strlen(trim($message->getMessage())) == 0)
            return DataBase::errorCode(405);

        if($message->getFile() != null)
        {
            $folder = $settings[Settings::UPLOAD].$message->getUser()->getId().'/';
            if(!file_exists($folder))
                mkdir(getenv('OPENSHIFT_DATA_DIR').$folder, 0730, true);

            $msg_file = $message->getFile();
            $filePath = $folder.uniqid().'.'.strtolower(substr(strrchr($msg_file['name'], '.'),1));
            if(move_uploaded_file($msg_file['tmp_name'], getenv('OPENSHIFT_DATA_DIR').$filePath))
            {
                $message->setFile($filePath);
                $finfo = finfo_open(FILEINFO_MIME_TYPE);
                $message->setMime(finfo_file($finfo, getenv('OPENSHIFT_DATA_DIR').$filePath));
                finfo_close($finfo);
            }
            else
                return DataBase::errorCode(505);
        }
        $this->insertMessage($message);
        return json_encode(array('status' => 'OK'));
    }

    /*
     * Log in or sign up the user
     */
    public function logIn($social_id, $type, $token_input, Device $device_input)
    {
        $settings = Settings::getSettings();
        if(!in_array($type, $settings[Settings::TYPE_SOCIAL]))
            return DataBase::errorCode(407);

        $user_exists = false;
        $user = $this->existsUser($social_id, $type, $user_exists);

        if(!$user_exists)
            $user = new User(array('inscription' => date("Y-m-d H:i:s"), 'social_id' => $social_id, 'type' => $type));
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

        $token_exists = false;
        $token_temp = new Token(array('user' => $user, 'device' => $device, 'token' => $token_input));
        $token = $this->existsToken($token_temp, $token_exists);
        if(!$token_exists)
            $token = $token_temp;
        else
            $token->setToken($token_input);
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
        $q = $this->pdo->prepare('SELECT `token`,`device`.`id` AS id_device, `token`.`user_id` AS id_user FROM `token` JOIN `device` ON `token`.`device_id` = `device`.`id` JOIN `user` ON `user`.`id` = `token`.`user_id`WHERE `device`.`device_id` = :device_id and `token` = :token');
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
        $q = $this->pdo->prepare('SELECT `id`, `user_id` AS user, `device_id` AS device, `token` FROM `token` WHERE `user_id` = :user_id and `device_id` = :device_id');
        $q->bindParam(':user_id', $token->getUser()->getId(), PDO::PARAM_INT);
        $q->bindParam(':device_id', $token->getDevice()->getId(), PDO::PARAM_INT);
        $q->execute();

        $data = $q->fetch(PDO::FETCH_ASSOC);
        $q->closeCursor();

        $res = is_array($data);
        if($res)
        {
            $data['user'] = $this->selectUser($data['user']);
            $data['device'] = $this->selectDevice($data['device']);
            return new Token($data);
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

    private function existsUser($social_id, $type, &$res)
    {
        $q = $this->pdo->prepare('SELECT * FROM `user` WHERE `social_id` = :social_id and `type` = :type');
        $q->bindParam(':social_id', $social_id, PDO::PARAM_STR);
        $q->bindParam(':type', $type, PDO::PARAM_STR);
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

    private function insertUpdateUser(User &$user)
    {
        if($user->getId() == null)
        {
            $q = $this->pdo->prepare('INSERT INTO `user` VALUES(NULL, :inscription, :social_id, :type)');
            $q->bindParam(':inscription', $user->getInscription(), PDO::PARAM_STR);
            $q->bindParam(':social_id', $user->getSocialId(), PDO::PARAM_STR);
            $q->bindParam(':type', $user->getType(), PDO::PARAM_STR);
            $q->execute();

            $user->setId($this->pdo->lastInsertId());
        }
        else
        {
            $q = $this->pdo->prepare('UPDATE `user` SET `inscription`=:inscription, `social_id` = :social_id, `type` = :type WHERE `id`=:id');
            $q->bindParam(':id', $user->getId(), PDO::PARAM_INT);
            $q->bindParam(':inscription', $user->getInscription(), PDO::PARAM_STR);
            $q->bindParam(':social_id', $user->getSocialId(), PDO::PARAM_STR);
            $q->bindParam(':type', $user->getType(), PDO::PARAM_STR);
            $q->execute();
        }
    }

    private function insertUpdateToken(Token &$token)
    {
        if($token->getId() == null)
        {
            $q = $this->pdo->prepare('INSERT INTO `token` VALUES(NULL, :user_id, :device_id, :token)');
            $q->bindParam(':user_id', $token->getUser()->getId(), PDO::PARAM_INT);
            $q->bindParam(':device_id', $token->getDevice()->getId(), PDO::PARAM_INT);
            $q->bindParam(':token', $token->getToken(), PDO::PARAM_STR);
            $q->execute();

            $token->setId($this->pdo->lastInsertId());
        }
        else
        {
            $q = $this->pdo->prepare('UPDATE `token` SET `user_id`=:user_id, `device_id`=:device_id ,`token`=:token WHERE `id`=:id');
            $q->bindParam(':user_id', $token->getUser()->getId(), PDO::PARAM_INT);
            $q->bindParam(':device_id', $token->getDevice()->getId(), PDO::PARAM_INT);
            $q->bindParam(':token', $token->getToken(), PDO::PARAM_STR);
            $q->bindParam(':id', $token->getId(), PDO::PARAM_INT);
            $q->execute();
        }
    }

    private function insertMessage(Message &$message)
    {
        $q = $this->pdo->prepare('INSERT INTO `message` VALUES(NULL, :user_id, :device_id, :parent, :lat, :lon, :datetime, :message, :mime, :file)');
        $q->bindParam(':user_id', $message->getUser()->getId(), PDO::PARAM_INT);
        $q->bindParam(':device_id', $message->getDevice()->getId(), PDO::PARAM_INT);
        $q->bindParam(':parent', $message->getParent(), PDO::PARAM_INT);
        $q->bindParam(':lat', $message->getLat(), PDO::PARAM_STR);
        $q->bindParam(':lon', $message->getLon(), PDO::PARAM_STR);
        $q->bindParam(':datetime', $message->getDatetime(), PDO::PARAM_STR);
        $q->bindParam(':message', $message->getMessage(), PDO::PARAM_STR);
        $q->bindParam(':mime', $message->getMime(), PDO::PARAM_STR);
        $q->bindParam(':file', $message->getFile(), PDO::PARAM_STR);
        $q->execute();
    }
} 