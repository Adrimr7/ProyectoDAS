<?php

$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Error de conexión: " . $conn->connect_error);
}

$sql = "DELETE FROM aviones";

if ($conn->query($sql) === TRUE) {
    echo "Todos los aviones fueron eliminados.";
} else {
    echo "Error al eliminar los aviones: " . $conn->error;
}

$conn->close();
?>
