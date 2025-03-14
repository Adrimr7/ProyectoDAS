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
        void onAvionAgregado(String nombre, String fabricante, int tarifa, int numPasajeros, int alcance);
    }

    private OnAvionAgregadoListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAvionAgregadoListener) {
            listener = (OnAvionAgregadoListener) context;
        }
        else {
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
        EditText etPasajeros = view.findViewById(R.id.etPasajeros);
        EditText etAlcance = view.findViewById(R.id.etAlcance);

        Button btnCancelar = view.findViewById(R.id.btnAgregar);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);

        if (savedInstanceState != null) {
            etNombre.setText(savedInstanceState.getString("nombre", ""));
            etClase.setText(savedInstanceState.getString("clase", ""));
            etTarifa.setText(savedInstanceState.getString("tarifa", ""));
            etPasajeros.setText(savedInstanceState.getString("numPasajeros", ""));
            etAlcance.setText(savedInstanceState.getString("alcance", ""));
        }

        btnCancelar.setOnClickListener(v -> {
            System.out.println("Cancelando");
            dismiss();
        });

        btnGuardar.setOnClickListener(v -> {
            System.out.println("Añadiendo nuevo avión");
            String nombre = etNombre.getText().toString().trim();
            String clase = etClase.getText().toString().trim();
            String tarifaStr = etTarifa.getText().toString().trim();

            String numPasajeros = etPasajeros.getText().toString().trim();
            String alcance = etAlcance.getText().toString().trim();

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
            if (numPasajeros.isEmpty()) {
                etPasajeros.setError("Este campo es obligatorio");
                datosValidos = false;
            }
            if (alcance.isEmpty()) {
                etAlcance.setError("Este campo es obligatorio");
                datosValidos = false;
            }
            // valores por defecto
            int tarifa = 500, pasajerosReal = 1, alcanceReal = 1000;

            if (!tarifaStr.isEmpty()) {
                try {
                    tarifa = Integer.parseInt(tarifaStr);
                    pasajerosReal = Integer.parseInt(numPasajeros);
                    alcanceReal = Integer.parseInt(alcance);
                    if (tarifa < 0) {
                        etTarifa.setError("La tarifa debe ser un número positivo.");
                        datosValidos = false;
                    }
                    if (pasajerosReal < 0) {
                        etPasajeros.setError("Los pasajeros tienen que ser positivos.");
                        datosValidos = false;
                    }
                    if (pasajerosReal > 900) {
                        etPasajeros.setError("Numero incorrecto de pasajeros.");
                        datosValidos = false;
                    }
                    if (alcanceReal < 0) {
                        etAlcance.setError("El alcance tiene que ser un número positivo.");
                        datosValidos = false;
                    }
                }
                catch (NumberFormatException e) {
                    etTarifa.setError("Ingrese un número válido");
                    datosValidos = false;
                }
            }

            if (datosValidos) {
                listener.onAvionAgregado(nombre, clase, tarifa, pasajerosReal, alcanceReal);
                dismiss();
            }
        });


        return builder.create();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText etNombre = getView().findViewById(R.id.etNombre);
        EditText etClase = getView().findViewById(R.id.etClase);
        EditText etTarifa = getView().findViewById(R.id.etTarifa);
        EditText etPasajeros = getView().findViewById(R.id.etPasajeros);
        EditText etAlcance = getView().findViewById(R.id.etAlcance);

        outState.putString("nombre", etNombre.getText().toString());
        outState.putString("clase", etClase.getText().toString());
        outState.putString("tarifa", etTarifa.getText().toString());
        outState.putString("numPasajeros", etPasajeros.getText().toString());
        outState.putString("alcance", etAlcance.getText().toString());
    }

}
