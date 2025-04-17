package com.example.das_primeraevaluacion;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_primeraevaluacion.bd.AvionDAO;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ReservaMapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private TextView tvMapa;
    private Aeropuerto origen;
    private Aeropuerto destino;
    private double distancia = -1;
    private ArrayList<Avion> listaAviones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("RMActivity: onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_mapa);

        origen = (Aeropuerto) getIntent().getSerializableExtra("origen");
        destino = (Aeropuerto) getIntent().getSerializableExtra("destino");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mvMapa);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        tvMapa = findViewById(R.id.tvMapa);

        tvMapa.setText(distancia + "km");

        Button btnVolver = findViewById(R.id.btnVolverMapa);
        btnVolver.setOnClickListener(v -> {
            // todo: que no redirija al menu ppal
            finish();
        });

        Button btnConfirmar = findViewById(R.id.btnConfirmarMapa);
        btnConfirmar.setOnClickListener(v -> {
            // todo: guardar la reserva
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMapa);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        AvionDAO avionDAO = new AvionDAO(getBaseContext());
        listaAviones = avionDAO.obtenerTodosLosAviones();

        if (listaAviones.isEmpty()){
            // cargar aviones desde el php al DAO
            // todo
        }

        filtrarAviones();
        // todo: avionAdapter? incluir la lista de los aviones con el checkbox

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AvionMapaAdapter adapter = new AvionMapaAdapter(listaAviones, avionSeleccionado -> {
            // avion seleccionado (solo uno a la vez)
            System.out.println("Avion seleccionado: " + avionSeleccionado.getNombre());
        });
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("RMActivity: onMapReady");
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            obtenerUbicacionYMostrar();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }
    // en caso de que no se pueda obtener la ubicacion,
    // se muestra un toast.
    private void obtenerUbicacionYMostrar() {
        System.out.println("RMActivity: obtenerUbicacionYMostrar");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                System.out.println("Ubicación obtenida: " + location.getLatitude() + ", " + location.getLongitude());
                mostrarUbicacionEnMapa(location.getLatitude(), location.getLongitude());
            } else {
                System.out.println("No se pudo obtener la ubicación");
                // sacar un toast

            }
            }).addOnFailureListener(e -> {
                System.out.println("Error al obtener la ubicación: " + e.getMessage());
            });
        mostrarOrigenYDestino();
    }

    private void mostrarUbicacionEnMapa(double lat, double lon) {
        LatLng ubicacionActual = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(ubicacionActual).title(getString(R.string.tu_ubicacion)));
    }

    private void mostrarOrigenYDestino() {
        System.out.println("RMActivity: mostrarUbicacionEnMapa");
        LatLng latlngOrigen = new LatLng(origen.getLat(), origen.getLon());
        LatLng latlngDestino = new LatLng(destino.getLat(), destino.getLon());

        mMap.addMarker(new MarkerOptions().position(latlngOrigen).title("Origen: " + origen.getNombre()));
        mMap.addMarker(new MarkerOptions().position(latlngDestino).title("Destino: " + destino.getNombre()));
        System.out.println("RMActivity: despuesMarkers" + latlngOrigen + latlngDestino);

        mMap.addPolyline(new PolylineOptions()
                .add(latlngOrigen, latlngDestino)
                .width(5)
                .color(Color.BLUE));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latlngOrigen);
        builder.include(latlngDestino);

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

        distancia = calcularDistanciaKm(latlngOrigen, latlngDestino);
        tvMapa.setText(distancia + "km");
        double EMISION_POR_KM = 0.115;
        Toast.makeText(this,
                "Distancia: " + String.format("%.2f", distancia) + " km\n" +
                        "Huella: " + String.format("%.2f", distancia * EMISION_POR_KM) + " kg CO₂/pax",
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionYMostrar();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // metodos obligatorios para MapView
    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override protected void onStop() { super.onStop(); mapView.onStop(); }
    @Override protected void onPause() { mapView.onPause(); super.onPause(); }
    @Override protected void onDestroy() { mapView.onDestroy(); super.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    public static double calcularDistanciaKm(LatLng origen, LatLng destino) {
        System.out.println("RMActivity: calcularDistanciaKm");
        // calcular la distancia con la formula de Haversine

        double latOrigenRad = Math.toRadians(origen.latitude);
        double latDestinoRad = Math.toRadians(destino.latitude);
        double difLat = Math.toRadians(destino.latitude - origen.latitude);
        double difLng = Math.toRadians(destino.longitude - origen.longitude);

        double a = Math.sin(difLat / 2) * Math.sin(difLat / 2) +
                Math.cos(latOrigenRad) * Math.cos(latDestinoRad) *
                        Math.sin(difLng / 2) * Math.sin(difLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c;
    }

    private void filtrarAviones() {
        // filtrar aviones que cumplan las condiciones
        if (distancia == -1) {
            LatLng latlngOrigen = new LatLng(origen.getLat(), origen.getLon());
            LatLng latlngDestino = new LatLng(destino.getLat(), destino.getLon());
            distancia = calcularDistanciaKm(latlngOrigen, latlngDestino);
        }
        // lambda de java8 para filtrar los aviones que no cubran la distancia y ordenar por precio.
        // tambien se anade un filtro con tree-set para eliminar duplicados o similares
        listaAviones = listaAviones.stream()
                .filter(p -> p.getAlcanceKm() > distancia)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Avion::getNombre))),
                        lista -> {
                            return lista.stream()
                                    .sorted(Comparator.comparingDouble(Avion::getTarifaBase))
                                    .collect(Collectors.toCollection(ArrayList::new));
                        }
                ));
    }
}