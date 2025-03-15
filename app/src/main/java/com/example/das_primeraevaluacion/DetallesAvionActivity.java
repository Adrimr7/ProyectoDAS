package com.example.das_primeraevaluacion;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.das_primeraevaluacion.bd.AvionDAO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DetallesAvionActivity extends AppCompatActivity implements EditarAvionFragment.OnAvionUpdatedListener {

    private TextView tvNombre, tvClase, tvTarifa, tvPasajeros, tvAlcance;
    private ImageView ivAvion;


    /**
     * Inicializa la actividad de detalles de un avión.
     * Obtiene los datos del Intent, los muestra y configura los listeners de los botones.
     * @param savedInstanceState Bundle.
     */
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

        Button btnBorrar = findViewById(R.id.btnBorrar);
        btnBorrar.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle(R.string.confirmar_borrado)
                    .setMessage(R.string.confirmar_texto)
                    .setPositiveButton(R.string.eliminar, (dialog, which) -> {
                        // Obtener el ID del avión desde el Intent
                        int avionId = getIntent().getIntExtra("id", -1);
                        if (avionId != -1) {
                            AvionDAO avionDAO = new AvionDAO(v.getContext());
                            avionDAO.borrarAvion(avionId);
                            Toast.makeText(v.getContext(), R.string.avion_borrado, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.btn_cancelar, (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }

    /**
     * Actualiza la interfaz de usuario con los datos del avión.
     * @param nombre String
     * @param clase String
     * @param tarifa int
     * @param pasajeros int
     * @param alcance int
     */

    private void actualizarInterfaz(String nombre, String clase, int tarifa, int pasajeros, int alcance) {
        System.out.println("DetallesAvionActivity: actualizarInterfaz");

        tvNombre.setText(nombre);

        int claseResId = clase.equals("Avioneta") ? R.string.clase_avioneta_text : R.string.clase_text;
        tvClase.setText(getString(claseResId, clase));

        tvTarifa.setText(getString(R.string.tarifa_text, tarifa));
        tvPasajeros.setText(getString(R.string.pasajeros_text, pasajeros));
        tvAlcance.setText(getString(R.string.alcance_text, alcance));
    }
    /**
     * Carga la imagen del avión en la interfaz.
     * Si no se encuentra, imagen genérica.
     *
     * @param nombre Nombre del avion
     */
    private void cargarImagen(String nombre){
        System.out.println("DetallesAvionActivity: cargarImagen");

        String ruta = "images/" + nombre + ".jpg";
        try {
            try (InputStream ims = getAssets().open(ruta)) {
                Bitmap bitmap = BitmapFactory.decodeStream(ims);
                ivAvion.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e) {
                InputStream ims = getAssets().open("images/generico.jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(ims);
                ivAvion.setImageBitmap(bitmap);
                ims.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al acceder a los assets.");
        }
    }

    @Override
    public void onAvionUpdated(Avion avion) {
        System.out.println("DetallesAvionActivity: onAvionUpdated");
        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", avion.getId());
        resultIntent.putExtra("nombre", avion.getNombre());
        resultIntent.putExtra("clase", avion.getClase());
        resultIntent.putExtra("tarifa", avion.getTarifaBase());
        resultIntent.putExtra("num_pasajeros", avion.getNumPasajeros());
        resultIntent.putExtra("alcance_km", avion.getAlcanceKm());
        setIntent(resultIntent);

        actualizarInterfaz(avion.getNombre(), avion.getClase(), avion.getTarifaBase(), avion.getNumPasajeros(), avion.getAlcanceKm());
        cargarImagen(avion.getNombre());

        finish();
    }
}