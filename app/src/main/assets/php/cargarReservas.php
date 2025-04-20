<?php

$host = "localhost";
$db = "Xamena028_usuarios";
$user = "Xamena028";
$pass = "uoXd3GyF";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die("Error de conexión: " . $conn->connect_error);
}

$query = "
    SELECT
        r.email_pasajero,
        r.fecha_reserva,
        r.avion_nombre,

        -- aeropuerto de origen
        ao.codigo_icao AS origen_icao,
        ao.nombre AS origen_nombre,
        ao.lat AS origen_lat,
        ao.lon AS origen_lon,
        ao.pais_iso AS origen_pais_iso,
        ao.pais_ingles AS origen_pais_ingles,
        ao.pais_castellano AS origen_pais_castellano,

        -- aeropuerto de destino
        ad.codigo_icao AS destino_icao,
        ad.nombre AS destino_nombre,
        ad.lat AS destino_lat,
        ad.lon AS destino_lon,
        ad.pais_iso AS destino_pais_iso,
        ad.pais_ingles AS destino_pais_ingles,
        ad.pais_castellano AS destino_pais_castellano

    FROM reservas r
    JOIN aeropuertos ao ON r.origen_icao = ao.codigo_icao
    JOIN aeropuertos ad ON r.destino_icao = ad.codigo_icao
";

$result = $conn->query($query);

$reservas = array();
while ($row = $result->fetch_assoc()) {
    $reservas[] = $row;
}

echo json_encode(array("reservas" => $reservas), JSON_UNESCAPED_UNICODE);

$conn->close();
?>
