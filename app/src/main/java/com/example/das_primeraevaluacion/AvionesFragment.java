package com.example.das_primeraevaluacion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_primeraevaluacion.bd.AvionDAO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AvionesFragment extends Fragment {

    private AvionAdapter avionAdapter;
    private ArrayList<Avion> listaAviones;
    private AvionDAO avionDAO;

    /**
     * Se ejecuta al crear la vista del fragment.
     * Inicializa el RecyclerView con la lista de aviones y configura adapter con la BD.
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return vista View.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("AFragment: onCreateView");
        View view = inflater.inflate(R.layout.fragment_aviones, container, false);

        avionDAO = new AvionDAO(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAviones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaAviones = avionDAO.obtenerTodosLosAviones();
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        // si la bd es vacia, cargar desde php
        if (listaAviones.isEmpty()) {

            new Thread(() -> {
                progressBar.setVisibility(View.VISIBLE);
                ArrayList<Avion> avionesRemotos = cargarAvionesDesdeRemoto();
                // hilo ppal
                requireActivity().runOnUiThread(() -> {
                    listaAviones = avionesRemotos;
                    avionAdapter = new AvionAdapter(listaAviones, avion -> {
                        Intent intent = new Intent(getContext(), DetallesAvionActivity.class);
                        intent.putExtra("id", avion.getId());
                        intent.putExtra("nombre", avion.getNombre());
                        intent.putExtra("clase", avion.getClase());
                        intent.putExtra("tarifa", avion.getTarifaBase());
                        intent.putExtra("num_pasajeros", avion.getNumPasajeros());
                        intent.putExtra("alcance_km", avion.getAlcanceKm());
                        startActivity(intent);
                    });

                    recyclerView.setAdapter(avionAdapter);
                    avionAdapter.notifyDataSetChanged();
                    pasarGarbageCollector();
                    progressBar.setVisibility(View.GONE);
                });
            }).start();
        } else {
            // si ya hay datos locales, mostrar
            avionAdapter = new AvionAdapter(listaAviones, avion -> {
                Intent intent = new Intent(getContext(), DetallesAvionActivity.class);
                intent.putExtra("id", avion.getId());
                intent.putExtra("nombre", avion.getNombre());
                intent.putExtra("clase", avion.getClase());
                intent.putExtra("tarifa", avion.getTarifaBase());
                intent.putExtra("num_pasajeros", avion.getNumPasajeros());
                intent.putExtra("alcance_km", avion.getAlcanceKm());
                startActivity(intent);
            });

            recyclerView.setAdapter(avionAdapter);
            avionAdapter.notifyDataSetChanged();
            pasarGarbageCollector();
        }

        return view;
    }

    // comentado en MainActivity
    @Override
    public void onResume() {
        System.out.println("AFragment: onResume");
        super.onResume();
    }
    // comentado en MainActivity
    public void pasarGarbageCollector(){
        Runtime garbage = Runtime.getRuntime();
        garbage.gc();
    }

    /**
     * Carga la lista de aviones desde un JSON y los añade a BD.
     * Se usa un buffer y se les asigna un id autoincremental por la BD.
     * @return ArrayList<Avion> Lista de aviones

    COMENTADO PORQUE YA NO SE USA

    private ArrayList<Avion> cargarAvionesDesdeJSON() {
        System.out.println("AFragment: cargarAvionesDesdeJSON");
        ArrayList<Avion> aviones = new ArrayList<>();
        try {
            InputStream is = requireContext().getAssets().open("datos.json");
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
            pasarGarbageCollector();
        } catch (Exception e) {
            Log.e("JSON_ERROR", "Error al cargar JSON", e);
        }
        return aviones;
    }
     */

    private ArrayList<Avion> cargarAvionesDesdeRemoto() {
        System.out.println("AFragment: cargarAvionesDesdeRemoto");
        ArrayList<Avion> aviones = new ArrayList<>();
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {

            URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/obtenerAviones.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.connect();

            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder jsonBuilder = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                jsonBuilder.append(linea);
            }

            JSONArray listaJSON = new JSONObject(jsonBuilder.toString()).getJSONArray("jets");
            for (int i = 0; i < listaJSON.length(); i++) {
                JSONObject obj = listaJSON.getJSONObject(i);
                Avion avion = new Avion(
                        0,
                        obj.getString("nombre"),
                        obj.getString("fabricante"),
                        obj.getString("modelo"),
                        obj.getInt("alcance_km"),
                        obj.getInt("num_pasajeros"),
                        obj.getInt("personal_cabina"),
                        obj.getInt("tarifa_base"),
                        obj.getString("clase"),
                        obj.getInt("tamano_m"),
                        null // no se usa facilidades
                );

                avion.setId((int) avionDAO.insertarAvion(avion));
                aviones.add(avion);
            }

            pasarGarbageCollector();

        } catch (Exception e) {
            System.out.println("AFragment: cargarAvionesDesdeRemoto, Error al cargar JSON remoto" + e);
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
            if (conn != null) conn.disconnect();
        }

        return aviones;
    }


    /**
     * Reinicia la base de datos, y carga otra vez los aviones desde el JSON.
     */
    void resetearBD() {
        System.out.println("AFragment: resetearBD");

        ProgressBar progressBar = getView().findViewById(R.id.progressBar);
        requireActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
        });

        avionDAO.eliminarBD();
        listaAviones.clear();

        new Thread(() -> {
            try {

                URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/resetearBD.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("BD remota vaciada con éxito.");
                }
                else {
                    System.out.println("Error al vaciar la BD remota: " + responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // llamar a la función que mete los aviones desde el JSON

            try {

                URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/cargarAvionesDesdeJSON.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("BD remota vaciada con éxito.");
                }
                else {
                    System.out.println("Error al vaciar la BD remota: " + responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<Avion> avionesRemotos = cargarAvionesDesdeRemoto();

            requireActivity().runOnUiThread(() -> {
                listaAviones = avionesRemotos;
                avionAdapter.notifyDataSetChanged();
                pasarGarbageCollector();

                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    /**
     * Se llama cuando se agrega un avión a BD y a la lista de aviones en el fragment.
     * Se actualiza el RecyclerView y se envia una notificacion.
     * @param nombre String
     * @param clase String
     * @param tarifa int
     * @param numPasajeros int
     * @param alcance int
     */

    public void onAvionAgregado(String nombre, String clase, int tarifa, int numPasajeros, int alcance) {
        System.out.println("AFragment: onAvionAgregado");
        Avion nuevoAvion = new Avion(0, nombre, "", "", alcance, numPasajeros, 0, tarifa, clase, 0, null);
        nuevoAvion.setId((int) avionDAO.insertarAvion(nuevoAvion));
        listaAviones.add(nuevoAvion);
        try {
            avionAdapter.notifyItemInserted(listaAviones.size() - 1);
        } catch (Exception exc) {
            mostrarNotificacion("ERROR" + exc);
        }
        mostrarNotificacion(nombre);
    }

    private void mostrarNotificacion(String nombreAvion) {
        System.out.println("AFragment: mostrarNotificacion, " + nombreAvion);

        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "aviones_channel";

        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Aviones",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.avion_agregado1))
                .setContentText(getString(R.string.avion_agregado2) + nombreAvion)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());

    }
    /**
     * @param codigoRequest int
     * @param codigoRespuesta int
     * @param respuesta Intent
     * Se ejecuta al volver de la actividad, agrega el avion al fragment
     */
    public void agregarAvion(int codigoRequest, int codigoRespuesta, Intent respuesta) {
        System.out.println("Entrado a onActivityResult");
        if (codigoRespuesta == 0 && respuesta != null) {

            int id = respuesta.getIntExtra("id", -1);
            String nombre = respuesta.getStringExtra("nombre");
            String clase = respuesta.getStringExtra("clase");
            int tarifa = respuesta.getIntExtra("tarifa", 0);
            int pasajeros = respuesta.getIntExtra("num_pasajeros", 0);
            int alcance = respuesta.getIntExtra("alcance_km", 0);

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
                    avionAdapter.notifyItemChanged(i, avion);
                    break;
                }
            }
            pasarGarbageCollector();
        }
    }
    /**
     * Se llama para agregar un avion a la BD
     * Se actualiza el RecyclerView y se envia una notificacion.
     * @param nombre String
     * @param clase String
     * @param tarifa int
     * @param pasajerosReal int
     * @param alcanceReal int
     */
    public void agregarAvion(String nombre, String clase, int tarifa, int pasajerosReal, int alcanceReal) {
        Avion nuevoAvion = new Avion(0, nombre, "Desconocido", "Desconocido", alcanceReal, pasajerosReal, 0, tarifa, clase, 0, null);
        nuevoAvion.setId((int) avionDAO.insertarAvion(nuevoAvion));
        listaAviones.add(nuevoAvion);
        try {
            avionAdapter.notifyItemInserted(listaAviones.size() - 1);
        } catch (Exception exc) {
            mostrarNotificacion("ERROR" + exc);
        }
        mostrarNotificacion(nombre);

        new Thread(() -> {
            try {
                URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/anadirAvion.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);

                String postData = "nombre=" + URLEncoder.encode(nuevoAvion.getNombre(), "UTF-8") +
                        "&fabricante=" + URLEncoder.encode(nuevoAvion.getFabricante(), "UTF-8") +
                        "&modelo=" + URLEncoder.encode(nuevoAvion.getModelo(), "UTF-8") +
                        "&alcance_km=" + nuevoAvion.getAlcanceKm() +
                        "&num_pasajeros=" + nuevoAvion.getNumPasajeros() +
                        "&personal_cabina=" + nuevoAvion.getPersonalCabina() +
                        "&tarifa_base=" + nuevoAvion.getTarifaBase() +
                        "&clase=" + URLEncoder.encode(nuevoAvion.getClase(), "UTF-8") +
                        "&tamano_m=" + nuevoAvion.getTamanoM() +
                        "&facilidades=" + URLEncoder.encode("vacio", "UTF-8");
                System.out.println("Agregando avion: " + postData);
                conn.getOutputStream().write(postData.getBytes("UTF-8"));

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Avión añadido correctamente a la BD remota.");
                } else {
                    System.out.println("Error al añadir avión a la BD remota: " + responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}

