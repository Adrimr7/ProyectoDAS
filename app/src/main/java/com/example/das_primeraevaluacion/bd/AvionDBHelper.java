package com.example.das_primeraevaluacion.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AvionDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aviones.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_AVIONES = "aviones";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_FABRICANTE = "fabricante";
    public static final String COLUMN_MODELO = "modelo";
    public static final String COLUMN_ALCANCE = "alcance_km";
    public static final String COLUMN_NUM_PASAJEROS = "num_pasajeros";
    public static final String COLUMN_PERSONAL_CABINA = "personal_cabina";
    public static final String COLUMN_TARIFA_BASE = "tarifa_base";
    public static final String COLUMN_CLASE = "clase";
    public static final String COLUMN_TAMANO = "tamano_m";
    public static final String COLUMN_FACILIDADES = "facilidades";

    public AvionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // comentar esto
        String sqlCrearTabla = "CREATE TABLE " + TABLE_AVIONES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT, " +
                COLUMN_FABRICANTE + " TEXT, " +
                COLUMN_MODELO + " TEXT, " +
                COLUMN_ALCANCE + " INTEGER, " +
                COLUMN_NUM_PASAJEROS + " INTEGER, " +
                COLUMN_PERSONAL_CABINA + " INTEGER, " +
                COLUMN_TARIFA_BASE + " INTEGER, " +
                COLUMN_CLASE + " TEXT, " +
                COLUMN_TAMANO + " INTEGER, " +
                COLUMN_FACILIDADES + " TEXT);";
        db.execSQL(sqlCrearTabla);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AVIONES);
        onCreate(db);
    }
}
