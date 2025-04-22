package com.example.das_primeraevaluacion.avion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_primeraevaluacion.R;
import com.example.das_primeraevaluacion.clases.Avion;

import java.util.ArrayList;

// clase para poder cargar los aviones en ReservaMapaActivity
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


    /**
     * Vincula los datos con la vista, configura los textos y checkbox..
     *
     * @param holder AvionViewHolder
     * @param position int
     */
    @Override
    public void onBindViewHolder(@NonNull AvionViewHolder holder, int position) {
        Avion avion = listaAviones.get(position);

        Context context = holder.itemView.getContext();
        holder.tvNombre.setText(avion.getNombre());
        holder.tvClase.setText(context.getString(R.string.num_pax) + ": " + avion.getNumPasajeros());
        holder.tvTarifa.setText(context.getString(R.string.tarifa) + ": " + avion.getTarifaBase() + "€");
        holder.tvAlcance.setText(context.getString(R.string.alcance_con_dospuntos) + ": " + avion.getAlcanceKm() + "km");


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

    // clase ViewHolder para el RecyclerView donde se anaden las cosas de los items
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