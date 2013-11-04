<?php

require_once('../DataBase.php');

if(isset($_GET['lat'], $_GET['lon'], $_GET['r'])
    && !empty($_GET['lat']) && !empty($_GET['lon']) && !empty($_GET['r'])
    && is_numeric($_GET['lat']) && is_numeric($_GET['lon']) && is_numeric($_GET['r']))
    echo (new DataBase())->getMessages($_GET['lat'], $_GET['lon'], $_GET['r'], (isset($_GET['nb']) && !empty($_GET['nb']) && is_int($_GET['nb'])) ? $_GET['nb'] : null);
else
    die(DataBase::errorCode(401));