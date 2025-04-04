package com.example.das_primeraevaluacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AvionAdapter extends RecyclerView.Adapter<AvionAdapter.ViewHolder> {
    /**
     * El clasico adapter para el modelo MVC (modelo-vista-controlador)
     * Se implementa un listener para escuchar los cambios que se hacen de cara a
     * gestionar las listas.
     */
    private final List<Avion> aviones;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Avion avion);
    }

    public AvionAdapter(List<Avion> aviones, OnItemClickListener listener) {
        this.aviones = aviones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_avion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Avion avion = aviones.get(position);
        holder.nombre.setText(avion.getNombre());
        holder.clase.setText(avion.getClase());
        holder.tarifa.setText("$" + avion.getTarifaBase());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(avion));
    }

    @Override
    public int getItemCount() {
        return aviones != null ? aviones.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, clase, tarifa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombre);
            clase = itemView.findViewById(R.id.tvClase);
            tarifa = itemView.findViewById(R.id.tvTarifa);
        }
    }
}
