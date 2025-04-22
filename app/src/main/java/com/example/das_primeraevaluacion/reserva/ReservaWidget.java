package com.example.das_primeraevaluacion.reserva;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import com.example.das_primeraevaluacion.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReservaWidget extends AppWidgetProvider {

    public static final String ACTION_ACTUALIZAR_WIDGET = "widgetUltimaReserva";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.out.println("RWidget: onUpdate");
        for (int widgetId : appWidgetIds) {
            actualizarWidget(context, appWidgetManager, widgetId);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("RWidget: onRecieve");
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

    /**
     * Actualiza el widget con la ultima reserva del servidor en un hilo
     * @param context Context
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetId int
     */

    public static void actualizarWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        System.out.println("RWidget: actualizarWidget");
        RemoteViews vistas = new RemoteViews(context.getPackageName(), R.layout.widget_reserva);

        Intent intent = new Intent(context, ReservaWidget.class);
        intent.setAction(ACTION_ACTUALIZAR_WIDGET);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        vistas.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        vistas.setTextViewText(R.id.tvContenidoReserva, "Actualizando...");

        appWidgetManager.updateAppWidget(appWidgetId, vistas);

        new Thread(() -> {
            try {
                System.out.println("RWidget: actualizarWidget, dentro del try");
                URL url = new URL("http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/amena028/WEB/reservas/obtenerUltimaReserva.php");
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
                    vistas.setTextViewText(R.id.tvContenidoReserva, textoReserva);
                    appWidgetManager.updateAppWidget(appWidgetId, vistas);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
