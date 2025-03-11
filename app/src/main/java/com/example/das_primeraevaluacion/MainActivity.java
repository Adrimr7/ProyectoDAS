package com.example.das_primeraevaluacion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.das_primeraevaluacion.bd.AvionDAO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AgregarAvionDialog.OnAvionAgregadoListener{
    private AvionDAO avionDAO;
    private RecyclerView recyclerView;
    private AvionAdapter adapter;
    private List<Avion> listaAviones;
    private Button btnAgregar;
    private Button btnResetear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        avionDAO = new AvionDAO(this);
        //
        avionDAO.eliminarBD();

        listaAviones = avionDAO.obtenerTodosLosAviones();
        System.out.println(listaAviones);
        if (listaAviones.isEmpty())
        {
            System.out.println("no hay info en BD");
            listaAviones = cargarAvionesDesdeJSON();
        }


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AvionAdapter(listaAviones, avion -> {
            Intent intent = new Intent(MainActivity.this, DetallesAvionActivity.class);
            intent.putExtra("nombre", avion.getNombre());
            intent.putExtra("clase", avion.getClase());
            intent.putExtra("tarifa", avion.getTarifaBase());
            intent.putExtra("num_pasajeros", avion.getNumPasajeros());
            intent.putExtra("alcance_km", avion.getAlcanceKm());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        // listaAviones = avionDAO.obtenerTodosLosAviones();
        // arreglar adapter, no usar notifyDataSetChanged
        adapter.notifyDataSetChanged();

        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> mostrarDialogoAgregarAvion());
        btnResetear = findViewById(R.id.btnReiniciarBD);
        btnResetear.setOnClickListener(v -> resetearBD());
    }
    private void resetearBD() {
        // codigo para resetear BD.
        System.out.println("ResetearBD");
        avionDAO.eliminarBD();
        listaAviones = cargarAvionesDesdeJSON();
        // tener en cuenta al resetear!
        adapter.notifyDataSetChanged();
        System.out.println("BD Reseteada");
    }
    private List<Avion> cargarAvionesDesdeJSON() {
        List<Avion> aviones = new ArrayList<>();
        try {
            System.out.println("Entrado a cargarAvionesDesdeJSON");
            InputStream is = getAssets().open("datos.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("jets");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                // Crear el objeto Avion
                Avion avion = new Avion(
                        0, // ID se asignará al insertar en la base de datos
                        obj.getString("nombre"),
                        obj.getString("fabricante"),
                        obj.getString("modelo"),
                        obj.getInt("alcance_km"),
                        obj.getInt("num_pasajeros"),
                        obj.getInt("personal_cabina"),
                        obj.getInt("tarifa_base"),
                        obj.getString("clase"),
                        obj.getInt("tamano_m"),
                        null // Lista de facilidades vacía por ahora
                );
                long idGenerado = avionDAO.insertarAvion(avion);
                avion.setId((int) idGenerado);
                aviones.add(avion);
            }
            System.gc();
        } catch (Exception e) {
            Log.e("JSON_ERROR", e.getMessage());
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

        btnCancelar.setOnClickListener(v -> {
            System.out.println("Cancelando");
            dialog.dismiss();
        });

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String clase = etClase.getText().toString().trim();
            String tarifaStr = etTarifa.getText().toString().trim();

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty()) {
                etNombre.setError("Este campo es obligatorio");
                return;
            }
            if (clase.isEmpty()) {
                etClase.setError("Este campo es obligatorio");
                return;
            }
            if (tarifaStr.isEmpty()) {
                etTarifa.setError("Este campo es obligatorio");
                return;
            }

            int tarifa;
            try {
                tarifa = Integer.parseInt(tarifaStr);
                if (tarifa < 0) { // Asegurar que la tarifa no sea negativa
                    etTarifa.setError("La tarifa debe ser un número positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                etTarifa.setError("Ingrese un número válido");
                return;
            }

            // Crear el nuevo avión solo si los datos son válidos
            Avion nuevoAvion = new Avion(0, nombre, "", "", 0, 0, 0, tarifa, clase, 0, null);
            listaAviones.add(nuevoAvion);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
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
        List<String> laLista = new ArrayList<>();

        Avion nuevoAvion = new Avion(0, nombre, "", "", 0, 0, 0, tarifa, clase, 0, laLista);
        long idGenerado = avionDAO.insertarAvion(nuevoAvion);
        nuevoAvion.setId((int) idGenerado);
        listaAviones.add(nuevoAvion);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Avión agregado con ID " + idGenerado, Toast.LENGTH_SHORT).show();
    }
}
