package com.example.das_primeraevaluacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    /**
     * El clasico adapter para el modelo MVC (modelo-vista-controlador)
     * Se implementa un listener para escuchar los cambios que se hacen de cara a
     * gestionar las listas de las reservas.
     */
    private ArrayList<Reserva> listaReservas;

    // Constructora
    public ReservaAdapter(ArrayList<Reserva> listaReservas) {
        this.listaReservas = listaReservas;
    }

    @Override
    public ReservaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReservaViewHolder holder, int position) {
        Reserva reserva = listaReservas.get(position);

        holder.tvNombrePasajero.setText(reserva.getNombrePasajero());
        holder.tvFechaReserva.setText(reserva.getFechaReserva());
        holder.tvAvionNombre.setText(reserva.getAvionNombre());
    }

    @Override
    public int getItemCount() {
        if (listaReservas != null) {
            return listaReservas.size();
        }
        return 0;
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombrePasajero, tvFechaReserva, tvAvionNombre;

        public ReservaViewHolder(View itemView) {
            super(itemView);
            tvNombrePasajero = itemView.findViewById(R.id.tvNombrePasajero);
            tvFechaReserva = itemView.findViewById(R.id.tvFechaReserva);
            tvAvionNombre = itemView.findViewById(R.id.tvAvionNombre);
        }
    }
}
