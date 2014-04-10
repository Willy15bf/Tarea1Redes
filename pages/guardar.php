<?php

// Se reciben variables del formulario de index.html con POST
$nombre = isset($_POST['name']) ? $_POST['name'] : $nombre = "\n" ;
$ip = isset($_POST['ip']) ? $_POST['ip'] : $ip = "\n" ;
$puerto = isset($_POST['port']) ? $_POST['port'] : $port = "\n" ;


echo "Para rutear...<br>";
echo "Nombre: " . $nombre . "<br>";
echo "IP: " . $ip . "<br>";
echo "Puerto: " . $puerto . "<br>";


$xml = new DOMDocument('1.0', 'utf-8');
$xml->formatOutput = true;
$xml->preserveWhiteSpace = false;
$xml->load('../data/directorio.xml');

$element = $xml->getElementsByTagName('contacto')->item(0);

$nombre = $element->getElementsByTagName('name')->item(0);
$ip = $element->getElementsByTagName('ip')->item(0);
$puerto = $element->getElementsByTagName('port')->item(0);

$newItem = $xml->createElement('contacto');

$newItem->appendChild($xml->createElement('name', $_POST['name']));
$newItem->appendChild($xml->createElement('ip', $_POST['ip']));
$newItem->appendChild($xml->createElement('port', $_POST['port']));

$xml->getElementsByTagName('directorio')->item(0)->appendChild($newItem);

$xml->save('../data/directorio.xml');

echo "<br>Contacto guardado.";

?>
