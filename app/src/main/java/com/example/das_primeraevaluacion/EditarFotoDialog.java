package com.example.das_primeraevaluacion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditarFotoDialog extends DialogFragment {

    public interface EditarFotoListener {
        void onElegirGaleria();
        void onSacarFoto();
    }

    private EditarFotoListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof EditarFotoListener) {
            listener = (EditarFotoListener) getParentFragment();
        } else if (context instanceof EditarFotoListener) {
            listener = (EditarFotoListener) context;
        } else {
            throw new RuntimeException("Debe implementar EditarFotoListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        CharSequence[] opciones = {
                requireContext().getString(R.string.elegir_de_galeria),
                requireContext().getString(R.string.sacar_foto)
        };

        builder.setTitle(requireContext().getString(R.string.editar_foto_titulo))
                .setItems(opciones, (dialog, which) -> {
                    if (listener != null) {
                        if (which == 0) {
                            listener.onElegirGaleria();
                        } else if (which == 1) {
                            listener.onSacarFoto();
                        }
                    }
                });

        return builder.create();
    }
}
