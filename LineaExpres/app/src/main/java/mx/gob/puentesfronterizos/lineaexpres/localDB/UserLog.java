package mx.gob.puentesfronterizos.lineaexpres.localDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserLog extends dbHelper {
    private static final String TAG = "UserLog";
    Context context;
    public UserLog(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    //Set User, Email and Encrypted Password
    public boolean SetUserData(String User, String Token, String FullName, String Sentri, String SentriDate, String user_set_pwd) {
        boolean result = false;
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Usuario", User);
            values.put("NombreCompleto", FullName);
            values.put("Token", Token);
            values.put("Sentri", Sentri);
            values.put("FechaSentri", SentriDate);
            values.put("tempPass", user_set_pwd);
            db.update(TABLE_USER, values, "id = 1", null);
            db.close();
            result = true;
        }catch (RuntimeException e){
            Log.e(TAG, "SetUserData: ", e);
        }
        return result;
    }

    public boolean updateSentri(String Sentri) {
        boolean result = false;
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Sentri", Sentri);
            db.update(TABLE_USER, values, "id = 1", null);
            db.close();
            result = true;
        }catch (RuntimeException e){
            Log.e(TAG, "SetUserData: ", e);
        }
        return result;
    }

    //Set user Firebase token
    public String UserFireToken(String transaction, String Token) {
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        String FireToken = null;
        switch (transaction){
            case "set":
                try {
                    ContentValues values = new ContentValues();
                    values.put("FireToken", Token);
                    db.update(TABLE_USER, values, "id = 1", null);
                    db.close();
                }catch (RuntimeException e){
                    Log.e(TAG, "setUserFireToken: ", e);
                }
                break;
            case "get":
                try {
                    Cursor getFireToken = db.rawQuery("SELECT FireToken FROM " + TABLE_USER + " WHERE id = 1 ",null);
                    if(getFireToken.moveToNext()) {
                        FireToken = getFireToken.getString(0);
                    }getFireToken.close();
                }catch (RuntimeException e) {
                    Log.e(TAG, "setUserFireToken: ", e);
                }
        }
        return FireToken;
    }

    public boolean setBillingData(String razonSocial, String RFC, String domFiscal, String CP, String Email, String Telefono) {
        boolean pass = false;
        try {
            dbHelper bHelper = new dbHelper(context);
            SQLiteDatabase db = bHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("facRazonSocial", razonSocial);
            values.put("facRFC", RFC);
            values.put("facDomFiscal", domFiscal);
            values.put("facCP", CP);
            values.put("facEmail", Email);
            values.put("facTelefono", Telefono);
            int shouldPass = db.update(TABLE_USER, values, "id = 1", null);
            if (shouldPass != 0) {
                pass = true;
            }
            db.close();

        }catch (RuntimeException e){
            Log.e(TAG, "SetUserData: ", e);
        }
        return pass;
    }

    //Getting User data
    public ArrayList<String> GetUserData() {
        ArrayList<String> result = new ArrayList<>();
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            Cursor getNote = db.rawQuery("SELECT Usuario, Token, NombreCompleto, FireToken, Sentri, FechaSentri, facRazonSocial, facRFC, facDomFiscal, facCP, facEmail, facTelefono, tempPass FROM " + TABLE_USER + " WHERE id = 1 ",null);
            if(getNote.moveToNext()) {
                String Usuario = getNote.getString(0);
                String Token = getNote.getString(1);
                String FName = getNote.getString(2);
                String FireToken = getNote.getString(3);
                String Sentri = getNote.getString(4);
                String FechaSentri = getNote.getString(5);
                String facRazonSocial = getNote.getString(6);
                String facRFC = getNote.getString(7);
                String facDomFiscal = getNote.getString(8);
                String facCP = getNote.getString(9);
                String facEmail = getNote.getString(10);
                String facTelefono = getNote.getString(11);
                String tempPass = getNote.getString(12);
                result.add(Usuario);
                result.add(Token);
                result.add(FName);
                result.add(FireToken);
                result.add(Sentri);
                result.add(FechaSentri);
                result.add(facRazonSocial);
                result.add(facRFC);
                result.add(facDomFiscal);
                result.add(facCP);
                result.add(facEmail);
                result.add(facTelefono);
                result.add(tempPass);


            }getNote.close();
            return result;
        }catch (RuntimeException e) {
            Log.e(TAG, "GetUserData: ", e);
            return result;
        }
    }

    public void Logout() {
        dbHelper bHelper = new dbHelper(context);
        SQLiteDatabase db = bHelper.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + TABLE_USER);
        }catch (RuntimeException e) {
            Log.e(TAG, "Logout: ", e);
        }
    }

}
