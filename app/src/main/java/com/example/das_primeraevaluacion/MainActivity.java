package com.example.das_primeraevaluacion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AgregarAvionDialog.OnAvionAgregadoListener{

    private RecyclerView recyclerView;
    private AvionAdapter adapter;
    private List<Avion> listaAviones;
    private Button btnAgregar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaAviones = cargarAvionesDesdeJSON();
        adapter = new AvionAdapter(listaAviones, avion -> {
            Intent intent = new Intent(MainActivity.this, DetallesAvionActivity.class);
            intent.putExtra("nombre", avion.getNombre());
            intent.putExtra("fabricante", avion.getFabricante());
            intent.putExtra("tarifa", avion.getTarifaBase());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> mostrarDialogoAgregarAvion());
    }

    private List<Avion> cargarAvionesDesdeJSON() {
        List<Avion> aviones = new ArrayList<>();
        try {
            InputStream is = getAssets().open("aviones.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                aviones.add(new Avion(
                        obj.getString("nombre"),
                        obj.getString("fabricante"),
                        obj.getString("modelo"),
                        obj.getInt("alcance_km"),
                        obj.getInt("num_pasajeros"),
                        obj.getInt("personal_cabina"),
                        obj.getInt("tarifa_base"),
                        obj.getString("clase"),
                        obj.getInt("tamano_m"),
                        null
                ));
            }
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
        EditText etFabricante = view.findViewById(R.id.etFabricante);
        EditText etTarifa = view.findViewById(R.id.etTarifa);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);

        AlertDialog dialog = builder.create();

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String fabricante = etFabricante.getText().toString();
            int tarifa = Integer.parseInt(etTarifa.getText().toString());

            Avion nuevoAvion = new Avion(nombre, fabricante, "", 0, 0, 0, tarifa, "", 0, null);
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
    public void onAvionAgregado(String nombre, String fabricante, int tarifa) {
        List<String> laLista = new ArrayList<>();
        listaAviones.add(new Avion(nombre, fabricante,"", 0, 0, 0, tarifa, "", 0, laLista));
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Avión agregado: " + nombre, Toast.LENGTH_SHORT).show();
    }
}
