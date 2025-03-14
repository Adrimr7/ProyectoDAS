package com.example.das_primeraevaluacion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReservasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReservaAdapter reservaAdapter;
    private ArrayList<Reserva> listaReservas;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("RFragment: onCreateView");
        View view = inflater.inflate(R.layout.fragment_reservas, container, false);

        prefs = requireContext().getSharedPreferences("Reservas", Context.MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.recyclerViewReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaReservas = cargarReservas();

        System.out.println("RFragment: Lista de reservas antes de verificar si está vacía: " + listaReservas.size());

        if (listaReservas.isEmpty()) {
            agregarReservasPorDefecto();
            listaReservas = cargarReservas();
        }

        System.out.println("RFragment: Lista de reservas después de agregar por defecto: " + listaReservas.size());

        reservaAdapter = new ReservaAdapter(listaReservas);
        recyclerView.setAdapter(reservaAdapter);
        reservaAdapter.notifyDataSetChanged();

        return view;
    }

    private void agregarReservasPorDefecto() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        ArrayList<Reserva> reservasPorDefecto = new ArrayList<>();
        reservasPorDefecto.add(new Reserva(1, "Pepe Pepez", "Gulfstream G600", "2025-06-23"));
        reservasPorDefecto.add(new Reserva(2, "John Johnson", "Gulfstream G700", "2025-04-15"));
        reservasPorDefecto.add(new Reserva(3, "Gonzalo González", "Cessna Citation X+", "2025-06-11"));

        String jsonReservas = gson.toJson(reservasPorDefecto);
        editor.putString("lista_reservas", jsonReservas);
        editor.apply();
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

    public void eliminarReserva(int position) {
        listaReservas.remove(position);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString("lista_reservas", gson.toJson(listaReservas));
        editor.apply();
        reservaAdapter.notifyItemRemoved(position);
    }
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

    private void actualizarLista() {
        if (reservaAdapter != null) {
            reservaAdapter.notifyDataSetChanged();
            System.out.println("RFragment: RecyclerView actualizado");
        }
    }
}