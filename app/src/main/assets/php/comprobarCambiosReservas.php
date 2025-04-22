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

$ultima_fecha = isset($_GET['ultima_fecha']) ? $_GET['ultima_fecha'] : '2000-01-01 00:00:00';

$query = "SELECT COUNT(*) AS total FROM reservas WHERE fecha_reserva > ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("s", $ultima_fecha);
$stmt->execute();
$result = $stmt->get_result();

$row = $result->fetch_assoc();
$hubo_cambio = ($row['total'] > 0);

$fecha_actual = date("Y-m-d H:i:s");

echo json_encode(array(
    "cambio" => $hubo_cambio,
    "nueva_fecha" => $fecha_actual
));

$conn->close();
?>
