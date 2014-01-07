<?php

require_once('../Settings.php');

$settings = Settings::getSettings();
$filename = getenv('OPENSHIFT_DATA_DIR').$settings[Settings::UPLOAD].$_GET['user'].'/'.$_GET['file'].'.'.$_GET['ext']; 
$finfo = finfo_open(FILEINFO_MIME_TYPE);
header('Content-type:'.finfo_file($finfo, $filename));
finfo_close($finfo);

$handle = fopen($filename, "rb"); 
$fsize = filesize($filename);
while($fsize > 0)
{
	$size = 1000;
	echo fread($handle, $size); 
	$fsize -= $size;
}
fclose($handle);