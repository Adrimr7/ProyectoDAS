<?php
// Hecho por Adrian Mena

$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Error de conexion: " . $conn->connect_error);
}

$email = $_POST['email'];

if (empty($email)) {
    echo json_encode(["success" => false, "message" => "Email no proporcionado"]);
    exit;
}

$stmt = $conn->prepare("SELECT foto_perfil FROM usuarios WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    $stmt->bind_result($fotoPerfil);
    $stmt->fetch();

    if ($fotoPerfil) {
        $fotoBase64 = base64_encode($fotoPerfil);
        echo json_encode([
            "success" => true,
            "message" => "Foto obtenida correctamente",
            "foto" => $fotoBase64
        ]);
    } else {
        echo json_encode(["success" => false, "message" => "No se encontró la foto"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Usuario no encontrado"]);
}

$stmt->close();
$conn->close();
?>