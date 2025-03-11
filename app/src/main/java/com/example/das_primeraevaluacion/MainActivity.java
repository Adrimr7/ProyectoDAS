package com.example.das_primeraevaluacion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_primeraevaluacion.bd.AvionDAO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AgregarAvionDialog.OnAvionAgregadoListener {
    private AvionDAO avionDAO;
    private RecyclerView recyclerView;
    private AvionAdapter adapter;
    private List<Avion> listaAviones;
    private Button btnAgregar, btnResetear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        avionDAO = new AvionDAO(this);
        listaAviones = avionDAO.obtenerTodosLosAviones();

        if (listaAviones.isEmpty()) {
            listaAviones = cargarAvionesDesdeJSON();
        }

        setupRecyclerView();
        setupBotones();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AvionAdapter(listaAviones, avion -> {
            Intent intent = new Intent(MainActivity.this, DetallesAvionActivity.class);
            intent.putExtra("id", avion.getId());
            intent.putExtra("nombre", avion.getNombre());
            intent.putExtra("clase", avion.getClase());
            intent.putExtra("tarifa", avion.getTarifaBase());
            intent.putExtra("num_pasajeros", avion.getNumPasajeros());
            intent.putExtra("alcance_km", avion.getAlcanceKm());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupBotones() {
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> mostrarDialogoAgregarAvion());

        btnResetear = findViewById(R.id.btnReiniciarBD);
        btnResetear.setOnClickListener(v -> resetearBD());
    }

    private void resetearBD() {
        avionDAO.eliminarBD();
        listaAviones.clear();
        listaAviones.addAll(cargarAvionesDesdeJSON());
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Base de datos reseteada", Toast.LENGTH_SHORT).show();
    }

    private List<Avion> cargarAvionesDesdeJSON() {
        List<Avion> aviones = new ArrayList<>();
        try {
            InputStream is = getAssets().open("datos.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONArray jsonArray = new JSONObject(new String(buffer, StandardCharsets.UTF_8)).getJSONArray("jets");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Avion avion = new Avion(0, obj.getString("nombre"), obj.getString("fabricante"), obj.getString("modelo"),
                        obj.getInt("alcance_km"), obj.getInt("num_pasajeros"), obj.getInt("personal_cabina"),
                        obj.getInt("tarifa_base"), obj.getString("clase"), obj.getInt("tamano_m"), null);

                avion.setId((int) avionDAO.insertarAvion(avion));
                aviones.add(avion);
            }
        } catch (Exception e) {
            Log.e("JSON_ERROR", "Error al cargar JSON", e);
        }
        return aviones;
    }

    private void mostrarDialogoAgregarAvion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_agregar_avion, null);
        builder.setView(view);

        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etClase = view.findViewById(R.id.etClase);
        EditText etTarifa = view.findViewById(R.id.etTarifa);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnAgregar);

        AlertDialog dialog = builder.create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String clase = etClase.getText().toString().trim();
            String tarifaStr = etTarifa.getText().toString().trim();

            if (nombre.isEmpty() || clase.isEmpty() || tarifaStr.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int tarifa = Integer.parseInt(tarifaStr);
                if (tarifa < 0) throw new NumberFormatException();

                onAvionAgregado(nombre, clase, tarifa);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Introduce un número válido para la tarifa", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void mostrarNotificacion(String nombreAvion) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "aviones_channel";

        NotificationChannel channel = new NotificationChannel(channelId, "Aviones", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Nuevo Avión Agregado")
                .setContentText("Se ha agregado: " + nombreAvion)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onAvionAgregado(String nombre, String clase, int tarifa) {
        Avion nuevoAvion = new Avion(0, nombre, "", "", 0, 0, 0, tarifa, clase, 0, null);
        nuevoAvion.setId((int) avionDAO.insertarAvion(nuevoAvion));
        listaAviones.add(nuevoAvion);
        adapter.notifyItemInserted(listaAviones.size() - 1);
        mostrarNotificacion(nombre);
    }

    // Este método se llama cuando volvemos de la actividad de detalles (editar)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Entrado a onActivityResult");
        if (resultCode == 0 && data != null) {

            int id = data.getIntExtra("id", -1);
            String nombre = data.getStringExtra("nombre");
            String clase = data.getStringExtra("clase");
            int tarifa = data.getIntExtra("tarifa", 0);
            int pasajeros = data.getIntExtra("num_pasajeros", 0);
            int alcance = data.getIntExtra("alcance_km", 0);

            // Encontramos el avión con ese ID y lo actualizamos
            for (int i = 0; i < listaAviones.size(); i++) {
                Avion avion = listaAviones.get(i);
                if (avion.getId() == id) {
                    System.out.println("El id que coincide es: " + id);
                    avion.setNombre(nombre);
                    avion.setClase(clase);
                    avion.setTarifaBase(tarifa);
                    avion.setNumPasajeros(pasajeros);
                    avion.setAlcanceKm(alcance);
                    adapter.notifyItemChanged(i, avion);
                    break;
                }
            }
        }
    }
}