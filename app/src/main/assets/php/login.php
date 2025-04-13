<?php
// Hecho por Adrian Mena

$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die("Conexion fallida: " . $conn->connect_error);
}

$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';


$sql = "SELECT * FROM usuarios WHERE email=?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

$response = array();

if ($row = $result->fetch_assoc()) {
    if (password_verify($password, $row['password'])) {
        $response['success'] = true;
        $response['message'] = "Login correcto";
        $response['user_id'] = $row['id'];
    } 
    else {
        $response['success'] = false;
        $response['message'] = "Contrasena incorrecta";
    }
} else {
    $response['success'] = false;
    $response['message'] = "Usuario no encontrado";
}

echo json_encode($response);
$conn->close();
?>
