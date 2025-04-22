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

$query = "SELECT * FROM aviones";
$result = $conn->query($query);

$aviones = array();
while ($row = $result->fetch_assoc()) {
    $row['facilidades'] = json_decode($row['facilidades'], true);
    $aviones[] = $row;
}

echo json_encode(array("jets" => $aviones), JSON_UNESCAPED_UNICODE);

$conn->close();
?>
