package com.example.das_primeraevaluacion.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.das_primeraevaluacion.Avion;
import java.util.ArrayList;

public class AvionDAO {
    private final SQLiteDatabase db;

    /**
     * @param context
     * La constructora de la DAO
     */
    public AvionDAO(Context context) {
        AvionDBHelper dbHelper = new AvionDBHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    /**
     * @param avion El avion a insertar
     * Se inserta el avion en BD
     * @return el Id generado por la BD
     */
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
        return db.insert("aviones", null, values);
    }

    /**
     * Se elimina la BD en su totalidad.
     */
    public void eliminarBD(){
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

    /**
     * Se obtienen todos los aviones de BD
     * @return ArrayList<Avion> listaAviones.
     */
    public ArrayList<Avion> obtenerTodosLosAviones() {
        System.out.println("DAO: obtenerTodosLosAviones");
        ArrayList<Avion> listaAviones = new ArrayList<Avion>();
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

    /**
     * @param avion
     * Se actualiza el contenido del avion en BD
     * @return boolean si se ha actualizado correctamente
     */
    public int actualizarAvion(Avion avion) {
        System.out.println("DAO: actualizarAvion");
        ContentValues values = new ContentValues();
        values.put("nombre", avion.getNombre());
        values.put("clase", avion.getClase());
        values.put("tarifa_base", avion.getTarifaBase());
        values.put("num_pasajeros", avion.getNumPasajeros());
        values.put("alcance_km", avion.getAlcanceKm());

        return db.update("aviones", values, "id = ?", new String[]{String.valueOf(avion.getId())});
    }

    /**
     * @param id
     * Se borra el avion con la id.
     */
    public void borrarAvion(int id){
        db.delete("aviones", "id = ?", new String[]{String.valueOf(id)});
    }
}
