Tarea1Redes
===========

Lab1 Redes 2014-1


La tarea se puede como proyecto de eclipse id.

Primero, clonar el proyecto con git clone https://github.com/Willy15bf/Tarea1Redes.git o 
simplemente descargarlo en formato .zip y luego descomprimirlo.
En una terminal, haga:

$cd /ruta/a/Tarea1Redes
Luego, para compilar el proyecto ejecute el siguiente comando:

$javac -d . -sourcepath src -cp lib/gson-2.2.4.jar src/tarea1/ServidorHTTP.java

Finalmente para ejecutar (Linux):
$java -cp .:lib/gson-2.2.4.jar tarea1.ServidorHTTP port

En windows:

$java -cp .;lib/gson-2.2.4.jar tarea1.ServidorHTTP port


port(opcional): numero de puerto sobre el cual el servidor funcionará
Por defecto es el 8080.