package com.example.das_primeraevaluacion;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AgregarAvionDialog.OnAvionAgregadoListener, NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private long tiempoPresionadoAtras = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("MainActivity: onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permisosNotificaciones();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "es"); // Español por defecto
        setIdioma(language);
        actualizarIdiomaMenu();
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_add || id == R.id.action_add) {
                mostrarDialogoAgregarAvion();
                if (navigationView.getCheckedItem().getItemId() != R.id.nav_reservas) {
                    navigationView.setCheckedItem(R.id.nav_home);
                }
            }
            else if (id == R.id.nav_reset) {
                reiniciarBD();
                cambiarFragement(new AvionesFragment());
            }
            else if (id == R.id.btnCambiarIdioma) {
                setIdioma(getIdiomaACambiar());
            }
            else if (id == R.id.nav_home) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (!(fragment instanceof AvionesFragment)) {
                    replaceFragment(new AvionesFragment());
                }
            }
            else if (id == R.id.nav_reservas) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (!(fragment instanceof ReservasFragment)) {
                    replaceFragment(new ReservasFragment());
                }
            }
            drawerLayout.closeDrawers();
            return true;
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AvionesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragmentActual = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (!(fragmentActual instanceof AvionesFragment)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AvionesFragment())
                    .commit();
        }
        else {
            if (tiempoPresionadoAtras + 3000 > System.currentTimeMillis()) {
                super.onBackPressed();
            }
            else {
                Toast.makeText(this, R.string.atras_2, Toast.LENGTH_SHORT).show();
            }
            tiempoPresionadoAtras = System.currentTimeMillis();
        }
    }

    public void setIdioma(String codigoIdioma) {
        System.out.println("MainActivity: setIdioma");
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String idiomaActual = prefs.getString("My_Lang", "es");

        if (!idiomaActual.equals(codigoIdioma)) {
            Locale locale = new Locale(codigoIdioma);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("My_Lang", codigoIdioma);
            editor.apply();
            if (findViewById(R.id.tvTitulo)== null) {
                TextView viewById3 = findViewById(R.id.tvTituloReservas);
                viewById3.setText(R.string.mis_reservas);
                TextView viewById4 = findViewById(R.id.tvSiguiente);
                viewById4.setText(R.string.texto_siguiente);
            }
            else {
                TextView viewById = findViewById(R.id.tvTitulo);
                viewById.setText(R.string.app_name);
            }
            Toolbar viewById2 = findViewById(R.id.toolbar);
            viewById2.setTitle(R.string.app_name);

            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("MainActivity: onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem cambiarIdioma = menu.findItem(R.id.btnCambiarIdioma);
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String idiomaActual = prefs.getString("My_Lang", "es");
        cambiarIdioma.setTitle(idiomaActual);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        System.out.println("MainActivity: onOptionsItemSelected");
        if (item.getItemId() == R.id.action_add) {
            mostrarDialogoAgregarAvion();
            return true;
        }
        else if (item.getItemId() == R.id.btnCambiarIdioma) {
            setIdioma(getIdiomaACambiar());
            invalidateOptionsMenu();
            actualizarIdiomaMenu();
            pasarGarbageCollector();
            return true;
        }
        else if (item.getItemId() == R.id.nav_reset) {
            reiniciarBD();
            navigationView.setCheckedItem(R.id.nav_reservas);
            replaceFragment(new ReservasFragment());
            pasarGarbageCollector();
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void actualizarIdiomaMenu() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();

        MenuItem homeItem = menu.findItem(R.id.nav_home);
        homeItem.setTitle(R.string.menu_home);

        MenuItem reservasItem = menu.findItem(R.id.nav_reservas);
        reservasItem.setTitle(R.string.menu_reservas);

        MenuItem agregarItem = menu.findItem(R.id.nav_add);
        agregarItem.setTitle(R.string.menu_add);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("MainActivity: onNavigationItemSelected");
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment(new AvionesFragment());
        }
        else if (id == R.id.nav_reservas) {
            replaceFragment(new ReservasFragment());
        }
        else if (id == R.id.nav_reset){
            reiniciarBD();
            cambiarFragement(new AvionesFragment());
        }
        pasarGarbageCollector();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getIdiomaACambiar() {
        System.out.println("MainActivity: getIdiomaACambiar");
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String idiomaActual = prefs.getString("My_Lang", "es");
        if (idiomaActual.equals("en")){
            return "es";
        }
        return "en";
    }

    public void reiniciarBD() {
        System.out.println("MainActivity: reiniciarBD");
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentById(R.id.fragment_container).getClass().equals(ReservasFragment.class)) {
            Toast.makeText(this, getString(R.string.dialog_no_implementado), Toast.LENGTH_SHORT).show();
        }
        else {
            AvionesFragment fragment = (AvionesFragment) fragmentManager.findFragmentById(R.id.fragment_container);
            if (fragment != null) {
                Executors.newSingleThreadExecutor().execute(fragment::resetearBD);
                pasarGarbageCollector();
                Toast.makeText(this, getString(R.string.dialog_reset_db), Toast.LENGTH_SHORT).show();
            }
            else {
                Log.e("MainActivity", "Fragment no encontrado");
            }
        }

    }

    private void mostrarDialogoAgregarAvion() {
        System.out.println("MainActivity: mostrarDialogoAgregarAvion");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_agregar_avion, null);
        builder.setView(view);

        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etClase = view.findViewById(R.id.etClase);
        EditText etTarifa = view.findViewById(R.id.etTarifa);
        EditText etPasajeros = view.findViewById(R.id.etPasajeros);
        EditText etAlcance = view.findViewById(R.id.etAlcance);

        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnAgregar);

        AlertDialog dialog = builder.create();

        btnCancelar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String clase = etClase.getText().toString().trim();
            String tarifaStr = etTarifa.getText().toString().trim();

            String numPasajeros = etPasajeros.getText().toString().trim();
            String alcance = etAlcance.getText().toString().trim();

            if (nombre.isEmpty() || clase.isEmpty() || tarifaStr.isEmpty() || numPasajeros.isEmpty() || alcance.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_campos), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int tarifa = Integer.parseInt(tarifaStr);
                int pasajerosReal = Integer.parseInt(numPasajeros);
                int alcanceReal = Integer.parseInt(alcance);

                if (tarifa < 0) throw new NumberFormatException();
                if (pasajerosReal < 0) throw new NumberFormatException();
                if (alcanceReal < 0) throw new NumberFormatException();
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.findFragmentById(R.id.fragment_container).getClass().equals(ReservasFragment.class)) {
                    navigationView.setCheckedItem(R.id.nav_reservas);
                    Toast.makeText(this, getString(R.string.dialog_no_implementado), Toast.LENGTH_SHORT).show();
                }
                else {
                    navigationView.setCheckedItem(R.id.nav_home);
                    AvionesFragment fragment = (AvionesFragment) fragmentManager.findFragmentById(R.id.fragment_container);
                    fragment.agregarAvion(nombre, clase, tarifa, pasajerosReal, alcanceReal);
                }
                dialog.dismiss();
            }
            catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.error_numero_valido), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    private void permisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        crearCanalNotificacion();
    }

    @Override
    public void onAvionAgregado(String nombre, String clase, int tarifa, int numPasajeros, int alcance) {
        System.out.println("MainActivity: onAvionAgregado");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AvionesFragment fragment = (AvionesFragment) fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment != null) {
            fragment.onAvionAgregado(nombre, clase, tarifa, numPasajeros, alcance);
        }
        else {
            System.out.println("Error: Fragment no encontrado");
        }
    }

    // Este método se llama cuando volvemos de la actividad de detalles (editar)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("MainActivity: onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        AvionesFragment fragment = (AvionesFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.agregarAvion(requestCode, resultCode, data);
        }
        else {
            System.out.println("Error: Fragment no encontrado");
        }
    }

    @Override
    protected void onResume() {
        System.out.println("MainActivity: onResume");
        super.onResume();
        pasarGarbageCollector();
        cambiarFragement(new AvionesFragment());
    }

    private void crearCanalNotificacion() {
        String channelId = "aviones_channel";
        CharSequence name = "Aviones";
        String description = getString(R.string.notif_aviones);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void replaceFragment(Fragment fragment) {
        System.out.println("MainActivity: replaceFragment");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragment != null && !fragment.getClass().getName().equals(getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getName())) {
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }
    private void cambiarFragement(Fragment fragment){
        System.out.println("MainActivity: cambiarFragement");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
    public void pasarGarbageCollector(){
        Runtime garbage = Runtime.getRuntime();
        garbage.gc();
    }

}