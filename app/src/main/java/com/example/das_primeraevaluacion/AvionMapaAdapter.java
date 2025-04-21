package com.example.das_primeraevaluacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class AvionMapaAdapter extends RecyclerView.Adapter<AvionMapaAdapter.AvionViewHolder> {

    private ArrayList<Avion> listaAviones;
    private int selectedPosition = -1;
    private OnAvionSeleccionadoListener listener;

    public interface OnAvionSeleccionadoListener {
        void onAvionSeleccionado(Avion avion);
    }

    public AvionMapaAdapter(ArrayList<Avion> listaAviones, OnAvionSeleccionadoListener listener) {
        this.listaAviones = listaAviones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AvionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_avion_mapa, parent, false);
        return new AvionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AvionViewHolder holder, int position) {
        Avion avion = listaAviones.get(position);

        holder.tvNombre.setText(avion.getNombre());
        holder.tvClase.setText(R.string.num_pax + avion.getNumPasajeros());
        holder.tvTarifa.setText(R.string.tarifa + avion.getTarifaBase() + "€");
        holder.tvAlcance.setText(R.string.alcance_con_dospuntos + avion.getAlcanceKm() + "km");

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(position == selectedPosition);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                int prevSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(prevSelected);
                notifyItemChanged(selectedPosition);
                listener.onAvionSeleccionado(avion);
            } else if (holder.getAdapterPosition() == selectedPosition) {
                selectedPosition = -1;
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaAviones.size();
    }

    public static class AvionViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvClase, tvTarifa, tvAlcance;
        CheckBox checkBox;

        public AvionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvClase = itemView.findViewById(R.id.tvClase);
            tvTarifa = itemView.findViewById(R.id.tvTarifa);
            tvAlcance = itemView.findViewById(R.id.tvAlcance);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}