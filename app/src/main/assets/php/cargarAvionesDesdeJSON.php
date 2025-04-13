<?php

$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die("Error de conexión: " . $conn->connect_error);
}

$jsonData = file_get_contents("datos.json");
if ($jsonData === false) {
    die("No se pudo leer el archivo JSON.");
}

$data = json_decode($jsonData, true);
if ($data === null) {
    die("Error al decodificar JSON: " . json_last_error_msg());
}

$jets = $data["jets"];
$stmt = $conn->prepare("INSERT INTO aviones (nombre, fabricante, modelo, alcance_km, num_pasajeros, personal_cabina, tarifa_base, clase, tamano_m, facilidades) 
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

if (!$stmt) {
    die("Error en la preparación del statement: " . $conn->error);
}

foreach ($jets as $jet) {
    $nombre = $jet["nombre"];
    $fabricante = $jet["fabricante"];
    $modelo = $jet["modelo"];
    $alcance = (int)$jet["alcance_km"];
    $pasajeros = (int)$jet["num_pasajeros"];
    $cabina = (int)$jet["personal_cabina"];
    $tarifa = (int)$jet["tarifa_base"];
    $clase = $jet["clase"];
    $tamano = (int)$jet["tamano_m"];
    $facilidades = json_encode($jet["facilidades"], JSON_UNESCAPED_UNICODE);

    $check = $conn->prepare("SELECT id FROM aviones WHERE nombre = ? AND modelo = ?");
    $check->bind_param("ss", $nombre, $modelo);
    $check->execute();
    $check->store_result();

    if ($check->num_rows > 0) {
        // avion existente
        echo "El avión '$nombre ($modelo)' ya existe. Saltando inserción.<br>";
        $check->close();
        continue;
    }
    $check->close();

    $stmt->bind_param("sssiiiisss", $nombre, $fabricante, $modelo, $alcance, $pasajeros, $cabina, $tarifa, $clase, $tamano, $facilidades);

    if (!$stmt->execute()) {
        echo "Error al insertar '$nombre ($modelo)': " . $stmt->error . "<br>";
    } else {
        echo "Avión '$nombre ($modelo)' insertado correctamente.<br>";
    }
}

$stmt->close();
$conn->close();
?>
