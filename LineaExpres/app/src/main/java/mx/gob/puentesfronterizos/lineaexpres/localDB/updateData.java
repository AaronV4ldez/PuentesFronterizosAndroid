package mx.gob.puentesfronterizos.lineaexpres.localDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class updateData extends dbHelper {
    private static final String TAG = "updateData";
    Context context;
    public updateData(@Nullable Context context) {
        super(context);
        this.context = context;
    }


    public void updateCarousel(String id, String Titulo, String Cuerpo, String Imagen) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("Titulo", Titulo);
            values.put("Cuerpo", Cuerpo);
            values.put("Imagen", Imagen);
            db.insert(TABLE_NAME, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "updateCarousel: ", e);
        }
    }


    public List<String> getTitles() {
        List<String> array = new ArrayList<String>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase localDB = bHelper.getWritableDatabase();
        Cursor getLocalDBData = localDB.rawQuery("SELECT Titulo FROM " + TABLE_NAME, null);
        try {
            while(getLocalDBData.moveToNext()){
                String uname = getLocalDBData.getString(0);
                array.add(uname);
            }
            localDB.close();
            return array;
        }catch(Exception e) {
            Log.e(TAG, "getTitles: ", e);
            return array;
        }
    }

    public List<String> getUrls() {
        List<String> array = new ArrayList<String>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase localDB = bHelper.getWritableDatabase();
        Cursor getLocalDBData = localDB.rawQuery("SELECT Imagen FROM " + TABLE_NAME, null);
        try {
            while(getLocalDBData.moveToNext()){
                String uname = getLocalDBData.getString(0);
                array.add(uname);
            }
            localDB.close();
            return array;
        }catch(Exception e) {
            Log.e(TAG, "getUrls: ", e);
            return array;
        }
    }

    public List<String> getIds() {
        List<String> array = new ArrayList<String>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase localDB = bHelper.getWritableDatabase();
        Cursor getLocalDBData = localDB.rawQuery("SELECT id FROM " + TABLE_NAME, null);
        try {
            while(getLocalDBData.moveToNext()){
                String uname = getLocalDBData.getString(0);
                array.add(uname);
            }
            localDB.close();
            return array;
        }catch(Exception e) {
            Log.e(TAG, "getIds: ", e);
            return array;
        }
    }

    //Actualizar al abrir nota
    public void NoteOnClick(int id) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Nota", id);
            db.update(NoteHelper, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "NoteOnClick: ", e);
        }
    }
    //Establecer nota previe
    public void PrevNoteOnClick(int id) {
        try {
            int idPrev = 0;
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            Cursor getNoteID = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE id < "+id+" ORDER BY id DESC LIMIT 1 ",null);
            while(getNoteID.moveToNext()) {
                idPrev = getNoteID.getInt(0);
            }

            values.put("PrevNote", idPrev);
            db.update(NoteHelper, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "PrevNoteOnClick: ", e);
        }
    }
    //Establecer nota siguiente
    public void NextNoteOnClick(int id) {
        try {
            int idNext = 0;
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            Cursor getServID = db.rawQuery("SELECT id FROM " + TABLE_NAME + " WHERE id > "+id+" ORDER BY id LIMIT 1 ",null);
            while(getServID.moveToNext()) {
                idNext = getServID.getInt(0);
            }


            values.put("NextNote", idNext);
            db.update(NoteHelper, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "NextNoteOnClick: ", e);
        }
    }

    public ArrayList<Integer> getNoteID() {
        ArrayList<Integer> NoteIds = new ArrayList<>();
        int id = 0;
        int idPrev = 0;
        int idNext = 0;
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNoteID = db.rawQuery("SELECT Nota, PrevNote, NextNote FROM " + NoteHelper,null);
            while(getNoteID.moveToNext()) {
                id = getNoteID.getInt(0);
                idPrev = getNoteID.getInt(1);
                idNext = getNoteID.getInt(2);
                NoteIds.add(id);
                NoteIds.add(idPrev);
                NoteIds.add(idNext);
            }getNoteID.close();
            return NoteIds;
        }catch (RuntimeException e) {
            Log.e(TAG, "getNoteID: ", e);
            return NoteIds;
        }
    }

    //Getting Note by id
    public ArrayList<String> getNote(int id) {
        ArrayList<String> NoteResult = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Titulo, Cuerpo, Imagen FROM " + TABLE_NAME + " WHERE id = '"+id+"' ",null);
            while(getNote.moveToNext()) {
                String Titulo = getNote.getString(0);
                String Cuerpo = getNote.getString(1);
                String Imagen = getNote.getString(2);
                NoteResult.add(Titulo);
                NoteResult.add(Cuerpo);
                NoteResult.add(Imagen);
            }getNote.close();
            return NoteResult;
        }catch (RuntimeException e) {
            Log.e(TAG, "getNote: ", e);
            return NoteResult;
        }
    }

    //Getting last 4 notes
    public ArrayList<String> getLastNotes() {
        ArrayList<String> LastNotes = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNoteID = db.rawQuery("SELECT id, Titulo, Imagen FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 5, 4",null);
            while(getNoteID.moveToNext()) {
                String id = getNoteID.getString(0);
                String Titulo = getNoteID.getString(1);
                String Imagen = getNoteID.getString(2);
                LastNotes.add(id + "∑" + Titulo + "∑" + Imagen);
            }getNoteID.close();
            return LastNotes;
        }catch (RuntimeException e) {
            Log.e(TAG, "getLastNotes: ", e);
            return LastNotes;
        }
    }

    //Insert Servicios
    public void InsertServicios(String json_servicios_id, String json_servicios_status, String titleArrayTitle, String fullImageLink, String bodyText) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", json_servicios_id);
            values.put("Status", json_servicios_status);
            values.put("Titulo", titleArrayTitle);
            values.put("Cuerpo", bodyText);
            values.put("Imagen", fullImageLink);
            db.insert(ServiciosHelper, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertServicios: ", e);
        }
    }

    //Insert Servicios
    public void InsertErrores(String error_text) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("error", error_text);
            db.insert(Errores, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertErrores: ", e);
        }
    }

    public String getErrores() {
        String error = "";
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getServices = db.rawQuery("SELECT error FROM " + Errores + " ORDER BY id DESC LIMIT 1 ",null);
            while(getServices.moveToNext()) {
                error = getServices.getString(0);
            }getServices.close();
        }catch (RuntimeException e) {
            Log.e(TAG, "showServices: ", e);
        }
        return error;
    }

    public ArrayList<String> getServiceID() {
        ArrayList<String> servicesResult = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getServices = db.rawQuery("SELECT id FROM " + ServiciosHelper + " ",null);
            while(getServices.moveToNext()) {
                String id = getServices.getString(0);
                servicesResult.add(id);
            }getServices.close();
            return servicesResult;
        }catch (RuntimeException e) {
            Log.e(TAG, "showServices: ", e);
            return servicesResult;
        }
    }
    public ArrayList<String> getServiceTitle() {
        ArrayList<String> servicesResult = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getServices = db.rawQuery("SELECT Titulo FROM " + ServiciosHelper + " ",null);
            while(getServices.moveToNext()) {
                String Title = getServices.getString(0);
                servicesResult.add(Title);
            }getServices.close();
            return servicesResult;
        }catch (RuntimeException e) {
            Log.e(TAG, "showServices: ", e);
            return servicesResult;
        }
    }
    public ArrayList<String> getServiceImageURL() {
        ArrayList<String> servicesResult = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getServices = db.rawQuery("SELECT Imagen FROM " + ServiciosHelper + " ",null);
            while(getServices.moveToNext()) {
                String imageURL = getServices.getString(0);
                servicesResult.add(imageURL);
            }getServices.close();
            return servicesResult;
        }catch (RuntimeException e) {
            Log.e(TAG, "showServices: ", e);
            return servicesResult;
        }
    }

    public ArrayList<String> showServices() {
        ArrayList<String> servicesResult = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getServices = db.rawQuery("SELECT id, Titulo, Imagen FROM " + ServiciosHelper + " ",null);
            while(getServices.moveToNext()) {
                String id = getServices.getString(0);
                String Titulo = getServices.getString(1);
                String Imagen = getServices.getString(2);
                servicesResult.add(id);
                servicesResult.add(Titulo);
                servicesResult.add(Imagen);
            }getServices.close();
            return servicesResult;
        }catch (RuntimeException e) {
            Log.e(TAG, "showServices: ", e);
            return servicesResult;
        }
    }

    //Getting Service //Waiting for coding //TODO remove when finish
    public ArrayList<String> getService(int id) {
        ArrayList<String> NoteResult = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Titulo, Cuerpo, Imagen FROM " + ServiciosHelper + " WHERE id = '"+id+"' ",null);
            while(getNote.moveToNext()) {
                String Titulo = getNote.getString(0);
                String Cuerpo = getNote.getString(1);
                String Imagen = getNote.getString(2);
                NoteResult.add(Titulo);
                NoteResult.add(Cuerpo);
                NoteResult.add(Imagen);
            }getNote.close();
            return NoteResult;
        }catch (RuntimeException e) {
            Log.e(TAG, "getNote: ", e);
            return NoteResult;
        }
    }

    //Insert QuienesSomos
    public void InsertQuienesSomos(String Title, String Body) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("Title", Title);
            values.put("Body", Body);
            db.insert(QuienesSomosHelper, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertQuienesSomos: ", e);
        }
    }

    public void insertVehicles(String tipoVeh, String Marca, String Linea, String Tag, String imgUrl, String ctl_contract_type, String clt_expiration_date, String Saldo, String Placa, String Color, String Anio, String ctl_stall_id, String ctl_user_id, String ctl_id) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("tipoVeh", tipoVeh);
            values.put("Marca", Marca);
            values.put("Linea", Linea);
            values.put("Tag", Tag);
            values.put("imgUrl", imgUrl);
            values.put("ctl_contract_type", ctl_contract_type);
            values.put("clt_expiration_date", clt_expiration_date);
            values.put("Saldo", Saldo);
            values.put("Placa", Placa);
            values.put("Color", Color);
            values.put("Anio", Anio);
            values.put("ctl_stall_id", ctl_stall_id);
            values.put("ctl_user_id", ctl_user_id);
            values.put("ctl_id", ctl_id);
            db.insert(Vehiculos, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "insertVehicles: ", e);
        }
    }

    public ArrayList<String> getVehicles() {
        ArrayList<String> result = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            //Cursor getRes = db.rawQuery("SELECT tipoVeh, Marca, Linea, Tag, imgUrl, ctl_contract_type, clt_expiration_date, Saldo, Placa, Color, Anio, ctl_stall_id, ctl_user_id, ctl_id FROM " + Vehiculos + " WHERE tipoVeh != 0 AND Tag LIKE '%EPAS%' OR tipoVeh != 0 AND Tag LIKE '%FPFC%' ORDER BY tipoVeh DESC ", null);
            Cursor getRes = db.rawQuery("SELECT tipoVeh, Marca, Linea, Tag, imgUrl, ctl_contract_type, clt_expiration_date, Saldo, Placa, Color, Anio, ctl_stall_id, ctl_user_id, ctl_id FROM " + Vehiculos + " WHERE tipoVeh != 0 ORDER BY tipoVeh DESC ", null); //Temporalmente
            while(getRes.moveToNext()) {
                String tipoVeh = getRes.getString(0);
                String Marca = getRes.getString(1);
                String Linea = getRes.getString(2);
                String Tag = getRes.getString(3);
                String imgUrl = getRes.getString(4);
                String ctl_contract_type = getRes.getString(5);
                String clt_expiration_date = getRes.getString(6);
                String Saldo = getRes.getString(7);
                String Placa = getRes.getString(8);
                String Color = getRes.getString(9);
                String Anio = getRes.getString(10);
                String ctl_stall_id = getRes.getString(11);
                String ctl_user_id = getRes.getString(12);
                String ctl_id = getRes.getString(13);

                String strRes = tipoVeh + "∑" + Marca + "∑" + Linea + "∑" + Tag + "∑" + imgUrl + "∑" + ctl_contract_type + "∑" + clt_expiration_date + "∑" + Saldo + "∑" + Placa + "∑" + Color + "∑" + Anio + "∑" + ctl_stall_id + "∑" + ctl_user_id + "∑" + ctl_id;
                result.add(strRes);
            }getRes.close();
            return result;
        }catch (RuntimeException e) {
            Log.e(TAG, "getNote: ", e);
            return result;
        }
    }

    public ArrayList<String> getProfileVehicles() {
        ArrayList<String> result = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getRes = db.rawQuery("SELECT tipoVeh, Marca, Linea, Tag, imgUrl, ctl_contract_type, clt_expiration_date, Saldo, Placa, Color, Anio, ctl_stall_id, ctl_user_id, ctl_id  FROM " + Vehiculos + " ORDER BY tipoVeh DESC " ,null);
            while(getRes.moveToNext()) {
                String tipoVeh = getRes.getString(0);
                String Marca = getRes.getString(1);
                String Linea = getRes.getString(2);
                String Tag = getRes.getString(3);
                String imgUrl = getRes.getString(4);
                String ctl_contract_type = getRes.getString(5);
                String clt_expiration_date = getRes.getString(6);
                String Saldo = getRes.getString(7);
                String Placa = getRes.getString(8);
                String Color = getRes.getString(9);
                String Anio = getRes.getString(10);
                String ctl_stall_id = getRes.getString(11);
                String ctl_user_id = getRes.getString(12);
                String ctl_id = getRes.getString(13);

                String strRes = tipoVeh + "∑" + Marca + "∑" + Linea + "∑" + Tag + "∑" + imgUrl + "∑" + ctl_contract_type + "∑" + clt_expiration_date + "∑" + Saldo + "∑" + Placa + "∑" + Color + "∑" + Anio + "∑" + ctl_stall_id + "∑" + ctl_user_id + "∑" + ctl_id;
                result.add(strRes);
            }getRes.close();
            return result;
        }catch (RuntimeException e) {
            Log.e(TAG, "getNote: ", e);
            return result;
        }
    }

    public void cleanVehiculos() {
        dbHelper bCapturas = new dbHelper(context);
        SQLiteDatabase db = bCapturas.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + Vehiculos);
        }catch (RuntimeException e){
            Log.e(TAG, "cleanVehiculos: ", e);
        }

    }


    //Insert Terms and conds
    public void InsertTermsAndCond(String Title, String Body) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("Title", Title);
            values.put("Body", Body);
            db.insert(TermsAndConditionsHelp, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "insert Terms And Conds: ", e);
        }
    }

    public ArrayList<String> getWhoWeAre() {
        ArrayList<String> gettingData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Title, Body FROM " + QuienesSomosHelper + " WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                String Titulo = getNote.getString(0);
                String Cuerpo = getNote.getString(1);
                gettingData.add(Titulo);
                gettingData.add(Cuerpo);
            }getNote.close();
            return gettingData;
        }catch (RuntimeException e) {
            Log.e(TAG, "getWhoWeAre: ", e);
            return gettingData;
        }
    }

    //Insert Objetivo
    public void InsertObjetivo(String Title, String Body) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("Title", Title);
            values.put("Body", Body);
            db.insert(ObjetivoHelper, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertObjetivo: ", e);
        }
    }
    //getting objectives
    public ArrayList<String> getObjectivo() {
        ArrayList<String> gettingData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Title, Body FROM " + ObjetivoHelper + " WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                String Titulo = getNote.getString(0);
                String Cuerpo = getNote.getString(1);
                gettingData.add(Titulo);
                gettingData.add(Cuerpo);
            }getNote.close();
            return gettingData;
        }catch (RuntimeException e) {
            Log.e(TAG, "getObjectivo: ", e);
            return gettingData;
        }
    }


    //Insert Objetivo
    public ArrayList<String> sqlLineamientos(String Title, String Body, String Transaction) {
        ArrayList<String> gettingData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        if (Objects.equals(Transaction, "set")) {
            try {
                ContentValues values = new ContentValues();
                values.put("id", 1);
                values.put("Title", Title);
                values.put("Body", Body);
                db.insert(LineamientosHelper, null, values);
                db.close();
            }catch (RuntimeException e){
                Log.e(TAG, "sqlLineamientos: ", e);
            }
        }
        if (Objects.equals(Transaction, "get")) {
            try {
                Cursor getNote = db.rawQuery("SELECT Title, Body FROM " + LineamientosHelper + " WHERE id = 1 ",null);
                while(getNote.moveToNext()) {
                    String Titulo = getNote.getString(0);
                    String Cuerpo = getNote.getString(1);
                    gettingData.add(Titulo);
                    gettingData.add(Cuerpo);
                }getNote.close();
            }catch (RuntimeException e) {
                Log.e(TAG, "getObjectivo: ", e);

            }
        }
        return gettingData;
    }

    //Insert Mision
    public void InsertMision(String Title, String Body) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("Title", Title);
            values.put("Body", Body);
            db.insert(MisionHelper, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertMision: ", e);
        }
    }
    //getting mision
    public ArrayList<String> getMision() {
        ArrayList<String> gettingData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Title, Body FROM " + MisionHelper + " WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                String Titulo = getNote.getString(0);
                String Cuerpo = getNote.getString(1);
                gettingData.add(Titulo);
                gettingData.add(Cuerpo);
            }getNote.close();
            return gettingData;
        }catch (RuntimeException e) {
            Log.e(TAG, "getMision: ", e);
            return gettingData;
        }
    }

    //Insert Vision
    public void InsertVision(String Title, String Body) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("Title", Title);
            values.put("Body", Body);
            db.insert(VisionHelper, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertVision: ", e);
        }
    }
    //getting vision
    public ArrayList<String> getVision() {
        ArrayList<String> gettingData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Title, Body FROM " + VisionHelper + " WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                String Titulo = getNote.getString(0);
                String Cuerpo = getNote.getString(1);
                gettingData.add(Titulo);
                gettingData.add(Cuerpo);
            }getNote.close();
            return gettingData;
        }catch (RuntimeException e) {
            Log.e(TAG, "getVision: ", e);
            return gettingData;
        }
    }

    //Insert Privacy
    public void InsertPrivacy(String Title, String Body) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("Title", Title);
            values.put("Body", Body);
            db.insert(PrivacyHelper, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertPrivacy: ", e);
        }
    }
    //getting privacy
    public ArrayList<String> getPrivacy() {
        ArrayList<String> gettingData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Title, Body FROM " + PrivacyHelper + " WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                String Titulo = getNote.getString(0);
                String Cuerpo = getNote.getString(1);
                gettingData.add(Titulo);
                gettingData.add(Cuerpo);
            }getNote.close();
            return gettingData;
        }catch (RuntimeException e) {
            Log.e(TAG, "getPrivacy: ", e);
            return gettingData;
        }
    }

    //Insert Rates
    public void InsertCurrentRates(String Title, String Body) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("Title", Title);
            values.put("Body", Body);
            db.insert(RatesHelper, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertCurrentRates: ", e);
        }
    }
    
    //getting rates
    public ArrayList<String> getCurrentRates() {
        ArrayList<String> gettingData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Title, Body FROM " + RatesHelper + " WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                String Titulo = getNote.getString(0);
                String Cuerpo = getNote.getString(1);
                gettingData.add(Titulo);
                gettingData.add(Cuerpo);
            }getNote.close();
            return gettingData;
        }catch (RuntimeException e) {
            Log.e(TAG, "getCurrentRates: ",  e);
            return gettingData;
        }
    }

    //Insert next layout
    public void updateCarSelected(String varCarSelected) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("CarSelected", varCarSelected);
            db.update(CarSelected, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "UpdateCarConfirmation: ", e);
        }
    }
    public void updateCantidadRecargar(String varCantidadRecargar) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("CantidadRecargar", varCantidadRecargar);
            db.update(CarSelected, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "UpdateCarConfirmation: ", e);
        }
    }

    //Get next layout
    public String getCarSelected() {
        String Car = "";

        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT CarSelected FROM " + CarSelected + " WHERE id = 1 ",null);
            if(getNote.moveToNext()) {
                Car = getNote.getString(0);

            }
            getNote.close();
        }catch (RuntimeException e) {
            Log.e(TAG, "getIfShowCar: ",  e);
        }
        return Car;
    }
    public String getCantidadRecargar() {
        String CantidadRecargar = "";

        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT CantidadRecargar FROM " + CarSelected + " WHERE id = 1 ",null);
            if(getNote.moveToNext()) {
                CantidadRecargar = getNote.getString(0);

            }
            getNote.close();
        }catch (RuntimeException e) {
            Log.e(TAG, "getIfShowCar: ",  e);
        }
        return CantidadRecargar;
    }

    public ArrayList<String> getTermsAndConds() {
        ArrayList<String> gettingData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Title, Body FROM " + TermsAndConditionsHelp + " WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                String Titulo = getNote.getString(0);
                String Cuerpo = getNote.getString(1);
                gettingData.add(Titulo);
                gettingData.add(Cuerpo);
            }getNote.close();
            return gettingData;
        }catch (RuntimeException e) {
            Log.e(TAG, "getTermsAndConds: ",  e);
            return gettingData;
        }
    }

    //Insert tramites
    public void InsertTramites(String Section, String uri) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Section, uri);
            db.update(uriTramites, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertTramites: ", e);
        }
    }

    public String getTramites(String searchQuery) {
        String Tramite = "";
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT "+searchQuery+" FROM " + uriTramites + " WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                Tramite = getNote.getString(0);
            }getNote.close();
        }catch (RuntimeException e) {
            Log.e(TAG, "getTermsAndConds: ",  e);
        }
        return Tramite;
    }
    public ArrayList<String> getTramitesArray() {
        ArrayList<String> Tramites = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        String Photo_Sentri = "";
        String Photo_idOficial = "";
        String Photo_idOficialReverso = "";
        String Photo_tarjetaCirculacion = "";
        String Photo_polizaSeguro = "";
        String Photo_polizaSeguro2 = "";
        String Photo_AprovacionUSA = "";
        String Photo_cartaPoder = "";
        String FIRMA = "";
        try {
            Cursor getNote = db.rawQuery("SELECT Photo_Sentri, Photo_idOficial, Photo_idOficialReverse, Photo_tarjetaCirculacion, Photo_polizaSeguro, Photo_polizaSeguro2, Photo_AprovacionUSA, Photo_cartaPoder, FIRMA FROM " + uriTramites + " WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                Photo_Sentri = getNote.getString(0);
                Photo_idOficial = getNote.getString(1);
                Photo_idOficialReverso = getNote.getString(2);
                Photo_tarjetaCirculacion = getNote.getString(3);
                Photo_polizaSeguro = getNote.getString(4);
                Photo_polizaSeguro2 = getNote.getString(5);
                Photo_AprovacionUSA = getNote.getString(6);
                Photo_cartaPoder = getNote.getString(7);
                FIRMA = getNote.getString(8);
                Tramites.add(Photo_Sentri);
                Tramites.add(Photo_idOficial);
                Tramites.add(Photo_idOficialReverso);
                Tramites.add(Photo_tarjetaCirculacion);
                Tramites.add(Photo_polizaSeguro);
                Tramites.add(Photo_polizaSeguro2);
                Tramites.add(Photo_AprovacionUSA);
                Tramites.add(Photo_cartaPoder);
                Tramites.add(FIRMA);
            }
            getNote.close();
        }catch (RuntimeException e) {
            Log.e(TAG, "getTramitesArray: ",  e);
        }
        return Tramites;
    }

    public void InsertCita(int id_proc, int id_proc_type) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id_proc", id_proc);
            values.put("id_proc_type", id_proc_type);
            db.update(CitasList, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertTramites: ", e);
        }
    }
    public void InsertCitaFecha(String Date, String Time) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Date", Date);
            values.put("Time", Time);
            db.update(CitasList, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertTramites: ", e);
        }
    }

    //Insert "Reporte de puentes"
    public void InsertReportePuentes(String portNumber, String portName, String crossingName, String generalDate, String portStatus, String maxPassengerLanes, String vehicleStandardUpdateTime, String vehicleStandardDelayMinutes, String vehicleStandardLanesOpen, String vehicleReadyUpdateTime, String vehicleReadyDelayMinutes, String vehicleReadyLanesOpen, String maxPedestrianLanes, String pedestrianUpdateTime, String pedestrianDelayMinutes, String pedestrianLanesOpen) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", portNumber);
            values.put("PortName", portName);
            values.put("CrossingName", crossingName);
            values.put("GeneralDate", generalDate);
            values.put("PortStatus", portStatus);
            values.put("MaxPassengerLanes", maxPassengerLanes);
            values.put("VehicleStandardUpdateTime", vehicleStandardUpdateTime);
            values.put("VehicleStandardDelayMinutes", vehicleStandardDelayMinutes);
            values.put("VehicleStandardLanesOpen", vehicleStandardLanesOpen);
            values.put("VehicleReadyUpdateTime", vehicleReadyUpdateTime);
            values.put("VehicleReadyDelayMinutes", vehicleReadyDelayMinutes);
            values.put("VehicleReadyLanesOpen", vehicleReadyLanesOpen);
            values.put("MaxPedestrianLanes", maxPedestrianLanes);
            values.put("PedestrianUpdateTime", pedestrianUpdateTime);
            values.put("PedestrianDelayMinutes", pedestrianDelayMinutes);
            values.put("PedestrianLanesOpen", pedestrianLanesOpen);
            db.insert(ReporteDePuentes, null, values);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "InsertReportePuentes: ", e);
        }
    }


    public ArrayList<String> getReportePuentes(String CrossingQuery) {
        ArrayList<String> Reportes = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT id, CrossingName, PortStatus, VehicleStandardUpdateTime, VehicleStandardDelayMinutes, VehicleStandardLanesOpen, VehicleReadyDelayMinutes, VehicleReadyLanesOpen FROM ReporteDePuentes WHERE CrossingName = '"+CrossingQuery+"' ",null);
            while(getNote.moveToNext()) {
                String id = getNote.getString(0);
                String CrossingName = getNote.getString(1);
                String PortStatus = getNote.getString(2);
                String VehicleStandardUpdateTime = getNote.getString(3);
                String VehicleStandardDelayMinutes = getNote.getString(4);
                String VehicleStandardLanesOpen = getNote.getString(5);
                String VehicleReadyDelayMinutes = getNote.getString(6);
                String VehicleReadyLanesOpen = getNote.getString(7);


                Reportes.add(id);
                Reportes.add(CrossingName);
                Reportes.add(PortStatus);
                Reportes.add(VehicleStandardUpdateTime);
                Reportes.add(VehicleStandardDelayMinutes);
                Reportes.add(VehicleStandardLanesOpen);
                Reportes.add(VehicleReadyDelayMinutes);
                Reportes.add(VehicleReadyLanesOpen);
            }
            getNote.close();
        }catch (RuntimeException e) {
            Log.e(TAG, "getReportePuentes: ",  e);
        }
        return Reportes;
    }

    public void FixDataFiles(int id_proc, int id_proc_type, int id_file_type, String TramiteDesc, String TramiteComment) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("TramiteID", id_proc);
            values.put("Tramite", id_proc_type);
            values.put("TipoTramite", "file");
            values.put("FileID", id_file_type);
            values.put("TramiteDesc", TramiteDesc);
            values.put("TramiteComment", TramiteComment);
            db.update(FixTramite, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "FixData: ", e);
        }
    }

    public void FixImgLocation(String imgLocation) {
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FileLocation", imgLocation);
            db.update(FixTramite, values, "id = 1", null);
            db.close();
        }catch (RuntimeException e){
            Log.e(TAG, "FixData: ", e);
        }
    }

    public ArrayList<String> getFixedData() {
        ArrayList<String> FixedData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT TramiteID, Tramite, TipoTramite, FileID, TramiteDesc, TramiteComment, FileLocation FROM FixTramite WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                String TramiteID = getNote.getString(0);
                String Tramite = getNote.getString(1);
                String TipoTramite = getNote.getString(2);
                String FileID = getNote.getString(3);
                String TramiteDesc = getNote.getString(4);
                String TramiteComment = getNote.getString(5);
                String FileLocation = getNote.getString(6);


                FixedData.add(TramiteID);
                FixedData.add(Tramite);
                FixedData.add(TipoTramite);
                FixedData.add(FileID);
                FixedData.add(TramiteDesc);
                FixedData.add(TramiteComment);
                FixedData.add(FileLocation);
            }
            getNote.close();
        }catch (RuntimeException e) {
            Log.e(TAG, "getFixedData: ",  e);
        }
        return FixedData;
    }

    public ArrayList<String> getCitaId() {
        ArrayList<String> CitaData = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT id_proc, id_proc_type FROM CitasList WHERE id = 1 ",null);
            while(getNote.moveToNext()) {
                String id_proc = getNote.getString(0);
                String id_proc_type = getNote.getString(1);

                CitaData.add(id_proc);
                CitaData.add(id_proc_type);
            }
            getNote.close();
        }catch (RuntimeException e) {
            Log.e(TAG, "getFixedData: ",  e);
        }
        return CitaData;
    }

}
