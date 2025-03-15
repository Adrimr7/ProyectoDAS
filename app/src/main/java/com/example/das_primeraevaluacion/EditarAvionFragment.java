package com.example.das_primeraevaluacion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.das_primeraevaluacion.bd.AvionDAO;

public class EditarAvionFragment extends DialogFragment {
    private EditText etNombre, etClase, etTarifa, etPasajeros, etAlcance;
    private AvionDAO avionDAO;
    private int avionId;
    private static final int EDITAR_AVION_REQUEST_CODE = 1;

    public interface OnAvionUpdatedListener {
        void onAvionUpdated(Avion avion);
    }

    private OnAvionUpdatedListener miListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAvionUpdatedListener) {
            miListener = (OnAvionUpdatedListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " tiene que implementar OnAvionUpdatedListener");
        }
    }

    /**
     * Se ejecuta al crear la instancia.
     * @param id int
     * @param nombre String
     * @param clase String
     * @param tarifa int
     * @param pasajeros int
     * @param alcance int
     * @return EditarAvionFragment
     */
    public static EditarAvionFragment newInstance(int id, String nombre, String clase, int tarifa, int pasajeros, int alcance) {
        EditarAvionFragment fragment = new EditarAvionFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("nombre", nombre);
        args.putString("clase", clase);
        args.putInt("tarifa", tarifa);
        args.putInt("num_pasajeros", pasajeros);
        args.putInt("alcance_km", alcance);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * Se ejecuta al crear la vista. Se añaden los varios listeners.
     * @return View vista
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_avion, container, false);

        avionDAO = new AvionDAO(getActivity());

        etNombre = view.findViewById(R.id.etNombre);
        etClase = view.findViewById(R.id.etClase);
        etTarifa = view.findViewById(R.id.etTarifa);
        etPasajeros = view.findViewById(R.id.etPasajeros);
        etAlcance = view.findViewById(R.id.etAlcance);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        if (getArguments() != null) {
            avionId = getArguments().getInt("id");
            etNombre.setText(getArguments().getString("nombre"));
            etClase.setText(getArguments().getString("clase"));
            etTarifa.setText(String.valueOf(getArguments().getInt("tarifa")));
            etPasajeros.setText(String.valueOf(getArguments().getInt("num_pasajeros")));
            etAlcance.setText(String.valueOf(getArguments().getInt("alcance_km")));
        }

        btnGuardar.setOnClickListener(v -> {
            System.out.println("FEditarAvion: btnGuardar");
            String nuevoNombre = etNombre.getText().toString().trim();
            String nuevaClase = etClase.getText().toString().trim();
            int nuevaTarifa = Integer.parseInt(etTarifa.getText().toString().trim());
            int nuevosPasajeros = Integer.parseInt(etPasajeros.getText().toString().trim());
            int nuevoAlcance = Integer.parseInt(etAlcance.getText().toString().trim());

            if (TextUtils.isEmpty(nuevoNombre) || TextUtils.isEmpty(nuevaClase)) {
                Toast.makeText(getActivity(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el nuevo avión con los valores actualizados
            Avion avion = new Avion(avionId, nuevoNombre, "", "", nuevoAlcance, nuevosPasajeros, 0, nuevaTarifa, nuevaClase, 0, null);
            int numFilas = avionDAO.actualizarAvion(avion);
            if (numFilas == 0) {
                Toast.makeText(getActivity(), "Error al actualizar el avión", Toast.LENGTH_SHORT).show();
            }
            // Notificar a la actividad que el avión ha sido actualizado
            if (miListener != null) {
                System.out.println("FEditarAvion: btnGuardar: Avion con cambios");
                miListener.onAvionUpdated(avion);
            }
            Toast.makeText(getActivity(), "Avión actualizado", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", avionId);
            resultIntent.putExtra("nombre", nuevoNombre);
            resultIntent.putExtra("clase", nuevaClase);
            resultIntent.putExtra("tarifa", nuevaTarifa);
            resultIntent.putExtra("num_pasajeros", nuevosPasajeros);
            resultIntent.putExtra("alcance_km", nuevoAlcance);
            getActivity().setResult(EDITAR_AVION_REQUEST_CODE, resultIntent);
            System.out.println("FEditarAvion: btnGuardar: Intent hecho" + getActivity());
            dismiss(); // Cerrar el Fragment
        });

        btnCancelar.setOnClickListener(v -> dismiss());

        return view;
    }
}
