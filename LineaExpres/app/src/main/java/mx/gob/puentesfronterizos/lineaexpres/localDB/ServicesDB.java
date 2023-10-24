package mx.gob.puentesfronterizos.lineaexpres.localDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ServicesDB extends dbHelper {
    private static final String TAG = "ServicesDB";
    Context context;

    public ServicesDB(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    //Actualizar al abrir Servicio
    public void SubServOnClick(String i) {
        try {
            int id = Integer.parseInt(i);
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Service", id);
            db.update(SubServHelper, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "SubServOnClick: ", e);
        }
    }
    //Set previous serv
    public void PrevServOnClick(String i) {
        try {
            int id = Integer.parseInt(i);
            int idPrev = 0;
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            Cursor getServID = db.rawQuery("SELECT id FROM " + ServiciosHelper + " WHERE id < "+id+" ORDER BY id DESC LIMIT 1 ",null);
            while(getServID.moveToNext()) {
                idPrev = getServID.getInt(0);
            }

            values.put("PrevServ", idPrev);
            db.update(SubServHelper, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "PrevNoteOnClick: ", e);
        }
    }
    //set next service
    public void NextServOnClick(String i) {
        try {
            int id = Integer.parseInt(i);
            int idNext = 0;
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            Cursor getServID = db.rawQuery("SELECT id FROM " + ServiciosHelper + " WHERE id > "+id+" ORDER BY id LIMIT 1 ",null);
            while(getServID.moveToNext()) {
                idNext = getServID.getInt(0);
            }

            values.put("NextServ", idNext);
            db.update(SubServHelper, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "NextNoteOnClick: ", e);
        }
    }

    public ArrayList<Integer> getSubServIDs() {
        ArrayList<Integer> ServIds = new ArrayList<>();
        int id = 0;
        int idPrev = 0;
        int idNext = 0;
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getServID = db.rawQuery("SELECT Service, PrevServ, NextServ FROM " + SubServHelper,null);
            while(getServID.moveToNext()) {
                id = getServID.getInt(0);
                idPrev = getServID.getInt(1);
                idNext = getServID.getInt(2);
                ServIds.add(id);
                ServIds.add(idPrev);
                ServIds.add(idNext);
            }getServID.close();
            return ServIds;
        }catch (RuntimeException e) {
            Log.e(TAG, "getSubServIDs: ", e);
            return ServIds;
        }
    }

    //Getting service by id
    public ArrayList<String> getService(int id) {
        ArrayList<String> ServResult = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getServ = db.rawQuery("SELECT Titulo, Cuerpo, Imagen FROM " + ServiciosHelper + " WHERE id = '"+id+"' AND Status = 'publish' ",null);
            while(getServ.moveToNext()) {
                String Titulo = getServ.getString(0);
                String Cuerpo = getServ.getString(1);
                String Imagen = getServ.getString(2);
                ServResult.add(Titulo);
                ServResult.add(Cuerpo);
                ServResult.add(Imagen);
            }getServ.close();
            return ServResult;
        }catch (RuntimeException e) {
            Log.e(TAG, "getService: ", e);
            return ServResult;
        }
    }

    //Getting next service by id
    public String getTitleServ(int id) {
        String ServResult = null;
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getServ = db.rawQuery("SELECT Titulo FROM " + ServiciosHelper + " WHERE id = '"+id+"' ",null);
            while(getServ.moveToNext()) {
                ServResult = getServ.getString(0);
            }getServ.close();
            return ServResult;
        }catch (RuntimeException e) {
            Log.e(TAG, "getTitleServ: ", e);
            return ServResult;
        }
    }

}
