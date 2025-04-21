package com.example.das_primeraevaluacion;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NuevaReservaDialog extends DialogFragment {

    private EditText busquedaOrigen, busquedaDestino;
    private RecyclerView recyclerOrigen, recyclerDestino;
    // private AutoCompleteTextView autoPaisOrigen, autoPaisDestino;
    private Button btnConfirmarReserva;
    private ArrayList<Aeropuerto> todosLosAeropuertos;
    private AeropuertoAdapter aeropuertoAdapterOrigen, aeropuertoAdapterDestino;
    private ArrayList<Aeropuerto> listaAeropuertosOrigen, listaAeropuertosDestino;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        System.out.println("NRDialog: onCreateDialog");
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_nueva_reserva, null);

        // layout
        busquedaOrigen = view.findViewById(R.id.busquedaOrigen);
        busquedaDestino = view.findViewById(R.id.busquedaDestino);
        recyclerOrigen = view.findViewById(R.id.recyclerOrigen);
        recyclerDestino = view.findViewById(R.id.recyclerDestino);
        //autoPaisOrigen = view.findViewById(R.id.autoPaisOrigen);
        //autoPaisDestino = view.findViewById(R.id.autoPaisDestino);
        btnConfirmarReserva = view.findViewById(R.id.btnConfirmarReserva);

        todosLosAeropuertos = obtenerAeropuertos();
        listaAeropuertosOrigen = new ArrayList<>(todosLosAeropuertos);
        listaAeropuertosDestino = new ArrayList<>(todosLosAeropuertos);

        recyclerOrigen.setLayoutManager(new LinearLayoutManager(context));
        recyclerDestino.setLayoutManager(new LinearLayoutManager(context));

        aeropuertoAdapterOrigen = new AeropuertoAdapter(listaAeropuertosOrigen, getContext());
        aeropuertoAdapterDestino = new AeropuertoAdapter(listaAeropuertosDestino, getContext());

        recyclerOrigen.setAdapter(aeropuertoAdapterOrigen);
        recyclerDestino.setAdapter(aeropuertoAdapterDestino);

        busquedaOrigen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                filtrarAeropuertos("a", null, listaAeropuertosOrigen, aeropuertoAdapterOrigen);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filtrarAeropuertos(busquedaOrigen.getText().toString(), null, listaAeropuertosOrigen, aeropuertoAdapterOrigen);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        busquedaDestino.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                filtrarAeropuertos("a", null, listaAeropuertosDestino, aeropuertoAdapterDestino);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filtrarAeropuertos(busquedaDestino.getText().toString(), null, listaAeropuertosDestino, aeropuertoAdapterDestino);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        /*
        autoPaisOrigen.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, obtenerPaises()));
        autoPaisDestino.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, obtenerPaises()));

        autoPaisOrigen.setOnItemClickListener((parent, view1, position, id) -> {
            String paisSeleccionado = (String) parent.getItemAtPosition(position);
            filtrarAeropuertos("", paisSeleccionado, listaAeropuertosOrigen, aeropuertoAdapterOrigen);
        });

        autoPaisDestino.setOnItemClickListener((parent, view1, position, id) -> {
            String paisSeleccionado = (String) parent.getItemAtPosition(position);
            filtrarAeropuertos("", paisSeleccionado, listaAeropuertosDestino, aeropuertoAdapterDestino);
        });
        */

        btnConfirmarReserva.setOnClickListener(v -> {

            Aeropuerto origenSeleccionado = aeropuertoAdapterOrigen.getAeropuertoSeleccionado();
            Aeropuerto destinoSeleccionado = aeropuertoAdapterDestino.getAeropuertoSeleccionado();

            if (origenSeleccionado != null && destinoSeleccionado != null && origenSeleccionado != destinoSeleccionado) {
                System.out.println(origenSeleccionado.getNombre());
                System.out.println(destinoSeleccionado.getNombre());

                Intent intent = new Intent(getContext(), ReservaMapaActivity.class);
                // la clase aeropuerto se ha hecho serializable
                // para poder pasar el aeropuerto al completo al intent.
                intent.putExtra("origen", origenSeleccionado);
                intent.putExtra("destino", destinoSeleccionado);
                startActivity(intent);

                dismiss();
            }
            else {
                Toast.makeText(getContext(), R.string.error_aeropuertos, Toast.LENGTH_SHORT).show();
            }
        });

        return new android.app.AlertDialog.Builder(context)
                .setView(view)
                .create();
    }

    private ArrayList<Aeropuerto> obtenerAeropuertos() {
        System.out.println("NRDialog: obtenerAeropuertos");
        final ArrayList<Aeropuerto> listaAeropuertos = new ArrayList<>();

        new Thread(() -> {
            try {
                URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/aeropuertos/obtenerAeropuertos.php");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                JSONObject response = new JSONObject(stringBuilder.toString());
                JSONArray aeropuertosJson = response.getJSONArray("aeropuertos");

                for (int i = 0; i < aeropuertosJson.length(); i++) {
                    JSONObject aeropuertoJson = aeropuertosJson.getJSONObject(i);

                    Aeropuerto aeropuerto = new Aeropuerto(
                            aeropuertoJson.getString("nombre"),
                            aeropuertoJson.getString("codigo_icao"),
                            aeropuertoJson.getDouble("lat"),
                            aeropuertoJson.getDouble("lon"),
                            aeropuertoJson.getString("pais_castellano"),
                            aeropuertoJson.getString("pais_ingles"),
                            aeropuertoJson.getString("pais_iso")
                    );

                    listaAeropuertos.add(aeropuerto);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return listaAeropuertos;
    }

    private List<String> obtenerPaises() {
        System.out.println("NRDialog: obtenerPaises");
        List<String> paises = new ArrayList<>();
        for (Aeropuerto aeropuerto : todosLosAeropuertos) {
            if (!paises.contains(aeropuerto.getPais_castellano())) {
                paises.add(aeropuerto.getPais_castellano());
            }
            if (!paises.contains(aeropuerto.getPais_ingles())) {
                paises.add(aeropuerto.getPais_ingles());
            }
        }
        return paises;
    }

    private void filtrarAeropuertos(String textoBusqueda, String paisSeleccionado, List<Aeropuerto> listaAeropuertos, AeropuertoAdapter adapter) {
        List<Aeropuerto> listaFiltrada = new ArrayList<>();
        for (Aeropuerto a : todosLosAeropuertos) {
            if ((textoBusqueda.isEmpty() || a.getNombre().toLowerCase().contains(textoBusqueda.toLowerCase())) &&
                    (paisSeleccionado == null || a.getPais_castellano().equalsIgnoreCase(paisSeleccionado) || a.getPais_ingles().equalsIgnoreCase(paisSeleccionado))) {
                listaFiltrada.add(a);
            }
            if (listaFiltrada.size()>30){
                // rendimiento
                break;
            }
        }
        listaAeropuertos.clear();
        listaAeropuertos.addAll(listaFiltrada);
        adapter.notifyDataSetChanged();
    }
}
