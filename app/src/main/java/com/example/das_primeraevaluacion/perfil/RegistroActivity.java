package com.example.das_primeraevaluacion.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.das_primeraevaluacion.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

public class RegistroActivity extends AppCompatActivity {

    EditText etEmail, etContrasena;
    Button btnRegistro;
    TextView tvALogin;

    /**
     * Inicializa el registro, listeners, campos de entrada y la nav. al login
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etEmail = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etContrasena);
        btnRegistro = findViewById(R.id.btnRegistro);
        tvALogin = findViewById(R.id.tvALogin);

        btnRegistro.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etContrasena.getText().toString();
            register(email, password);
        });

        tvALogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Hace el registro mediante el php, procesa el JSON y envia al usuario
     * a LoginActivity para que se haga login si el registro ha ido bien.
     * @param email String
     * @param password String
     */
    private void register(String email, String password) {
        new Thread(() -> {
            try {
                URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/registro.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String linea;

                while ((linea = reader.readLine()) != null) {
                    sb.append(linea);
                }

                JSONObject json = new JSONObject(sb.toString());
                boolean ok = json.getBoolean("success");

                runOnUiThread(() -> {

                    if (ok) {
                        Toast.makeText(RegistroActivity.this, R.string.registro_correcto, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(RegistroActivity.this, R.string.error_registro, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(RegistroActivity.this, R.string.error_conexion, Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

