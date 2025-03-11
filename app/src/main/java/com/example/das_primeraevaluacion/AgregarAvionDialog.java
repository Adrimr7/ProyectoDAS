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
        EditText etClase = view.findViewById(R.id.etClase);
        EditText etTarifa = view.findViewById(R.id.etTarifa);
        Button btnCancelar = view.findViewById(R.id.btnAgregar);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);

        btnCancelar.setOnClickListener(v -> {
            System.out.println("Cancelando");
            dismiss();
        });

        btnGuardar.setOnClickListener(v -> {
            System.out.println("Añadiendo nuevo avión");
            String nombre = etNombre.getText().toString().trim();
            String clase = etClase.getText().toString().trim();
            String tarifaStr = etTarifa.getText().toString().trim();

            boolean datosValidos = true;

            // Verificar si los campos están vacíos
            if (nombre.isEmpty()) {
                etNombre.setError("Este campo es obligatorio");
                datosValidos = false;
            }
            if (clase.isEmpty()) {
                etClase.setError("Este campo es obligatorio");
                datosValidos = false;
            }
            if (tarifaStr.isEmpty()) {
                etTarifa.setError("Este campo es obligatorio");
                datosValidos = false;
            }

            int tarifa = 0;
            if (!tarifaStr.isEmpty()) {
                try {
                    tarifa = Integer.parseInt(tarifaStr);
                    if (tarifa < 0) { // Asegurar que la tarifa no sea negativa
                        etTarifa.setError("La tarifa debe ser un número positivo");
                        datosValidos = false;
                    }
                } catch (NumberFormatException e) {
                    etTarifa.setError("Ingrese un número válido");
                    datosValidos = false;
                }
            }

            if (datosValidos) {
                listener.onAvionAgregado(nombre, clase, tarifa);
                dismiss();
            }
        });


        return builder.create();
    }
}
