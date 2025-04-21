package com.example.das_primeraevaluacion;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment implements EditarFotoDialog.EditarFotoListener{

    private String mailUsuario;
    private TextView tvCorreo;
    private ImageView ivPerfil;
    private SharedPreferences prefs;
    private ImageButton btnEditarFoto;
    private ActivityResultLauncher<String> permisoGaleriaLauncher;
    private ActivityResultLauncher<String> permisoCamaraLauncher;
    private ActivityResultLauncher<Intent> resultadoGaleriaLauncher;
    private ActivityResultLauncher<Intent> resultadoCamaraLauncher;

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
            obtenerFotoDesdeServidor();
        }
        else {
            tvCorreo.setText("ERROR");
        }
        // llamar a ver si hay foto, en caso de que haya foto, descargarla. Si no la hay, no hacer nada.


        // cargar la foto en local y en la nube?
        // hace falta un PHP para cargar la foto en la BD
        // editar la BD para cargar una foto de perfil?

        return view;
    }

    private void obtenerFotoDesdeServidor() {
        String url = "http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/fotos/obtenerFoto.php?email=http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/fotos/obtenerFoto.php";

        new Thread(() -> {
            try {
                URL servidor = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) servidor.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String datos = "email=" + URLEncoder.encode(mailUsuario, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(datos.getBytes());
                os.flush();
                os.close();

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder resultado = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    resultado.append(linea);
                }

                reader.close();
                conn.disconnect();

                JSONObject jsonResponse = new JSONObject(resultado.toString());
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    String fotoBase64 = jsonResponse.getString("foto");

                    byte[] decodedBytes = Base64.decode(fotoBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                    new Handler(Looper.getMainLooper()).post(() -> ivPerfil.setImageBitmap(bitmap));
                } else {
                    String message = jsonResponse.getString("message");
                    System.out.println("PFragment: obtenerFotoDesdeServidor, Error: " + message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivPerfil = view.findViewById(R.id.ivPerfil);
        btnEditarFoto = view.findViewById(R.id.btnEditarFoto);
        // cargar foto tambien!
        permisoGaleriaLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    System.out.println("PFragment: onViewCreated, permisoGaleriaLauncher");
                    if (isGranted) {
                        abrirGaleria();
                    } else {
                        Toast.makeText(getContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        permisoCamaraLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    System.out.println("PFragment: onViewCreated, permisoCamaraLauncher");
                    if (isGranted) {
                        abrirCamara();
                    } else {
                        Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        resultadoGaleriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    System.out.println("PFragment: onViewCreated, resultadoGaleriaLauncher");
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        ivPerfil.setImageURI(uri);
                        // llamada a la BD para settear ahi la foto.
                        try {
                            ImageDecoder.Source origen = ImageDecoder.createSource(requireContext().getContentResolver(), uri);
                            Bitmap bitmap = ImageDecoder.decodeBitmap(origen);
                            subirFotoEnBase64(bitmap);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        //ivPerfil.setImageResource(R.mipmap.ic_icono_persona);
                    }
                }
        );

        resultadoCamaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    System.out.println("PFragment: onViewCreated, resultadoCamaraLauncher");
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap foto = (Bitmap) result.getData().getExtras().get("data");
                        ivPerfil.setImageBitmap(foto);
                        // llamada a la BD para settear ahi la foto.
                        subirFotoEnBase64(foto);
                    }
                    else {
                        //ivPerfil.setImageResource(R.mipmap.ic_icono_persona);
                    }
                }
        );

        btnEditarFoto.setOnClickListener(v -> {
            EditarFotoDialog dialog = new EditarFotoDialog();
            dialog.show(getChildFragmentManager(), "EditarFotoDialog");
        });

    }

    private void cargarMail() {
        String email = prefs.getString("email", "");
        if (!email.isEmpty()) {
            mailUsuario = email;
            System.out.println("PFragment: cargarMail. Correo guardado: " + email);
        } else {
            mailUsuario = "";
            System.out.println("PFragment: cargarMail. No se ha encontrado el correo");
        }
    }

    @Override
    public void onElegirGaleria() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permisoGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
            else {
                abrirGaleria();
            }
        }
    }

    public void onSacarFoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        resultadoCamaraLauncher.launch(intent);
    }


    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultadoGaleriaLauncher.launch(intent);
    }

    private String convertirImagenABase64(Bitmap bitmap) {
        if (bitmap == null) {
            return "";
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] imagenBytes = outputStream.toByteArray();
        return Base64.encodeToString(imagenBytes, Base64.NO_WRAP);
    }

    private void subirFotoEnBase64(Bitmap foto) {
        System.out.println("PFragment: subirFotoEnBase64");
        System.out.println("Bitmap es nulo: " + (foto == null));

        String fotoBase64 = convertirImagenABase64(foto);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/fotos/anadirFoto.php",
                respuesta -> {
                    try {
                        JSONObject respuestaJSON = new JSONObject(respuesta);
                        String mensaje = respuestaJSON.getString("message");
                        System.out.println("PFragment: subirFotoEnBase64, " + mensaje);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", mailUsuario);
                params.put("foto", fotoBase64);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
