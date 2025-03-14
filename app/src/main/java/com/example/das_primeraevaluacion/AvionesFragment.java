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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_primeraevaluacion.bd.AvionDAO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class AvionesFragment extends Fragment {

    private AvionAdapter avionAdapter;
    private ArrayList<Avion> listaAviones;
    private AvionDAO avionDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("AFragment: onCreateView");
        View view = inflater.inflate(R.layout.fragment_aviones, container, false);

        avionDAO = new AvionDAO(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAviones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaAviones = avionDAO.obtenerTodosLosAviones();
        ArrayList<Avion> arr = new ArrayList<Avion>();
        if (Objects.equals(listaAviones, arr)) {
            listaAviones = cargarAvionesDesdeJSON();
        }

        avionAdapter = new AvionAdapter(listaAviones, avion -> {
            Intent intent = new Intent(getContext(), DetallesAvionActivity.class);
            intent.putExtra("id", avion.getId());
            intent.putExtra("nombre", avion.getNombre());
            intent.putExtra("clase", avion.getClase());
            intent.putExtra("tarifa", avion.getTarifaBase());
            intent.putExtra("num_pasajeros", avion.getNumPasajeros());
            intent.putExtra("alcance_km", avion.getAlcanceKm());
            getActivity().runOnUiThread(() -> startActivity(intent));
        });

        recyclerView.setAdapter(avionAdapter);

        avionAdapter.notifyDataSetChanged();
        pasarGarbageCollector();
        return view;
    }

    @Override
    public void onResume() {
        System.out.println("AFragment: onResume");
        super.onResume();
    }

    public void pasarGarbageCollector(){
        Runtime garbage = Runtime.getRuntime();
        garbage.gc();
    }

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

    void resetearBD() {
        System.out.println("AFragment: resetearBD");
        avionDAO.eliminarBD();
        listaAviones.clear();
        listaAviones = cargarAvionesDesdeJSON();

        getActivity().runOnUiThread(() -> avionAdapter.notifyDataSetChanged());
        pasarGarbageCollector();
        //Toast.makeText(this.getContext(), getString(R.string.dialog_reset_db), Toast.LENGTH_SHORT).show();
    }

    public void onAvionAgregado(String nombre, String clase, int tarifa, int numPasajeros, int alcance) {
        System.out.println("onAvionAgregado");
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
        System.out.println("Mostrar notificación: " + nombreAvion);

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

    public void agregarAvion(int requestCode, int resultCode, Intent data) {
        System.out.println("Entrado a onActivityResult");
        if (resultCode == 0 && data != null) {

            int id = data.getIntExtra("id", -1);
            String nombre = data.getStringExtra("nombre");
            String clase = data.getStringExtra("clase");
            int tarifa = data.getIntExtra("tarifa", 0);
            int pasajeros = data.getIntExtra("num_pasajeros", 0);
            int alcance = data.getIntExtra("alcance_km", 0);

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

    public void agregarAvion(String nombre, String clase, int tarifa, int pasajerosReal, int alcanceReal) {
        Avion nuevoAvion = new Avion(0, nombre, "", "", alcanceReal, pasajerosReal, 0, tarifa, clase, 0, null);
        nuevoAvion.setId((int) avionDAO.insertarAvion(nuevoAvion));
        listaAviones.add(nuevoAvion);
        try {
            avionAdapter.notifyItemInserted(listaAviones.size() - 1);
        } catch (Exception exc) {
            mostrarNotificacion("ERROR" + exc);
        }
        mostrarNotificacion(nombre);
    }
}

