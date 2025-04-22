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

$emailUsuario = $_POST['emailUsuario'];
$fechaIso = $_POST["fechaReserva"];
$avionNombre = $_POST['avionNombre'];
$icaoOrigen = $_POST['icaoOrigen'];
$icaoDestino = $_POST['icaoDestino'];

// convertir fecha de ISO a la correcta en mysql
$dt = new DateTime($fechaIso);
$fechaMysql = $dt->format('Y-m-d H:i:s');

$sql = "INSERT INTO reservas (
    email_pasajero, fecha_reserva, avion_nombre,
    origen_icao, destino_icao) VALUES (?, ?, ?, ?, ?)";

$stmt = $conn->prepare($sql);
if ($stmt) {
    $stmt->bind_param(
        "sssss",
        $emailUsuario, $fechaMysql, $avionNombre, $icaoOrigen, $icaoDestino
    );

    if ($stmt->execute()) {
        echo "Reserva de '$avionNombre' anadida correctamente.";
    } else {
        echo "Error al insertar: " . $stmt->error;
    }
    $stmt->close();
} else {
    echo "Error en la consulta: " . $conn->error;
}

$conn->close();
?>
