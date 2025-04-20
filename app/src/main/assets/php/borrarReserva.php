<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Error de conexión: " . $conn->connect_error);
}

$emailUsuario = $_POST['emailUsuario'];
$fechaIso = $_POST["fechaReserva"];

// convertir fecha de ISO a la correcta en mysql
$dt = new DateTime($fechaIso);
$fechaMysql = $dt->format('Y-m-d H:i:s');

$sql = "DELETE FROM reservas WHERE email_pasajero=? AND fecha_reserva=?";

$stmt = $conn->prepare($sql);
if ($stmt) {
    $stmt->bind_param("ss", $emailUsuario, $fechaMysql);

    if ($stmt->execute()) {
        echo "Reserva de '$emailUsuario' borrada correctamente.";
    } else {
        echo "Error al borrar: " . $stmt->error;
    }
    $stmt->close();
} else {
    echo "Error en la consulta: " . $conn->error;
}

$conn->close();
?>
