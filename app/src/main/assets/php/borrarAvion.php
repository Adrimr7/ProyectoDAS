<?php
// Hecho por Adrian Mena

$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Error de conexión: " . $conn->connect_error);
}

$nombre = $_POST['nombre'];

$stmt = $conn->prepare("DELETE FROM aviones WHERE nombre = ?");
$stmt->bind_param("s", $nombre);

if ($stmt->execute()) {
    echo "El avion se ha eliminado correctamente.";
} else {
    echo "Error al eliminar el avion.";
}

$conn->close();
?>