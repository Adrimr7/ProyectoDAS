package com.example.das_primeraevaluacion;

import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;
import java.util.concurrent.Executors;
public class PerfilFragment extends Fragment {

    private String mailUsuario;
    private TextView tvCorreo;
    private ImageView ivPerfil;
    private SharedPreferences prefs;

    /**
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * Se ejecuta al crear la vista. Se añaden los varios listeners que
     * todavia no se usan, se usaran en el futuro.
     * @return View vista
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("RFragment: onCreateView");
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        prefs = requireContext().getSharedPreferences("Perfil", MODE_PRIVATE);
        ivPerfil = view.findViewById(R.id.ivPerfil);
        tvCorreo = view.findViewById(R.id.tvCorreo);
        cargarMail();
        if (!mailUsuario.isEmpty()){
            tvCorreo.setText(mailUsuario);
        }
        else {
            tvCorreo.setText("ERROR");
        }
        ivPerfil.setImageResource(R.mipmap.ic_icono_persona);
        return view;
    }

    private void cargarMail() {
        String email = prefs.getString("email", "");
        if (!email.isEmpty()) {
            mailUsuario = email;
            Log.d("MainActivity", "Correo guardado: " + email);
        } else {
            mailUsuario = "";
            Log.d("MainActivity", "No se ha encontrado un correo guardado.");
        }
    }

}
