<?php


$name="";
if (!empty($_REQUEST['name'])){
$name=$_REQUEST['name'];
}
 
$ip="";
if (!empty($_REQUEST['ip'])){
$ip=$_REQUEST['ip'];
}
 
$port="";
if (!empty($_REQUEST['port'])){
$port=$_REQUEST['port'];
}
 
$archivo="datos.txt";
 
     $file=fopen($archivo,"a");
     fwrite($file,$name."\r\n");
     fwrite($file,$ip."\r\n");
     fwrite($file,$port."\r\n\r\n");
     
     fclose($file);

header("Location: enviado.html");

?>

