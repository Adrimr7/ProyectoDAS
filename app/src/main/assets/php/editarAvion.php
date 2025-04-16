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
$clase = $_POST['clase'];
$tarifa = $_POST['tarifa_base'];
$pasajeros = $_POST['num_pasajeros'];
$alcance = $_POST['alcance_km'];

$stmt = $conn->prepare("UPDATE aviones SET
                        clase = ?,
                        tarifa_base = ?,
                        num_pasajeros = ?,
                        alcance_km = ?
                        WHERE nombre = ?");

$stmt->bind_param("siiis", $clase, $tarifa, $pasajeros, $alcance, $nombre);
$stmt->execute();

echo $stmt->affected_rows > 0 ? 'OK' : 'FAIL';

$conn->close();
?>