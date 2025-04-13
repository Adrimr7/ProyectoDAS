<?php
// Hecho por Adrian Mena

$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Error de conexión"]);
    exit;
}

$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

if (empty($email) || empty($password)) {
    echo json_encode(["success" => false, "message" => "Faltan los campos"]);
    exit;
}

// comprobar si el usuario ya existe
$stmt = $conn->prepare("SELECT id FROM usuarios WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "El usuario ya existe"]);
    $stmt->close();
    $conn->close();
    exit;
}
$stmt->close();

// insertar nuevo usuario 
$passwordHash = password_hash($password, PASSWORD_DEFAULT);

$stmt = $conn->prepare("INSERT INTO usuarios (email, password) VALUES (?, ?)");
$stmt->bind_param("ss", $email, $passwordHash);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Usuario registrado con exito"]);
} else {
    echo json_encode(["success" => false, "message" => "Error al registrar"]);
}

$stmt->close();
$conn->close();
?>
