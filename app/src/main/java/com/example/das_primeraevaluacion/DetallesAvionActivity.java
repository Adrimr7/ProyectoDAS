package com.example.das_primeraevaluacion;

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

public class DetallesAvionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_avion);

        String nombre = getIntent().getStringExtra("nombre");
        String clase = getIntent().getStringExtra("clase");
        int tarifa = getIntent().getIntExtra("tarifa", 0); // Valor predeterminado 0 si no se pasa
        int pasajeros = getIntent().getIntExtra("num_pasajeros", 0);
        int alcance = getIntent().getIntExtra("alcance_km", 0);

        String ruta = "images/" + nombre + ".jpg";

        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvClase = findViewById(R.id.tvClase);
        TextView tvTarifa = findViewById(R.id.tvTarifa);
        TextView tvPasajeros = findViewById(R.id.tvPasajeros);
        TextView tvAlcance = findViewById(R.id.tvAlcance);
        ImageView ivAvion = findViewById(R.id.ivAvion);

        tvNombre.setText(nombre);
        tvClase.setText("Es un " + clase + ".");
        tvTarifa.setText("Tarifa base de: "  + tarifa + " €.");
        tvPasajeros.setText(String.valueOf(pasajeros) + " pasajeros.");
        tvAlcance.setText(String.valueOf(alcance) + " km de alcance.");

        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        try {
            InputStream ims = getAssets().open(ruta);
            Bitmap bitmap = BitmapFactory.decodeStream(ims);
            ivAvion.setImageBitmap(bitmap);
            ims.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la imagen.");
        }

    }
}
