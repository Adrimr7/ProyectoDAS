package com.example.das_primeraevaluacion;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class AgregarAvionDialog extends DialogFragment {

    public interface OnAvionAgregadoListener {
        void onAvionAgregado(String nombre, String fabricante, int tarifa);
    }

    private OnAvionAgregadoListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAvionAgregadoListener) {
            listener = (OnAvionAgregadoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar OnAvionAgregadoListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_agregar_avion, null);
        builder.setView(view);

        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etFabricante = view.findViewById(R.id.etFabricante);
        EditText etTarifa = view.findViewById(R.id.etTarifa);
        Button btnCancelar = view.findViewById(R.id.btnAgregar);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String fabricante = etFabricante.getText().toString().trim();
            String tarifaStr = etTarifa.getText().toString().trim();

            if (!nombre.isEmpty() && !fabricante.isEmpty() && !tarifaStr.isEmpty()) {
                int tarifa = Integer.parseInt(tarifaStr);
                listener.onAvionAgregado(nombre, fabricante, tarifa);
                dismiss();
            }
        });

        return builder.create();
    }
}
