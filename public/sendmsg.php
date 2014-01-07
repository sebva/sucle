<?php
header('Content-type: application/json');
require_once('../DataBase.php');
require_once('../Settings.php');

if(isset($_POST['token'], $_POST['device_id'], $_POST['lat'], $_POST['lon'], $_POST['message'])
    && !empty($_POST['token']) && !empty($_POST['device_id']) && !empty($_POST['lat']) && !empty($_POST['lon']) && !empty($_POST['message'])
    && is_numeric($_POST['lat']) && is_numeric($_POST['lon']))
{
    $message = new Message(array('lat' => $_POST['lat'], 'lon' => $_POST['lon'], 'datetime' => date("Y-m-d H:i:s"), 'message' => $_POST['message'], 'parent' => $_POST['parent']));

    if(isset($_POST['parent']) && is_numeric($_POST['parent']))
        $message->setParent($_POST['parent']);

    if(isset($_FILES['file']) && strlen($_FILES['file']['name']) > 0)
    {
        $file = $_FILES['file'];
        if($file['error'] > 0)
            die(DataBase::errorCode(402));
        $settings = Settings::getSettings();
        if($file['size'] > $settings[Settings::MAX_SIZE_FILE])
            die(DataBase::errorCode(403));
        $ok = false;
        $extension = strtolower(substr(strrchr($file['name'], '.'),1));
        foreach($settings[Settings::VALID_EXTENSION] as $c)
            foreach($c as $k)
                if($k == $extension)
                    $ok = true;
        if(!$ok)
            die(DataBase::errorCode(404));

        $message->setMime($file['type']);
        $message->setFile($file);
    }

    $db = new DataBase();
    echo $db->sendMessage($_POST['token'], $_POST['device_id'], $message);
}
else
    die(DataBase::errorCode(401));