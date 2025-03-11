package com.example.das_primeraevaluacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;

public class DetallesAvionActivity extends AppCompatActivity implements EditarAvionFragment.OnAvionUpdatedListener {

    private TextView tvNombre, tvClase, tvTarifa, tvPasajeros, tvAlcance;
    private ImageView ivAvion;
    private static final int EDITAR_AVION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_avion);

        tvNombre = findViewById(R.id.tvNombre);
        tvClase = findViewById(R.id.tvClase);
        tvTarifa = findViewById(R.id.tvTarifa);
        tvPasajeros = findViewById(R.id.tvPasajeros);
        tvAlcance = findViewById(R.id.tvAlcance);
        ivAvion = findViewById(R.id.ivAvion);

        // Obtener los datos del Intent
        String nombre = getIntent().getStringExtra("nombre");
        String clase = getIntent().getStringExtra("clase");
        int tarifa = getIntent().getIntExtra("tarifa", 0);
        int pasajeros = getIntent().getIntExtra("num_pasajeros", 0);
        int alcance = getIntent().getIntExtra("alcance_km", 0);

        // Establecer los valores en los TextViews
        actualizarInterfaz(nombre, clase, tarifa, pasajeros, alcance);
        cargarImagen(nombre);

        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        Button btnEditar = findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(v -> {
            EditarAvionFragment fragment = EditarAvionFragment.newInstance(
                    getIntent().getIntExtra("id", 0),
                    getIntent().getStringExtra("nombre"),
                    getIntent().getStringExtra("clase"),
                    getIntent().getIntExtra("tarifa", 0),
                    getIntent().getIntExtra("num_pasajeros", 0),
                    getIntent().getIntExtra("alcance_km", 0)
            );
            fragment.show(getSupportFragmentManager(), "EditarAvion");
        });
    }

    private void actualizarInterfaz(String nombre, String clase, int tarifa, int pasajeros, int alcance) {
        tvNombre.setText(nombre);
        tvClase.setText("Es un " + clase + ".");
        tvTarifa.setText("Tarifa base de: " + tarifa + " €.");
        tvPasajeros.setText(pasajeros + " pasajeros.");
        tvAlcance.setText(alcance + " km de alcance.");

        if (clase.equals("Avioneta")) {
            tvClase.setText("Es una " + clase + ".");
        }
    }

    private void cargarImagen(String nombre) {
        String ruta = "images/" + nombre + ".jpg";
        try {
            InputStream ims = getAssets().open(ruta);  // Usamos los assets para la imagen
            Bitmap bitmap = BitmapFactory.decodeStream(ims);
            ivAvion.setImageBitmap(bitmap);
            ims.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la imagen.");
        }
    }

    @Override
    public void onAvionUpdated(Avion avion) {
        actualizarInterfaz(avion.getNombre(), avion.getClase(), avion.getTarifaBase(), avion.getNumPasajeros(), avion.getAlcanceKm());
        cargarImagen(avion.getNombre());

    }
}