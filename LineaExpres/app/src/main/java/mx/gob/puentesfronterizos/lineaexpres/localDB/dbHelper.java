package mx.gob.puentesfronterizos.lineaexpres.localDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LineaExpress.mysqlDB";
    public static final String TABLE_NAME = "carousel";
    public static final String TABLE_USER = "UserLogged";
    public static final String NoteHelper = "NoteHelper";
    public static final String SubServHelper = "SubServHelper";
    public static final String ServiciosHelper = "ServiciosHelper";
    public static final String QuienesSomosHelper = "QuienesSomosHelper";
    public static final String ObjetivoHelper = "ObjetivoHelper";
    public static final String MisionHelper = "MisionHelper";
    public static final String VisionHelper = "VisionHelper";
    public static final String PrivacyHelper = "PrivacyHelper";
    public static final String RatesHelper = "RatesHelper";
    public static final String LineamientosHelper = "LineamientosHelper";
    public static final String TermsAndConditionsHelp = "TermsAndConditionsHelper";
    public static final String CarSelected = "CarSelected";
    public static final String uriTramites = "uriTramites";
    public static final String ReporteDePuentes = "ReporteDePuentes";
    public static final String FixTramite = "FixTramite";
    public static final String CitasList = "CitasList";
    public static final String Errores = "Errores";
    public static final String Payment = "Payment";
    public static final String Vehiculos = "Vehiculos";

    public dbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_USER + " (id INTEGER UNIQUE, Usuario TEXT, NombreCompleto TEXT, tempPass TEXT, Token TEXT, FireToken TEXT, Sentri TEXT, FechaSentri TEXT, facRazonSocial TEXT DEFAULT '', facRFC TEXT DEFAULT '', facDomFiscal TEXT DEFAULT '', facCP TEXT DEFAULT '', facEmail TEXT DEFAULT '', facTelefono TEXT DEFAULT '')" );
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER, Titulo TEXT, Cuerpo TEXT, Etiqueta TEXT, Imagen blob)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + NoteHelper + " (id INTEGER NOT NULL, Nota INTEGER, PrevNote INTEGER, NextNote INTEGER)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + ServiciosHelper + " (id INTEGER NOT NULL, Status TEXT, Titulo TEXT, Cuerpo TEXT, Imagen TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + SubServHelper + " (id INTEGER NOT NULL, Service INTEGER, PrevServ INTEGER, NextServ INTEGER)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + QuienesSomosHelper + " (id INTEGER NOT NULL, Title TEXT, Body TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + ObjetivoHelper + " (id INTEGER NOT NULL, Title TEXT, Body TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + MisionHelper + " (id INTEGER NOT NULL, Title TEXT, Body TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + VisionHelper + " (id INTEGER NOT NULL, Title TEXT, Body TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + PrivacyHelper + " (id INTEGER NOT NULL, Title TEXT, Body TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + RatesHelper + " (id INTEGER NOT NULL, Title TEXT, Body TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + LineamientosHelper + " (id INTEGER NOT NULL, Title TEXT, Body TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + TermsAndConditionsHelp + " (id INTEGER NOT NULL, Title TEXT, Body TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + CarSelected + " (id INTEGER NOT NULL, CarSelected TEXT, CantidadRecargar TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + uriTramites + " (id INTEGER NOT NULL, Photo_Sentri TEXT, Photo_idOficial TEXT, Photo_idOficialReverse TEXT, Photo_tarjetaCirculacion TEXT, Photo_polizaSeguro TEXT, Photo_polizaSeguro2 TEXT, Photo_AprovacionUSA TEXT, Photo_cartaPoder TEXT, FIRMA TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + ReporteDePuentes + " (id INTEGER NOT NULL, PortName TEXT, CrossingName TEXT, GeneralDate TEXT, PortStatus TEXT, MaxPassengerLanes TEXT, VehicleStandardUpdateTime TEXT, VehicleStandardDelayMinutes TEXT, VehicleStandardLanesOpen TEXT, VehicleReadyUpdateTime TEXT, VehicleReadyDelayMinutes TEXT, VehicleReadyLanesOpen TEXT, MaxPedestrianLanes TEXT, PedestrianUpdateTime TEXT, PedestrianDelayMinutes TEXT, PedestrianLanesOpen TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + FixTramite + " (id INTEGER NOT NULL, TramiteID TEXT, Tramite TEXT, TipoTramite TEXT, FileID TEXT, TramiteDesc TEXT, TramiteComment TEXT,  FileLocation TEXT, TextField TEXT, TextDesc TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + CitasList + " (id INTEGER NOT NULL, id_proc TEXT, id_proc_type TEXT, Date TEXT, Time TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + Errores + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, error TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + Payment + " (id INTEGER PRIMARY KEY NOT NULL, CarToRecharge TEXT, QtyRecharge TEXT, CardName TEXT, CardNumber TEXT, CardExpirationDate, CardCVV TEXT, CardEmail TEXT, Country TEXT, State TEXT, City TEXT, CP TEXT, Street TEXT)" );
        sqLiteDatabase.execSQL("CREATE TABLE " + Vehiculos + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, tipoVeh TEXT, Marca TEXT, Linea TEXT, Tag TEXT, imgUrl TEXT, ctl_contract_type TEXT, clt_expiration_date TEXT, Saldo TEXT, Placa TEXT, Color TEXT, Anio TEXT, ctl_stall_id TEXT, ctl_user_id TEXT, ctl_id TEXT)" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SubServHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ServiciosHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuienesSomosHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ObjetivoHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MisionHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VisionHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PrivacyHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RatesHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LineamientosHelper);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CarSelected);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + uriTramites);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReporteDePuentes);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FixTramite);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CitasList);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Payment);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Vehiculos);
        onCreate(sqLiteDatabase);
    }
}
