package com.example.das_primeraevaluacion;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReservaAdapter reservaAdapter;
    private ArrayList<Reserva> listaReservas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reservaAdapter = new ReservaAdapter(listaReservas);
        recyclerView.setAdapter(reservaAdapter);

        cargarReservasDesdeServidor();

        Button btnReserva = view.findViewById(R.id.btnAnadirReserva);
        btnReserva.setOnClickListener(v -> {
            NuevaReservaDialog nuevaReservaDialog = new NuevaReservaDialog();
            nuevaReservaDialog.show(getFragmentManager(), "nuevaReserva");
        });

        return view;
    }

    private void cargarReservasDesdeServidor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ArrayList<Reserva> nuevasReservas = new ArrayList<>();

            try {
                URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/reservas/cargarReservas.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder result = new StringBuilder();
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

                    Aeropuerto origen = new Aeropuerto(
                            resObj.getString("origen_nombre"),
                            resObj.getString("origen_icao"),
                            resObj.getDouble("origen_lat"),
                            resObj.getDouble("origen_lon"),
                            resObj.getString("origen_pais_iso"),
                            resObj.getString("origen_pais_ingles"),
                            resObj.getString("origen_pais_castellano")
                    );

                    Aeropuerto destino = new Aeropuerto(
                            resObj.getString("destino_nombre"),
                            resObj.getString("destino_icao"),
                            resObj.getDouble("destino_lat"),
                            resObj.getDouble("destino_lon"),
                            resObj.getString("destino_pais_iso"),
                            resObj.getString("destino_pais_ingles"),
                            resObj.getString("destino_pais_castellano")
                    );

                    Reserva reserva = new Reserva(i + 1, email, avion, fecha, origen, destino);
                    nuevasReservas.add(reserva);
                }

                reader.close();
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                listaReservas.clear();
                listaReservas.addAll(nuevasReservas);
                reservaAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarReservasDesdeServidor();
    }
}
