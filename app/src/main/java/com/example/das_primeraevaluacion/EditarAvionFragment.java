package com.example.das_primeraevaluacion;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
        // linea para evitar cambiar el nombre, que es la clave.
        etNombre.setFocusable(false);
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
                Toast.makeText(getActivity(), R.string.campos_obligatorios, Toast.LENGTH_SHORT).show();
                return;
            }

            Avion avion = new Avion(avionId, nuevoNombre, "", "", nuevoAlcance, nuevosPasajeros, 0, nuevaTarifa, nuevaClase, 0, null);
            int numFilas = avionDAO.actualizarAvion(avion);
            if (numFilas == 0) {
                Toast.makeText(getActivity(), R.string.error_act_avion, Toast.LENGTH_SHORT).show();
            }
            else {
                // actualizar el avion en BD.
                new Thread(() -> {
                    try {
                        URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/editarAvion.php");

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(2000);
                        conn.setReadTimeout(2000);

                        String postData = "nombre=" + URLEncoder.encode(nuevoNombre, "UTF-8") +
                                "&alcance_km=" + nuevoAlcance +
                                "&num_pasajeros=" + nuevosPasajeros +
                                "&tarifa_base=" + nuevaTarifa +
                                "&clase=" + URLEncoder.encode(nuevaClase, "UTF-8");

                        System.out.println("Agregando avion: " + postData);
                        conn.getOutputStream().write(postData.getBytes("UTF-8"));

                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            System.out.println("Avión editado correctamente");
                        } else {
                            System.out.println("Error al editar avión a la BD remota: " + responseCode);
                        }

                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            // notificar a la actividad que el avión ha sido actualizado
            if (miListener != null) {
                System.out.println("FEditarAvion: btnGuardar: Avion con cambios");
                miListener.onAvionUpdated(avion);
            }
            Toast.makeText(getActivity(), R.string.avion_actualizado, Toast.LENGTH_SHORT).show();

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
