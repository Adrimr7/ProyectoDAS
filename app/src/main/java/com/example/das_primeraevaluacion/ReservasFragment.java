package com.example.das_primeraevaluacion.reserva;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.das_primeraevaluacion.Aeropuerto;
import com.example.das_primeraevaluacion.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservasFragment extends Fragment {

    /*
    Datos para aeropuertos adaptados desde:
    (https://ourairports.com/data/ y https://github.com/davidmegginson/ourairports-data)
     */

    private RecyclerView recyclerView;
    private ReservaAdapter reservaAdapter;
    private ArrayList<Reserva> listaReservas;
    private SharedPreferences prefs;

    /**
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * Se ejecuta al crear la vista. Se añaden los varios listeners que
     * todavia no se usan, se usaran en el futuro.
     * @return View vista
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("RFragment: onCreateView");
        View view = inflater.inflate(R.layout.fragment_reservas, container, false);

        prefs = requireContext().getSharedPreferences("Reservas", Context.MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.recyclerViewReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                System.out.println("Dentro del hilo, antes de llamar a agregarReservasDesdeRemoto");
                agregarReservasDesdeRemoto();
                System.out.println("Reservas cargadas: " + listaReservas.size());

                handler.post(() -> {
                    System.out.println("Estamos en el hilo principal ahora");
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        listaReservas = cargarReservas();

        System.out.println("RFragment: Lista de reservas después de agregar por defecto: " + listaReservas.size());

        reservaAdapter = new ReservaAdapter(listaReservas);
        recyclerView.setAdapter(reservaAdapter);
        reservaAdapter.notifyDataSetChanged();


        Button btnReserva = view.findViewById(R.id.btnAnadirReserva);
        btnReserva.setOnClickListener(v -> {
            NuevaReservaDialog nuevaReservaDialog = new NuevaReservaDialog();
            nuevaReservaDialog.show(getFragmentManager(), "nuevaReserva");
        });

        return view;
    }

    /**
     * Se agregan a las preferencias las reservas por defecto.
     * Se hara en el futuro mediante BD

     COMENTADO POR DESUSO

    private void agregarReservasPorDefecto() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        ArrayList<Reserva> reservasPorDefecto = new ArrayList<>();
        reservasPorDefecto.add(new Reserva(1, "pepe@gmail.com", "Gulfstream G600", "2025-06-23", null, null));
        reservasPorDefecto.add(new Reserva(2, "johnlook@gmail.com", "Gulfstream G700", "2025-04-15", null, null));
        reservasPorDefecto.add(new Reserva(3, "gonzi@outlook.es", "Cessna Citation X+", "2025-06-11", null, null));

        String jsonReservas = gson.toJson(reservasPorDefecto);
        editor.putString("lista_reservas", jsonReservas);
        editor.apply();
    }
     */

    private void agregarReservasDesdeRemoto() {
        System.out.println("RFragment: agregarReservasDesdeRemoto. Inicio");
        ArrayList<Reserva> listaReservas = new ArrayList<>();
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        try {
            System.out.println("RFragment: agregarReservasDesdeRemoto. Inicio del try");
            URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/reservas/cargarReservas.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder result = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String linea;
                while ((linea = reader.readLine()) != null) {
                    result.append(linea);
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray reservasArray = jsonObject.getJSONArray("reservas");

                for (int i = 0; i < reservasArray.length(); i++) {
                    JSONObject resObj = reservasArray.getJSONObject(i);

                    String email = resObj.getString("email_pasajero");
                    String avion = resObj.getString("avion_nombre");
                    String fecha = resObj.getString("fecha_reserva");

                    Aeropuerto aeroOrigen = new Aeropuerto(
                            resObj.getString("origen_nombre"),
                            resObj.getString("origen_icao"),
                            resObj.getDouble("origen_lat"),
                            resObj.getDouble("origen_lon"),
                            resObj.getString("origen_pais_iso"),
                            resObj.getString("origen_pais_ingles"),
                            resObj.getString("origen_pais_castellano")
                    );

                    Aeropuerto aeroDestino = new Aeropuerto(
                            resObj.getString("destino_nombre"),
                            resObj.getString("destino_icao"),
                            resObj.getDouble("destino_lat"),
                            resObj.getDouble("destino_lon"),
                            resObj.getString("destino_pais_iso"),
                            resObj.getString("destino_pais_ingles"),
                            resObj.getString("destino_pais_castellano")
                    );

                    Reserva reserva = new Reserva(i + 1,email,avion,fecha,aeroOrigen,aeroDestino);
                    listaReservas.add(reserva);
                }
                String jsonReservas = gson.toJson(listaReservas);
                editor.putString("lista_reservas", jsonReservas);
                editor.apply();
            }
            else {
                System.out.println("RFragment: agregarReservasDesdeRemoto, error; Respuesta del servidor: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private ArrayList<Reserva> cargarReservas() {
        Gson gson = new Gson();
        String json = prefs.getString("lista_reservas", "[]");

        try {
            Type type = new TypeToken<ArrayList<Reserva>>() {}.getType();
            ArrayList<Reserva> reservas = gson.fromJson(json, type);

            if (reservas == null) reservas = new ArrayList<>();
            System.out.println("RFragment: Reservas cargadas: " + reservas.size());
            return reservas;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void onResume() {
        System.out.println("RFragment: onResume");
        super.onResume();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                agregarReservasDesdeRemoto();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listaReservas = cargarReservas();
    }

    public void eliminarReserva(int position) {
        listaReservas.remove(position);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString("lista_reservas", gson.toJson(listaReservas));
        editor.apply();
        reservaAdapter.notifyItemRemoved(position);
    }
    /*
    private void guardarReserva(String nombreAvion) {
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Reserva>>() {}.getType();

        ArrayList<Reserva> listaReservas = gson.fromJson(prefs.getString("lista_reservas", "[]"), type);
        if (listaReservas == null) {
            listaReservas = new ArrayList<>();
        }

        int nuevoId = listaReservas.size() + 1;
        String fechaActual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Reserva nuevaReserva = new Reserva(nuevoId, "Nombre Cliente", fechaActual, nombreAvion);
        listaReservas.add(nuevaReserva);

        editor.putString("lista_reservas", gson.toJson(listaReservas));
        editor.apply();

        actualizarLista();
    }
     */

    private void actualizarLista() {
        if (reservaAdapter != null) {
            reservaAdapter.notifyDataSetChanged();
            System.out.println("RFragment: RecyclerView actualizado");
        }
    }
}