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

$query = "SELECT * FROM aeropuertos";
$result = $conn->query($query);

$aeropuertos = array();
while ($row = $result->fetch_assoc()) {
    $aeropuertos[] = $row;
}

echo json_encode(array("aeropuertos" => $aeropuertos), JSON_UNESCAPED_UNICODE);

$conn->close();
?>
