<?php

$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Error de conexión: " . $conn->connect_error);
}

$nombre = $_POST['nombre'];
$fabricante = $_POST['fabricante'];
$modelo = $_POST['modelo'];
$alcance_km = $_POST['alcance_km'];
$num_pasajeros = $_POST['num_pasajeros'];
$personal_cabina = $_POST['personal_cabina'];
$tarifa_base = $_POST['tarifa_base'];
$clase = $_POST['clase'];
$tamano_m = $_POST['tamano_m'];
$facilidades = $_POST['facilidades'];  // campo opcional


$stmt = $conn->prepare("INSERT INTO aviones (nombre, fabricante, modelo, alcance_km, num_pasajeros, personal_cabina, tarifa_base, clase, tamano_m, facilidades)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

if ($stmt) {
    $stmt->bind_param("sssiiiisss", $nombre, $fabricante, $modelo, $alcance_km, $num_pasajeros, $personal_cabina, $tarifa_base, $clase, $tamano_m, $facilidades);

    if ($stmt->execute()) {
        echo "Avión añadido correctamente a la BD remota.";
    } else {
        echo "Error al insertar avión en la BD remota: " . $stmt->error;
    }

    $stmt->close();
} else {
    echo "Error al preparar la consulta: " . $conn->error;
}

$conn->close();
?>
