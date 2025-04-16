<?php
$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$archivo_csv = "aeropuertos_actualizado.csv";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die("Error de conexión: " . $conn->connect_error);
} else {
    echo "Conexión exitosa.<br>";
}

if (($handle = fopen($archivo_csv, "r")) !== false) {
    fgetcsv($handle); // Saltar cabecera

    while (($data = fgetcsv($handle, 1000, ",")) !== false) {
        echo "Leyendo fila: " . var_dump($data) . "<br>";

        $codigo_icao = $conn->real_escape_string($data[0]);
        $nombre = $conn->real_escape_string($data[1]);
        $lat = floatval($data[2]);
        $lon = floatval($data[3]);
        $pais_iso = !empty($data[5]) ? "'" . $conn->real_escape_string($data[5]) . "'" : "NULL";
        $pais_ingles = $conn->real_escape_string($data[9]);
        $pais_castellano = $conn->real_escape_string($data[10]);

        echo "Insertando: codigo_icao=$codigo_icao, nombre=$nombre, lat=$lat, lon=$lon, pais_iso=$pais_iso<br>";

        $sql = "INSERT INTO aeropuertos (codigo_icao, nombre, lat, lon, pais_iso, pais_ingles, pais_castellano) VALUES ('$codigo_icao', '$nombre', $lat, $lon, $pais_iso, '$pais_ingles', '$pais_castellano')";

        echo "Consulta SQL: $sql<br>";

        if (!$conn->query($sql)) {
            echo "Error en fila con codigo_icao $codigo_icao: " . $conn->error . "<br>";
        }
    }

    fclose($handle);
    echo "Importación completada.<br>";
} else {
    echo "No se pudo abrir el archivo CSV.<br>";
}

