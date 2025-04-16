package com.example.das_primeraevaluacion;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AeropuertoAdapter extends RecyclerView.Adapter<AeropuertoAdapter.AeropuertoViewHolder> {

    private Context contexto;
    private ArrayList<Aeropuerto> aeropuertos;
    private Aeropuerto aeropuertoSeleccionado;

    // constructora
    public AeropuertoAdapter(ArrayList<Aeropuerto> aeropuertos, Context contexto) {
        this.contexto = contexto;
        this.aeropuertos = aeropuertos;
        this.aeropuertoSeleccionado = null;
    }

    @Override
    public AeropuertoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_aeropuerto, parent, false);
        return new AeropuertoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AeropuertoViewHolder holder, int position) {
        Aeropuerto aeropuerto = aeropuertos.get(position);

        holder.nombreAeropuerto.setText(aeropuerto.getNombre());
        holder.codigoIcao.setText(aeropuerto.getCodigo_icao());
        SharedPreferences prefs = contexto.getSharedPreferences("Settings", MODE_PRIVATE);
        String idiomaActual = prefs.getString("My_Lang", "es");

        if ("en".equals(idiomaActual)) {
            holder.paisAeropuerto.setText(aeropuerto.getPais_ingles());
        }
        else {
            holder.paisAeropuerto.setText(aeropuerto.getPais_castellano());
        }

        holder.checkBox.setChecked(aeropuerto.equals(aeropuertoSeleccionado));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // esto se hace asi de complejo por errores pasados
            // a la hora de checkear y des-checkear rapidamente
            // los distintos aeropuertos
            if (isChecked) {
                //
                if (aeropuertoSeleccionado != null) {
                    int prevPos = aeropuertos.indexOf(aeropuertoSeleccionado);
                    aeropuertoSeleccionado = null;
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        notifyItemChanged(prevPos);
                    }, 50);
                }

                aeropuertoSeleccionado = aeropuerto;

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    notifyItemChanged(aeropuertos.indexOf(aeropuerto));
                }, 150);
            } else if (aeropuerto.equals(aeropuertoSeleccionado)) {
                aeropuertoSeleccionado = null;
            }
        });
    }

    @Override
    public int getItemCount() {
        return aeropuertos.size();
    }

    public static class AeropuertoViewHolder extends RecyclerView.ViewHolder {

        TextView nombreAeropuerto, paisAeropuerto, codigoIcao;
        CheckBox checkBox;

        public AeropuertoViewHolder(View itemView) {
            super(itemView);
            nombreAeropuerto = itemView.findViewById(R.id.nombreAeropuerto);
            paisAeropuerto = itemView.findViewById(R.id.paisAeropuerto);
            codigoIcao = itemView.findViewById(R.id.codigoIcao);
            checkBox = itemView.findViewById(R.id.checkBoxSeleccion);
        }
    }

    public Aeropuerto getAeropuertoSeleccionado() {
        return aeropuertoSeleccionado;
    }
}

