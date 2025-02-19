package com.example.das_primeraevaluacion;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetallesAvionActivity extends AppCompatActivity {

    private TextView tvNombre, tvFabricante, tvTarifa, tvPasajeros, tvAlcance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_avion);

        tvNombre = findViewById(R.id.tvNombre);
        tvFabricante = findViewById(R.id.tvFabricante);
        tvTarifa = findViewById(R.id.tvTarifa);
        tvPasajeros = findViewById(R.id.tvPasajeros);
        tvAlcance = findViewById(R.id.tvAlcance);

        // Recibir datos
        String nombre = getIntent().getStringExtra("nombre");
        String fabricante = getIntent().getStringExtra("fabricante");
        int tarifa = getIntent().getIntExtra("tarifa", 0);

        // Mostrar datos
        tvNombre.setText(nombre);
        tvFabricante.setText("Fabricante: " + fabricante);
        tvTarifa.setText("Tarifa: €" + tarifa);
    }
}
