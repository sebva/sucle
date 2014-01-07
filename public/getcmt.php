<?php
header('Content-type: application/json');
require_once('../DataBase.php');

if(isset($_GET['parent']) && is_numeric($_GET['parent']))
{
    $db = new DataBase();
    echo $db->getComments($_GET['parent']);
}
else
    die(DataBase::errorCode(401));