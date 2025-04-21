package com.example.das_primeraevaluacion.reserva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.das_primeraevaluacion.R;

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

        System.out.println(reserva.getDestino().getCodigo_icao());
        System.out.println(reserva.getDestino().getNombre());
        System.out.println(reserva.getOrigen().getCodigo_icao());
        System.out.println(reserva.getOrigen().getNombre());

        holder.tvEmailPasajero.setText(reserva.getEmailPasajero());
        holder.tvFechaReserva.setText(reserva.getFechaReserva());
        holder.tvAvionNombre.setText(reserva.getAvionNombre());
        holder.tvAeroOrigen.setText(reserva.getOrigen().getCodigo_icao());
        holder.tvAeroDestino.setText(reserva.getDestino().getCodigo_icao());
    }

    @Override
    public int getItemCount() {
        if (listaReservas != null) {
            return listaReservas.size();
        }
        return 0;
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEmailPasajero, tvFechaReserva, tvAvionNombre, tvAeroOrigen, tvAeroDestino;

        public ReservaViewHolder(View itemView) {
            super(itemView);
            tvEmailPasajero = itemView.findViewById(R.id.tvEmailPasajero);
            tvFechaReserva = itemView.findViewById(R.id.tvFechaReserva);
            tvAvionNombre = itemView.findViewById(R.id.tvAvionNombre);
            tvAeroOrigen = itemView.findViewById(R.id.tvAeroOrigen);
            tvAeroDestino = itemView.findViewById(R.id.tvAeroDestino);
        }
    }
}
