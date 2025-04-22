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

// Obtener datos del POST
$nombre = $_POST['nombre'];
$fabricante = $_POST['fabricante'];
$modelo = $_POST['modelo'];
$alcance_km = $_POST['alcance_km'] ?? 0;
$num_pasajeros = $_POST['num_pasajeros'] ?? 0;
$personal_cabina = $_POST['personal_cabina'] ?? 0;
$tarifa_base = $_POST['tarifa_base'] ?? 0;
$clase = $_POST['clase'];
$tamano_m = $_POST['tamano_m'] ?? 0;
$facilidades = json_encode($_POST["facilidades"], JSON_UNESCAPED_UNICODE);

// Verificar si el avión ya existe por NOMBRE
$check_sql = "SELECT id FROM aviones WHERE nombre = ?";
$check_stmt = $conn->prepare($check_sql);
$check_stmt->bind_param("s", $nombre);
$check_stmt->execute();
$check_stmt->store_result();

if ($check_stmt->num_rows > 0) {
    echo "Error: El avión '$nombre' ya existe en la base de datos.";
    $check_stmt->close();
    $conn->close();
    exit();
}
$check_stmt->close();

// Insertar nuevo avión
$insert_sql = "INSERT INTO aviones (
    nombre, fabricante, modelo, alcance_km, num_pasajeros,
    personal_cabina, tarifa_base, clase, tamano_m, facilidades
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

$stmt = $conn->prepare($insert_sql);
if ($stmt) {
    $stmt->bind_param(
        "sssiiiisss",
        $nombre, $fabricante, $modelo, $alcance_km, $num_pasajeros,
        $personal_cabina, $tarifa_base, $clase, $tamano_m, $facilidades
    );

    if ($stmt->execute()) {
        echo "Avión '$nombre' añadido correctamente.";
    } else {
        echo "Error al insertar: " . $stmt->error;
    }
    $stmt->close();
} else {
    echo "Error en la consulta: " . $conn->error;
}

$conn->close();
?>