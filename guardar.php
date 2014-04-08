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
     fwrite($file,$name.$ip.$port);
     fclose($file);
?>

