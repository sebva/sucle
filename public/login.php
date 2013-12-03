<?php
header('Content-type: application/json');
require_once('../DataBase.php');
require_once('../Model/Device.php');

if(isset($_POST['social_id'], $_POST['type_social'], $_POST['token'], $_POST['device_id'], $_POST['type_mobile'], $_POST['os_version'])
    && !empty($_POST['social_id']) && !empty($_POST['type_social']) && !empty($_POST['token']) && !empty($_POST['device_id']) && !empty($_POST['type_mobile']) && !empty($_POST['os_version'])
    && is_numeric($_POST['social_id']))
{
    $db = new DataBase();
    echo $db->logIn($_POST['social_id'], $_POST['type_social'], $_POST['token'], new Device(array('device_id' => $_POST['device_id'], 'type' => $_POST['type_mobile'], 'os_version' => $_POST['os_version'])));
}
else
    die(DataBase::errorCode(401));