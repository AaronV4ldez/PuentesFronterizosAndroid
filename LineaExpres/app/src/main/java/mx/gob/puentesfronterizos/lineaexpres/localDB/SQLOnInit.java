package mx.gob.puentesfronterizos.lineaexpres.localDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Switch;

import androidx.annotation.Nullable;

import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.ui.lineamientos.LineamientosFragment;

public class SQLOnInit extends dbHelper {
    private static final String TAG = "SQLOnInit";
    Context context;
    public SQLOnInit(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public void NoteHelperOnInit() {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            db.insert(NoteHelper, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "NoteHelperOnInit: ", e);
        }
    }

    public void UserSetIdOnInit() {
        Cursor c = null;
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            String query = "select count(*) from "+TABLE_USER+" where id = 1";
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                if (c.getInt(0) == 0) {
                    ContentValues values = new ContentValues();
                    values.put("id", 1);
                    db.insert(TABLE_USER, null, values);
                    db.close();
                }
            }

        }catch (RuntimeException e){
            Log.e(TAG, "UserSetIdOnInit: ", e);
        }
    }

    public void setCardIDIfNotExists() {
        try {
            int id = 0;
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            Cursor getNoteID = db.rawQuery("SELECT id FROM " + Payment + " WHERE id = 1 ",null);
            if (getNoteID.moveToNext()) {
                id = getNoteID.getInt(0);
                System.out.println(id);
            }else {
                try {
                    values.put("id", 1);
                    db.insert(Payment, null, values);
                    db.close();
                }catch (RuntimeException e){
                    Log.e(TAG, "LayoutSetIdOnInit: ", e);
                }
            }
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "insert Card Inpout: ", e);
        }
    }

    public void CarSelecterSetIdOnInit() {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("CarSelected", 1);
            db.insert(CarSelected, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "LayoutSetIdOnInit: ", e);
        }
    }
    public void TramitesSetIdOnInit() {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            db.insert(uriTramites, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "LayoutSetIdOnInit: ", e);
        }
    }

    //Set for Services
    public void SubServiceHelperOnInit() {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            db.insert(SubServHelper, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "SubServiceHelperOnInit: ", e);
        }
    }

    public void InsertTramitesOnInit() {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            db.insert(uriTramites, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertTramites: ", e);
        }
    }



    public void cleanTramites() {
        dbHelper bCapturas = new dbHelper(context);
        SQLiteDatabase db = bCapturas.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + uriTramites);

        }catch (Exception e) {
            Log.e(TAG, "cleanOnInit: ", e);
        }
    }

    public void cleanFix() {
        dbHelper bCapturas = new dbHelper(context);
        SQLiteDatabase db = bCapturas.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + FixTramite);

        }catch (Exception e) {
            Log.e(TAG, "cleanOnInit: ", e);
        }
    }

    public void InsertTramiteFixOnInit() {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            db.insert(FixTramite, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertTramites: ", e);
        }
    }

    public void InsertCitasOnInit() {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            db.insert(CitasList, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertTramites: ", e);
        }
    }

    public void cleanCitas() {
        dbHelper bCapturas = new dbHelper(context);
        SQLiteDatabase db = bCapturas.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + CitasList);

        }catch (Exception e) {
            Log.e(TAG, "cleanOnInit: ", e);
        }
    }


    public void cleanOnInit() {
        dbHelper bCapturas = new dbHelper(context);
        SQLiteDatabase db = bCapturas.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + TABLE_NAME);
            db.execSQL("DELETE FROM " + NoteHelper);
            db.execSQL("DELETE FROM " + ServiciosHelper);
            db.execSQL("DELETE FROM " + SubServHelper);
            db.execSQL("DELETE FROM " + QuienesSomosHelper);
            db.execSQL("DELETE FROM " + ObjetivoHelper);
            db.execSQL("DELETE FROM " + CarSelected);
            db.execSQL("DELETE FROM " + MisionHelper);
            db.execSQL("DELETE FROM " + VisionHelper);
            db.execSQL("DELETE FROM " + PrivacyHelper);
            db.execSQL("DELETE FROM " + RatesHelper);
            db.execSQL("DELETE FROM " + LineamientosHelper);
            db.execSQL("DELETE FROM " + uriTramites);
            db.execSQL("DELETE FROM " + CitasList);
            db.execSQL("DELETE FROM " + FixTramite);

        }catch (Exception e) {
            Log.e(TAG, "cleanOnInit: ", e);
        }
    }

    public void cleanNotes() {
        dbHelper bCapturas = new dbHelper(context);
        SQLiteDatabase db = bCapturas.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + TABLE_NAME);
            db.execSQL("DELETE FROM " + NoteHelper);
            db.execSQL("DELETE FROM " + ServiciosHelper);
            db.execSQL("DELETE FROM " + SubServHelper);
            db.execSQL("DELETE FROM " + QuienesSomosHelper);
            db.execSQL("DELETE FROM " + ObjetivoHelper);
            db.execSQL("DELETE FROM " + CarSelected);
            db.execSQL("DELETE FROM " + MisionHelper);
            db.execSQL("DELETE FROM " + VisionHelper);
            db.execSQL("DELETE FROM " + PrivacyHelper);
            db.execSQL("DELETE FROM " + RatesHelper);
            db.execSQL("DELETE FROM " + LineamientosHelper);
        }catch (Exception e) {
            Log.e(TAG, "cleanOnInit: ", e);
        }
    }

    public void cleanOnFire(String Fired) {
        dbHelper bCapturas = new dbHelper(context);
        SQLiteDatabase db = bCapturas.getWritableDatabase();
        try {
            if (Fired.contains("ReporteDePuentes")) {
                db.execSQL("DELETE FROM " + ReporteDePuentes);
            }
        }catch (Exception e) {
            Log.e(TAG, "cleanOnInit: ", e);
        }
    }


}
