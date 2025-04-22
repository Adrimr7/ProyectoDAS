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

$email = $_POST['email'] ?? '';
$fotoBase64 = $_POST['foto'] ?? '';

if (empty($email) || empty($fotoBase64)) {
    echo json_encode(["success" => false, "message" => "Faltan datos"]);
    exit;
}

$imagenDecodificada = base64_decode($fotoBase64);

$stmt = $conn->prepare("UPDATE usuarios SET foto_perfil = ? WHERE email = ?");
$null = NULL;
$stmt->bind_param("bs", $null, $email);
$stmt->send_long_data(0, $imagenDecodificada);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Foto guardada correctamente"]);
} else {
    echo json_encode(["success" => false, "message" => "Error al guardar la foto"]);
}

$stmt->close();
$conn->close();
?>
