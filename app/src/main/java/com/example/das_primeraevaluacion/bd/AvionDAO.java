package com.example.das_primeraevaluacion.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.das_primeraevaluacion.Avion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvionDAO {
    private final SQLiteDatabase db;

    public AvionDAO(Context context) {
        AvionDBHelper dbHelper = new AvionDBHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    // Insertar avión
    public long insertarAvion(Avion avion) {

        ContentValues values = new ContentValues();
        values.put("nombre", avion.getNombre());
        values.put("fabricante", avion.getFabricante());
        values.put("modelo", avion.getModelo());
        values.put("alcance_km", avion.getAlcanceKm());
        values.put("num_pasajeros", avion.getNumPasajeros());
        values.put("personal_cabina", avion.getPersonalCabina());
        values.put("tarifa_base", avion.getTarifaBase());
        values.put("clase", avion.getClase());
        values.put("tamano_m", avion.getTamanoM());

        // Insertar el avión y devolver el ID generado
        // System.out.println(avion.getNombre() + avion.getFabricante());
        return db.insert("aviones", null, values);
    }

    public void cerrarBD(){
        db.close();
    }

    public void eliminarBD(){
        System.out.println("eliminarBD");
        db.execSQL("DROP TABLE IF EXISTS " + AvionDBHelper.TABLE_AVIONES);
        String sqlCrearTabla = "CREATE TABLE " + AvionDBHelper.TABLE_AVIONES + " (" +
                AvionDBHelper.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AvionDBHelper.COLUMN_NOMBRE + " TEXT, " +
                AvionDBHelper.COLUMN_FABRICANTE + " TEXT, " +
                AvionDBHelper.COLUMN_MODELO + " TEXT, " +
                AvionDBHelper.COLUMN_ALCANCE + " INTEGER, " +
                AvionDBHelper.COLUMN_NUM_PASAJEROS + " INTEGER, " +
                AvionDBHelper.COLUMN_PERSONAL_CABINA + " INTEGER, " +
                AvionDBHelper.COLUMN_TARIFA_BASE + " INTEGER, " +
                AvionDBHelper.COLUMN_CLASE + " TEXT, " +
                AvionDBHelper.COLUMN_TAMANO + " INTEGER, " +
                AvionDBHelper.COLUMN_FACILIDADES + " TEXT);";
        db.execSQL(sqlCrearTabla);
    }

    // Obtener todos los aviones
    public List<Avion> obtenerTodosLosAviones() {
        List<Avion> listaAviones = new ArrayList<Avion>();
        String query = "SELECT * FROM " + AvionDBHelper.TABLE_AVIONES;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_NOMBRE));
                String fabricante = cursor.getString(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_FABRICANTE));
                String modelo = cursor.getString(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_MODELO));
                int alcance = cursor.getInt(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_ALCANCE));
                int numPasajeros = cursor.getInt(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_NUM_PASAJEROS));
                int personalCabina = cursor.getInt(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_PERSONAL_CABINA));
                int tarifaBase = cursor.getInt(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_TARIFA_BASE));
                String clase = cursor.getString(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_CLASE));
                int tamano = cursor.getInt(cursor.getColumnIndexOrThrow(AvionDBHelper.COLUMN_TAMANO));

                listaAviones.add(new Avion(id, nombre, fabricante, modelo, alcance, numPasajeros, personalCabina, tarifaBase, clase, tamano, new ArrayList<>()));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return listaAviones;
    }
    public int actualizarAvion(Avion avion) {
        ContentValues values = new ContentValues();
        values.put("nombre", avion.getNombre());
        values.put("clase", avion.getClase());
        values.put("tarifa_base", avion.getTarifaBase());
        values.put("num_pasajeros", avion.getNumPasajeros());
        values.put("alcance_km", avion.getAlcanceKm());

        return db.update("aviones", values, "id = ?", new String[]{String.valueOf(avion.getId())});
    }
}
