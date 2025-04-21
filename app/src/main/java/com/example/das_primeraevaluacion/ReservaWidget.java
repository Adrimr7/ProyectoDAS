package com.example.das_primeraevaluacion;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReservaWidget extends AppWidgetProvider {

    public static final String ACTION_ACTUALIZAR_WIDGET = "widgetUltimaReserva";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            actualizarWidget(context, appWidgetManager, widgetId);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_ACTUALIZAR_WIDGET.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName nombreWidget = new ComponentName(context, ReservaWidget.class);
            int[] ids = appWidgetManager.getAppWidgetIds(nombreWidget);

            for (int id : ids) {
                actualizarWidget(context, appWidgetManager, id);
            }
        }
    }

    public static void actualizarWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_reserva);

        // Llamada HTTP sencilla
        new Thread(() -> {
            try {
                URL url = new URL("http://http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/reservas/obtenerUltimaReserva.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(4000);
                conn.setReadTimeout(4000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder resultado = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    resultado.append(linea);
                }

                JSONObject json = new JSONObject(resultado.toString());
                JSONObject reserva = json.optJSONObject("reserva");

                String textoReserva;
                if (reserva != null) {
                    String avion = reserva.getString("avion_nombre");
                    String origen = reserva.getString("origen_nombre");
                    String destino = reserva.getString("destino_nombre");
                    String fecha = reserva.getString("fecha_reserva");

                    textoReserva = avion + "\n" + origen + " ➡ " + destino + "\n" + fecha;
                }
                else {
                    textoReserva = "No hay reservas";
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    views.setTextViewText(R.id.tvContenidoReserva, textoReserva);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
