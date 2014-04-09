<?php
	$file_handle = fopen("datos.txt", "r");

	while (!feof($file_handle)) {
		$line = fgets($file_handle);
		echo $line;
		echo "<br>";
		}
fclose($file_handle);

?>



