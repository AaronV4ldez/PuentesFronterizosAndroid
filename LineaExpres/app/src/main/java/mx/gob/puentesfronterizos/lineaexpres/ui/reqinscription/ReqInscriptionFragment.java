package mx.gob.puentesfronterizos.lineaexpres.ui.reqinscription;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentReqinscriptionBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReqInscriptionFragment extends Fragment {

    private static final int WRITE_REQUEST_CODE = 32;
    //Database declaration
    UserLog userLog;
    updateData UpdateData;

    //Layout fields declaration
    EditText noSentriInput;
    EditText expirationDateSentriInput;
    EditText nameInput;
    EditText middleNameInput;
    EditText lastNameInput;
    EditText PersonalEmailInput;
    EditText PersonalCelInput;
    EditText streetInput;
    EditText noExtInput;
    EditText ColonyInput;
    EditText CityInput;
    EditText StateInput;
    EditText CPInput;
    EditText BusinessNameInput;
    EditText RFCInput;
    EditText ResidenceInput;
    EditText FactEmailInput;
    EditText FactCelInput;
    EditText BrandVehicleInput;
    EditText ModelVehicleInput;
    EditText ColorVehicleInput;
    EditText YearVehicleInput;
    EditText PlatesVehicleInput;
    EditText AnualidadOSaldo;

    ImageView SentriPhotoImageView;
    ImageView SentriPhotoImageView2;
    ImageView IdOficialPhotoImageView;
    ImageView IdOficialReversePhotoImageView;
    ImageView IdOficialReversePhotoImageView2;
    ImageView TarjetaCirculacionPhotoImageView;
    ImageView PolizaSeguroPhotoImageView;
    ImageView PolizaSeguroPhotoImageViewSecond;
    ImageView AprovacionUSAPhotoImageView;
    ImageView CartaPoderPhotoImageView;

    TextView expirationDateSentriLbl;
    TextView noSentriLbl;

    Button btnSelSaldo;

    //Layout Photo Declarations

    Button uploadSentriPhotoBtn;
    Button uploadSentriPhotoBtn2;
    Button uploadOfficialIDPhotoBtn;
    Button uploadOfficialReverseIDPhotoBtn;
    Button uploadOfficialReverseIDPhotoBtn2;
    Button uploadCirculationCardPhotoBtn;
    Button uploadInsurancePolicyBtn;
    Button uploadInsurancePolicyBtnSecond;
    Button uploadUSAApprovalBtn;
    Button uploadPowerAttorney;



    String Nacionalidad = "";

    String ConvenioSaldo = "";
    String Convenio = "";
    String ConvenioAnualidad = "";


    SignaturePad mSignaturePad;

    Button sendFormBtn;

    String Token;

    String PhotoUriTemp;


    ImageView currentImageView;

    String currentPhotoPath;

    LayoutInflater popupInflater;
    View popup_View;
    View popup_view;
    int popup_width;
    int popup_height;
    PopupWindow popup_Window;
    TextView popup_Head;
    TextView popup_Body;

    Handler handler;
    int counter;

    //Tarifas
    String anual_zaragoza_mx = "";
    String anual_lerdo_mx = "";
    String anual_zaragoza_us = "";
    String anual_lerdo_us = "";
    String anual_mixto_mx = "";
    String anual_mixto_us = "";
    String saldo_zaragoza1_mx = "";
    String saldo_zaragoza2_mx = "";
    String saldo_zaragoza1_us = "";
    String saldo_zaragoza2_us = "";
    String pago_minimotp_mx = "";
    String mbPreciosURL = "";

    String CarSelected = "";
    String Sentri = "";
    String FechaSentri = "";

    String localRazonSocial;
    String localRFCFiscal;
    String localDomFiscal;
    String localEmailFiscal;
    String localTelefonoFiscal;

    LayoutInflater loaderInflater;
    View popupView;
    View view;
    int width;
    int height;
    PopupWindow popupWindow;

    LayoutInflater tarifasInflater;
    View tarifasView;
    View tarifasview;
    int tarifaswidth;
    int tarifasheight;
    PopupWindow tarifaspopupWindow;


    Boolean saldoSel;

    ImageView SaldoImage;

    Boolean AgregarVeh = false;

    String textoPuenteSel = "";

    String numTramite = "1";
    File imageFile = null;
    String currentTramiteFoto = "";
    Uri photoOne = null;
    Uri photoTwo = null;
    Uri photoThree = null;
    Uri photoFour = null;
    Uri photoFive = null;
    Uri photoSix = null;
    Uri photoSeven = null;
    Uri photoEighth = null;
    Uri photoNine = null;
    Uri photoTen = null;


    private Camera camera;

    final Calendar myCalendar= Calendar.getInstance();
    private ActivityResultLauncher<Intent> activityResultLauncher;
    boolean READ_MEDIA_IMAGES = false;

    private FragmentReqinscriptionBinding binding;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReqInscriptionViewModel reqInscriptionViewModel =
                new ViewModelProvider(this).get(ReqInscriptionViewModel.class);

        if (savedInstanceState != null) {
            PhotoUriTemp = savedInstanceState.getString("photo_uri_temp");
        }

        binding = FragmentReqinscriptionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userLog = new UserLog(requireContext()); //Open Users db
        UpdateData = new updateData(requireContext()); //Open Users db
        SQLOnInit sqlOnInit = new SQLOnInit(requireContext()); //Open Users db
        sqlOnInit.cleanTramites();
        sqlOnInit.InsertTramitesOnInit();

        ArrayList<String> userData = userLog.GetUserData();
        Token = userData.get(1);

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
            root = null;
        }

        //EditText
        noSentriInput = binding.noSentriInput;
        expirationDateSentriInput = binding.expirationDateSentriInput;
        streetInput = binding.streetInput;
        noExtInput = binding.noExtInput;
        ColonyInput = binding.ColonyInput;
        CityInput = binding.CityInput;
        StateInput = binding.StateInput;
        CPInput = binding.CPInput;
        BusinessNameInput = binding.BusinessNameInput;
        RFCInput = binding.RFCInput;
        ResidenceInput = binding.ResidenceInput;
        FactEmailInput = binding.FactEmailInput;
        FactCelInput = binding.FactCelInput;
        BrandVehicleInput = binding.BrandVehicleInput;
        ModelVehicleInput = binding.ModelVehicleInput;
        ColorVehicleInput = binding.ColorVehicleInput;
        YearVehicleInput = binding.YearVehicleInput;
        PlatesVehicleInput = binding.PlatesVehicleInput;
        expirationDateSentriLbl = binding.expirationDateSentriLbl;
        noSentriLbl = binding.noSentriLbl;

        btnSelSaldo = binding.btnSelSaldo;

        uploadSentriPhotoBtn = binding.uploadSentriPhotoBtn;
        uploadSentriPhotoBtn2 = binding.uploadSentriPhotoBtn2;
        uploadOfficialIDPhotoBtn = binding.uploadOfficialIDPhotoBtn;
        uploadOfficialReverseIDPhotoBtn = binding.uploadOfficialReverseIDPhotoBtn;
        uploadOfficialReverseIDPhotoBtn2 = binding.uploadOfficialReverseIDPhotoBtn2;
        uploadCirculationCardPhotoBtn = binding.uploadCirculationCardPhotoBtn;
        uploadInsurancePolicyBtn = binding.uploadInsurancePolicyBtn;
        uploadInsurancePolicyBtnSecond = binding.uploadInsurancePolicyBtnSecond;
        uploadUSAApprovalBtn = binding.uploadUSAApprovalBtn;
        uploadPowerAttorney = binding.uploadPowerAttorney;

        SentriPhotoImageView = binding.SentriPhotoImageView;
        SentriPhotoImageView2 = binding.SentriPhotoImageView2;
        IdOficialPhotoImageView = binding.IdOficialPhotoImageView;
        IdOficialReversePhotoImageView = binding.IdOficialReversePhotoImageView;
        IdOficialReversePhotoImageView2 = binding.IdOficialReversePhotoImageView2;
        TarjetaCirculacionPhotoImageView = binding.TarjetaCirculacionPhotoImageView;
        PolizaSeguroPhotoImageView = binding.PolizaSeguroPhotoImageView;
        PolizaSeguroPhotoImageViewSecond = binding.PolizaSeguroPhotoImageViewSecond;
        AprovacionUSAPhotoImageView = binding.AprovacionUSAPhotoImageView;
        CartaPoderPhotoImageView = binding.CartaPoderPhotoImageView;



        Button btnNewVehiculeOriginNational = binding.btnNewVehiculeOriginNational;
        Button btnNewVehiculeOriginUSA = binding.btnNewVehiculeOriginUSA;

        popupInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        popup_View = popupInflater.inflate(R.layout.popup_top, null);
        popup_view = binding.uploadSentriPhotoBtn.getRootView();
        popup_width = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup_height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup_Window = new PopupWindow(popup_View, popup_width, popup_height, false);
        popup_Head = popup_View.findViewById(R.id.popupHead);
        popup_Body = popup_View.findViewById(R.id.popupBody);


        loaderInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        popupView = loaderInflater.inflate(R.layout.loader, null);
        view = binding.uploadSentriPhotoBtn.getRootView();
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(popupView, width, height, false);
        popupWindow.setElevation(20);
        ImageView LoaderGif = popupView.findViewById(R.id.loader_gif);
        Glide.with(requireContext())
                .load(R.drawable.linea_expres_loader)
                .into(LoaderGif);

        tarifasInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        tarifasView = tarifasInflater.inflate(R.layout.tarifas_popup, null);
        tarifasview = binding.uploadSentriPhotoBtn.getRootView();
        tarifaswidth = LinearLayout.LayoutParams.MATCH_PARENT;
        tarifasheight = LinearLayout.LayoutParams.MATCH_PARENT;
        tarifaspopupWindow = new PopupWindow(tarifasView, tarifaswidth, tarifasheight, false);
        tarifaspopupWindow.setElevation(20);
        SaldoImage = tarifasView.findViewById(R.id.imageTarifas);


        Token = userLog.GetUserData().get(1);
        Sentri = userLog.GetUserData().get(4);
        FechaSentri = userLog.GetUserData().get(5);
        localRazonSocial = userLog.GetUserData().get(6);;
        localRFCFiscal = userLog.GetUserData().get(7);
        localDomFiscal = userLog.GetUserData().get(8);
        localEmailFiscal = userLog.GetUserData().get(10);
        localTelefonoFiscal = userLog.GetUserData().get(11);


        CarSelected = UpdateData.getCarSelected();

        TextView Pleca = binding.reqInscriptionTitle;



        TextView SentriImportant = binding.SentriImportant;
        SentriImportant.setText(Html.fromHtml("Necesitas como requisito tu SENTRI, si no lo tienes, visita <a href='https://ttp.cbp.dhs.gov/'>https://ttp.cbp.dhs.gov/</>"));
        SentriImportant.setMovementMethod(LinkMovementMethod.getInstance());

        saldoSel = false;

        if (CarSelected.equals("Inscripcion")){
            Pleca.setText("Solicitud de Inscripción a Línea Exprés");
        }else {
            Pleca.setText("Registro de vehículo");
            noSentriInput.setVisibility(GONE);
            expirationDateSentriInput.setVisibility(GONE);
            expirationDateSentriLbl.setVisibility(GONE);
            noSentriLbl.setVisibility(GONE);
            uploadSentriPhotoBtn.setVisibility(View.VISIBLE);
            uploadSentriPhotoBtn.setText("Aprobación del vehiculo para SENTRI o Global Entry");
            SentriImportant.setVisibility(GONE);
        }

        ArrayList<String> vehiculos = UpdateData.getVehicles();

        if (vehiculos.size() == 0) {
            Pleca.setText("Solicitud de Inscripción a Línea Exprés");
        }else{
            AgregarVeh = true;
            TextView addressTitle = binding.addressTitle;
            TextView noExtLbl = binding.noExtLbl;
            TextView ColonyLbl = binding.ColonyLbl;
            TextView streetLbl = binding.streetLbl;
            TextView CityLbl = binding.CityLbl;
            TextView StateLbl = binding.StateLbl;
            TextView CPLbl = binding.CPLbl;
            TextView billingTitle = binding.billingTitle;
            TextView BusinessNameLbl = binding.BusinessNameLbl;
            TextView RFCLbl = binding.RFCLbl;
            TextView ResidenceLbl = binding.ResidenceLbl;
            TextView FactEmailLbl = binding.FactEmailLbl;
            TextView FactCelLbl = binding.FactCelLbl;

            uploadOfficialIDPhotoBtn.setVisibility(View.VISIBLE);
            uploadOfficialReverseIDPhotoBtn.setVisibility(View.VISIBLE);
            uploadSentriPhotoBtn.setVisibility(View.VISIBLE);
            addressTitle.setVisibility(GONE);
            streetInput.setVisibility(GONE);
            streetLbl.setVisibility(GONE);
            noExtLbl.setVisibility(GONE);
            noExtInput.setVisibility(GONE);
            ColonyLbl.setVisibility(GONE);
            ColonyInput.setVisibility(GONE);
            CityLbl.setVisibility(GONE);
            CityInput.setVisibility(GONE);
            StateLbl.setVisibility(GONE);
            StateInput.setVisibility(GONE);
            CPLbl.setVisibility(GONE);
            CPInput.setVisibility(GONE);
            billingTitle.setVisibility(GONE);
            BusinessNameLbl.setVisibility(GONE);
            BusinessNameInput.setVisibility(GONE);
            RFCLbl.setVisibility(GONE);
            RFCInput.setVisibility(GONE);
            ResidenceLbl.setVisibility(GONE);
            ResidenceInput.setVisibility(GONE);
            FactEmailLbl.setVisibility(GONE);
            FactEmailInput.setVisibility(GONE);
            FactCelLbl.setVisibility(GONE);
            FactCelInput.setVisibility(GONE);
        }

        getPrices();



        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };

        expirationDateSentriInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(requireActivity(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnNewVehiculeOriginNational.setOnClickListener(v -> {
            Nacionalidad = "MX";
            btnNewVehiculeOriginUSA.setBackgroundResource(R.drawable.buttons2);
            btnNewVehiculeOriginNational.setBackgroundResource(R.drawable.buttons);

        });

        btnNewVehiculeOriginUSA.setOnClickListener(v -> {
            Nacionalidad = "USA";
            btnNewVehiculeOriginNational.setBackgroundResource(R.drawable.buttons2);
            btnNewVehiculeOriginUSA.setBackgroundResource(R.drawable.buttons);

        });



        EditText TarifasShow = binding.TarifasShow;
        EditText NameShow = binding.NameShow;

        Button BtnOriginNational = binding.btnCardMX;
        Button BtnOriginUSA = binding.btnCardUSA;
        Button btnSendForm = binding.sendFormBtn;
        //Tipo de convenio
        Button btnZaragoza = binding.btnZaragoza;
        Button btnLerdo = binding.btnLerdo;
        Button btnMixto = binding.btnMixto;

        Button btnSaldo1 = binding.btnSaldo1;
        Button btnSaldo2 = binding.btnSaldo2;

        String tipoConvenioInformativo = "";
        TextView SaldoTitle = binding.SaldoTitle;
        LinearLayout saldoSegment = binding.saldoSegment;

        BtnOriginNational.setOnClickListener(v -> {
            Nacionalidad = "Mexico";
            BtnOriginUSA.setBackgroundResource(R.drawable.buttons2);
            BtnOriginNational.setBackgroundResource(R.drawable.buttons);
            btnZaragoza.setBackgroundResource(R.drawable.buttons2);
            btnLerdo.setBackgroundResource(R.drawable.buttons2);
            btnMixto.setBackgroundResource(R.drawable.buttons2);
            btnSaldo1.setBackgroundResource(R.drawable.buttons2);
            btnSaldo2.setBackgroundResource(R.drawable.buttons2);
            TarifasShow.setText("");
            NameShow.setText("");
            btnSaldo1.setText(saldo_zaragoza1_mx);
            btnSaldo2.setText(saldo_zaragoza2_mx);
        });

        BtnOriginUSA.setOnClickListener(v -> {
            Nacionalidad = "USA";
            BtnOriginNational.setBackgroundResource(R.drawable.buttons2);
            BtnOriginUSA.setBackgroundResource(R.drawable.buttons);
            btnZaragoza.setBackgroundResource(R.drawable.buttons2);
            btnLerdo.setBackgroundResource(R.drawable.buttons2);
            btnMixto.setBackgroundResource(R.drawable.buttons2);
            btnSaldo1.setBackgroundResource(R.drawable.buttons2);
            btnSaldo2.setBackgroundResource(R.drawable.buttons2);
            TarifasShow.setText("");
            NameShow.setText("");
           btnSaldo1.setText(saldo_zaragoza1_us);
           btnSaldo2.setText(saldo_zaragoza2_us);
        });

        btnZaragoza.setOnClickListener(v -> {
            saldoSel = false;
            NameShow.setVisibility(View.VISIBLE);
            TarifasShow.setEnabled(false);
            if (Nacionalidad.equals("")) {
                return;
            }
            ConvenioSaldo = "0";
            btnZaragoza.setBackgroundResource(R.drawable.buttons);
            btnLerdo.setBackgroundResource(R.drawable.buttons2);
            btnMixto.setBackgroundResource(R.drawable.buttons2);
            btnSaldo1.setBackgroundResource(R.drawable.buttons2);
            btnSaldo2.setBackgroundResource(R.drawable.buttons2);
            if (Nacionalidad.equals("Mexico")) {
                Convenio = anual_zaragoza_mx;
                ConvenioAnualidad = anual_zaragoza_mx;
                TarifasShow.setText("Tarifa: " + anual_zaragoza_mx);
                NameShow.setText("Puente Internacional Zaragoza");
            }
            if (Nacionalidad.equals("USA")) {
                Convenio = anual_zaragoza_us;
                ConvenioAnualidad = anual_zaragoza_us;
                TarifasShow.setText("Tarifa: " + anual_zaragoza_us);
                NameShow.setText("Puente Internacional Zaragoza");
            }
            textoPuenteSel = NameShow.getText().toString();

        });
        btnLerdo.setOnClickListener(v -> {
            saldoSel = false;
            NameShow.setVisibility(View.VISIBLE);
            TarifasShow.setEnabled(false);
            if (Nacionalidad.equals("")) {
                return;
            }
            ConvenioSaldo = "0";

            btnLerdo.setBackgroundResource(R.drawable.buttons);
            btnMixto.setBackgroundResource(R.drawable.buttons2);
            btnZaragoza.setBackgroundResource(R.drawable.buttons2);
            btnSaldo1.setBackgroundResource(R.drawable.buttons2);
            btnSaldo2.setBackgroundResource(R.drawable.buttons2);
            if (Nacionalidad.equals("Mexico")) {
                Convenio = anual_lerdo_mx;
                ConvenioAnualidad = anual_lerdo_mx;
                TarifasShow.setText("Tarifa: " + anual_lerdo_mx);
                NameShow.setText("Puente Internacional Lerdo");

            }
            if (Nacionalidad.equals("USA")) {
                Convenio = anual_lerdo_us;
                ConvenioAnualidad = anual_lerdo_us;
                TarifasShow.setText("Tarifa: " + anual_lerdo_us);
                NameShow.setText("Puente Internacional Lerdo");
            }
            textoPuenteSel = NameShow.getText().toString();
        });
        btnMixto.setOnClickListener(v -> {
            saldoSel = false;
            NameShow.setVisibility(View.VISIBLE);
            TarifasShow.setVisibility(GONE);
            NameShow.setVisibility(GONE);

            if (Nacionalidad.equals("")) {
                return;
            }
            ConvenioSaldo = "0";
            btnMixto.setBackgroundResource(R.drawable.buttons);
            btnLerdo.setBackgroundResource(R.drawable.buttons2);
            btnZaragoza.setBackgroundResource(R.drawable.buttons2);
            btnSaldo1.setBackgroundResource(R.drawable.buttons2);
            btnSaldo2.setBackgroundResource(R.drawable.buttons2);
            if (Nacionalidad.equals("Mexico")) {
                Convenio = anual_mixto_mx;
                ConvenioAnualidad = anual_mixto_mx;
                TarifasShow.setText("Tarifa: " + anual_mixto_mx);
                NameShow.setText("Mixto ");
            }
            if (Nacionalidad.equals("USA")) {
                Convenio = anual_mixto_us;
                ConvenioAnualidad = anual_mixto_us;
                TarifasShow.setText("Tarifa: " + anual_mixto_us);
                NameShow.setText("Mixto ");
            }
            textoPuenteSel = NameShow.getText().toString();

        });

        SaldoImage.setOnClickListener(view1 -> {
            tarifaspopupWindow.dismiss();
            System.out.println("Si me está clicando");
        });

        btnSelSaldo.setOnClickListener(view1 -> {
            Toast.makeText(requireContext(), "Toca para cerrar", Toast.LENGTH_SHORT).show();

            tarifaspopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            Toast.makeText(requireContext(), "Toca para cerrar", Toast.LENGTH_SHORT).show();
        });

        btnSaldo1.setOnClickListener(v -> {
            if (Nacionalidad == "") {
                return;
            }
            ConvenioAnualidad = "0";
            btnSaldo1.setBackgroundResource(R.drawable.buttons);
            btnSaldo2.setBackgroundResource(R.drawable.buttons2);
            btnLerdo.setBackgroundResource(R.drawable.buttons2);
            btnMixto.setBackgroundResource(R.drawable.buttons2);
            btnZaragoza.setBackgroundResource(R.drawable.buttons2);
            if (Nacionalidad.equals("Mexico")) {
                Convenio = saldo_zaragoza1_mx;
                ConvenioSaldo = saldo_zaragoza1_mx;
                TarifasShow.setText("Tarifa: " + saldo_zaragoza1_mx);
                NameShow.setText("Puente Internacional Zaragoza");
            }
            if (Nacionalidad.equals("USA")) {
                Convenio = saldo_zaragoza1_us;
                ConvenioSaldo = saldo_zaragoza1_us;
                TarifasShow.setText("Tarifa: " + saldo_zaragoza1_us);
                NameShow.setText("Puente Internacional Zaragoza");
            }
        });
        btnSaldo2.setOnClickListener(v -> {
            if (Nacionalidad == "") {
                return;
            }
            ConvenioAnualidad = "0";
            btnSaldo2.setBackgroundResource(R.drawable.buttons);
            btnSaldo1.setBackgroundResource(R.drawable.buttons2);
            btnZaragoza.setBackgroundResource(R.drawable.buttons2);
            btnLerdo.setBackgroundResource(R.drawable.buttons2);
            btnMixto.setBackgroundResource(R.drawable.buttons2);
            btnZaragoza.setBackgroundResource(R.drawable.buttons2);
            if (Nacionalidad.equals("Mexico")) {
                Convenio = saldo_zaragoza2_mx;
                ConvenioSaldo = saldo_zaragoza2_mx;
                TarifasShow.setText("Tarifa: " + saldo_zaragoza2_mx);
                NameShow.setText("Puente Internacional Zaragoza");
            }
            if (Nacionalidad.equals("USA")) {
                Convenio = saldo_zaragoza2_us;
                ConvenioSaldo = saldo_zaragoza2_us;
                TarifasShow.setText("Tarifa: " + saldo_zaragoza2_us);
                NameShow.setText("Puente Internacional Zaragoza");
            }
        });
        //Tipo de convenio


        sendFormBtn = binding.sendFormBtn;

        mSignaturePad = binding.signaturePad;
        Button clearSignatureBtn = binding.clearButton;
        Button SignBtn = binding.sendFormBtn;

        clearSignatureBtn.setOnClickListener(v -> {
            mSignaturePad.clearView();
        });
        final int[] Firma = {0};
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Firma[0] = 1;
            }

            @Override
            public void onSigned() {
                Firma[0] = 1;
            }

            @Override
            public void onClear() {
                Firma[0] = 0;
            }
        });


        //Starting info send
        sendFormBtn.setOnClickListener(v -> {
            counter = 0;
            String numSentri = "";
            String numSentriExp = "";


            if (AgregarVeh){
                numSentri = Sentri;
                numSentriExp = FechaSentri;
            }else {
                numSentri = noSentriInput.getText().toString();
                numSentriExp = expirationDateSentriInput.getText().toString();
            }


            String dom_calle = streetInput.getText().toString();
            String dom_numero_ext = noExtInput.getText().toString();
            String dom_colonia = ColonyInput.getText().toString();
            String dom_ciudad = CityInput.getText().toString();
            String dom_estado = StateInput.getText().toString();
            String dom_cp = CPInput.getText().toString();
            String fac_razon_social = BusinessNameInput.getText().toString();
            String fac_rfc = RFCInput.getText().toString();
            String fac_dom_fiscal = ResidenceInput.getText().toString(); //ResidenceInput es de facturación
            String fac_email = FactEmailInput.getText().toString();
            String fac_telefono = FactCelInput.getText().toString();
            String veh_marca = BrandVehicleInput.getText().toString();
            String veh_modelo = ModelVehicleInput.getText().toString();
            String veh_color = ColorVehicleInput.getText().toString();
            String veh_anio = YearVehicleInput.getText().toString();
            String veh_placas = PlatesVehicleInput.getText().toString();
            String veh_origen = Nacionalidad;


            String conv_saldo = null;
            String conv_anualidad = null;
            counter = 5000;
            handler = new Handler();


            if (vehiculos.size() == 0) {
                if (numSentri.equals("")) {
                    popup_Head.setText("No. SENTRI / PASS ID");
                    popup_Body.setText("Rellene este campo, por favor.");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);


                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (SentriPhotoImageView.getDrawable() == null) {
                    popup_Head.setText("Fotografía SENTRI");
                    popup_Body.setText("Se requiere de una fotografía de SENTRI");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (SentriPhotoImageView2.getDrawable() == null) {
                    popup_Head.setText("Fotografía SENTRI");
                    popup_Body.setText("Se requiere de una fotografía de SENTRI (reverso)");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (numSentriExp.equals("")) {
                    popup_Head.setText("SENTRI: Fecha de expiración");
                    popup_Body.setText("Rellene este campo, por favor.");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (IdOficialPhotoImageView.getDrawable() == null) {
                    popup_Head.setText("ID Oficial (Frontal)");
                    popup_Body.setText("Se requiere una fotografía de su ÏD (Frontal)");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (IdOficialReversePhotoImageView.getDrawable() == null) {
                    popup_Head.setText("ID Oficial (Reverso)");
                    popup_Body.setText("Se requiere una fotografía de su ÏD (Reverso)");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (IdOficialReversePhotoImageView2.getDrawable() == null) {
                    popup_Head.setText("ID Oficial (Reverso)");
                    popup_Body.setText("Se requiere una fotografía de su identificación");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }

                if (dom_calle.equals("")) {
                    popup_Head.setText("Domicilio: Calle");
                    popup_Body.setText("Rellene este campo, por favor.");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (dom_numero_ext.equals("")) {
                    popup_Head.setText("Domicilio: No. Exterior");
                    popup_Body.setText("Rellene este campo, por favor.");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (dom_colonia.equals("")) {
                    popup_Head.setText("Domicilio: Colonia");
                    popup_Body.setText("Rellene este campo, por favor.");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (dom_ciudad.equals("")) {
                    popup_Head.setText("Domicilio: Ciudad");
                    popup_Body.setText("Rellene este campo, por favor.");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (dom_estado.equals("")) {
                    popup_Head.setText("Domicilio: Estado");
                    popup_Body.setText("Rellene este campo, por favor.");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                if (dom_cp.equals("")) {
                    popup_Head.setText("Domicilio: Código Postal");
                    popup_Body.setText("Rellene este campo, por favor.");
                    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                    return;
                }
                //if (fac_razon_social.equals("")) {
                //    popup_Head.setText("Facturación: Razon Social");
                //    popup_Body.setText("Rellene este campo, por favor.");
                //    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                //    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                //    return;
                //}
                //if (fac_rfc.equals("")) {
                //    popup_Head.setText("Facturación: RFC");
                //    popup_Body.setText("Rellene este campo, por favor.");
                //    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                //    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                //    return;
                //}
                //if (fac_dom_fiscal.equals("")) {
                //    popup_Head.setText("Facturación: Domicilio");
                //    popup_Body.setText("Rellene este campo, por favor.");
                //    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                //    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                //    return;
                //}
                //if (fac_email.equals("")) {
                //    popup_Head.setText("Facturación: Correo Electronico");
                //    popup_Body.setText("Rellene este campo, por favor.");
                //    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                //    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                //    return;
                //}
                //if (fac_telefono.equals("")) {
                //    popup_Head.setText("Facturación: Número Cel.");
                //    popup_Body.setText("Rellene este campo, por favor.");
                //    popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                //    handler.postDelayed(() -> popup_Window.dismiss(), counter);
                //    return;
                //}
            }

            if (veh_marca.equals("")) {
                popup_Head.setText("Datos Vehiculo: Marca");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (veh_modelo.equals("")) {
                popup_Head.setText("Datos Vehiculo: Modelo");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (veh_color.equals("")) {
                popup_Head.setText("Datos Vehiculo: Color");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (veh_anio.equals("")) {
                popup_Head.setText("Datos Vehiculo: Año");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (veh_placas.equals("")) {
                popup_Head.setText("Datos Vehiculo: Placas");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (veh_origen.equals("")) {
                popup_Head.setText("Datos Vehiculo: Estado");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }

            if (TarjetaCirculacionPhotoImageView.getDrawable() == null) {
                popup_Head.setText("Tarjeta de circulación");
                popup_Body.setText("Se requiere una fotografía de su Tarjeta de circulación");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (PolizaSeguroPhotoImageView.getDrawable() == null) {
                popup_Head.setText("Poliza de seguro");
                popup_Body.setText("Se requiere una fotografía de su Poliza de seguro");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (AprovacionUSAPhotoImageView.getDrawable() == null) {
                popup_Head.setText("Aprobación por USA");
                popup_Body.setText("Se requiere una fotografía de su Aprobación por USA");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }


            if (Firma[0] == 0) {
                popup_Head.setText("Firma no válida");
                popup_Body.setText("Debe firmar el documento.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }

            Bitmap sign = mSignaturePad.getSignatureBitmap();
            bitmapToFile(requireContext(), sign, "Solicitud_Inscripcion_Firma.png");

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            if (AgregarVeh) {
                SendReqInscription(numSentri, numSentriExp, "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", localRazonSocial, localRFCFiscal, localDomFiscal, localEmailFiscal, localTelefonoFiscal, veh_marca, veh_modelo, veh_color, veh_anio, veh_placas, veh_origen, ConvenioSaldo, ConvenioAnualidad, Token);
            }else {
                SendReqInscription(numSentri, numSentriExp, dom_calle, dom_numero_ext, dom_colonia, dom_ciudad, dom_estado, dom_cp, fac_razon_social, fac_rfc, fac_dom_fiscal, fac_email, fac_telefono, veh_marca, veh_modelo, veh_color, veh_anio, veh_placas, veh_origen, ConvenioSaldo, ConvenioAnualidad, Token);
            }

            System.out.println(numSentri+ numSentriExp+ dom_calle+ dom_numero_ext+ dom_colonia+ dom_ciudad+ dom_estado+ dom_cp+ fac_razon_social+ fac_rfc+ fac_dom_fiscal+ fac_email+ fac_telefono+ veh_marca+ veh_modelo+ veh_color+ veh_anio+ veh_placas+ veh_origen+ conv_saldo+ conv_anualidad+ Token);
            






        });

        //Permissions
        if (checkPermission()) {
            Log.i("InscriptionReq", "onCreate: Permissions Ok");
        } else {
            requestPermission(); // Request Permission
        }
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult( ActivityResult result ) {
                System.out.println("yesPerm Almacenamiento");
            }
        });
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.CAMERA
        };
        if (!hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_ALL);
        }


        uploadSentriPhotoBtn.setOnClickListener(v -> {
            currentImageView = SentriPhotoImageView;
            takePicture("Photo_Sentri", "Solicitud_Inscripcion_PhotoSentri");
            SentriPhotoImageView.setVisibility(View.VISIBLE);
        });

        uploadSentriPhotoBtn2.setOnClickListener(v -> {
            currentImageView = SentriPhotoImageView2;
            takePicture("Photo_Sentri2", "Solicitud_Inscripcion_PhotoSentri2");
            SentriPhotoImageView2.setVisibility(View.VISIBLE);
        });

        uploadOfficialIDPhotoBtn.setOnClickListener(v -> {
            currentImageView = IdOficialPhotoImageView;
            takePicture("Photo_idOficial", "Solicitud_Inscripcion_PhotoidOficial");
            IdOficialPhotoImageView.setVisibility(View.VISIBLE);
        });
        uploadOfficialReverseIDPhotoBtn.setOnClickListener(v -> {
            currentImageView = IdOficialReversePhotoImageView;
<<<<<<< Updated upstream
            takePicture("Photo_idOficialReverse", "Solicitud_Inscripcion_PhotoidOficialReverse");
=======
            takePicture("Photo_idOficialReverse", "Solicitud-Inscripcion-Photo_idOficialReverse");
>>>>>>> Stashed changes
            IdOficialReversePhotoImageView.setVisibility(View.VISIBLE);
        });
        uploadOfficialReverseIDPhotoBtn2.setOnClickListener(v -> {
            currentImageView = IdOficialReversePhotoImageView2;
            takePicture("Photo_idOficialReverse2", "Solicitud-Inscripcion-Photo_idOficialReverse2");
            IdOficialReversePhotoImageView2.setVisibility(View.VISIBLE);
        });
        uploadCirculationCardPhotoBtn.setOnClickListener(v -> {
            currentImageView = TarjetaCirculacionPhotoImageView;
            takePicture("Photo_tarjetaCirculacion", "Solicitud_Inscripcion_PhototarjetaCirculacion");
            TarjetaCirculacionPhotoImageView.setVisibility(View.VISIBLE);
        });
        uploadInsurancePolicyBtn.setOnClickListener(v -> {
            currentImageView = PolizaSeguroPhotoImageView;
            takePicture("Photo_polizaSeguro", "Solicitud_Inscripcion_PhotopolizaSeguro");
            PolizaSeguroPhotoImageView.setVisibility(View.VISIBLE);
        });
        uploadInsurancePolicyBtnSecond.setOnClickListener(v -> {
            currentImageView = PolizaSeguroPhotoImageViewSecond;
            takePicture("Photo_polizaSeguro2", "Photo_polizaSeguro2");
            currentImageView.setVisibility(View.VISIBLE);
        });
        uploadUSAApprovalBtn.setOnClickListener(v -> {
            currentImageView = AprovacionUSAPhotoImageView;
            takePicture("Photo_AprovacionUSA", "Solicitud_Inscripcion_PhotoaprovacionVehiculoUSA");
            AprovacionUSAPhotoImageView.setVisibility(View.VISIBLE);
        });
        uploadPowerAttorney.setOnClickListener(v -> {
            currentImageView = CartaPoderPhotoImageView;
            takePicture("Photo_cartaPoder", "Solicitud_Inscripcion_PhotocartaPoder");
            CartaPoderPhotoImageView.setVisibility(View.VISIBLE);
        });





        return root;
    }

    private void updateLabel(){
        String myFormat="yyyy/MM/dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        expirationDateSentriInput.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("photo_uri_temp", PhotoUriTemp);
        outState.putString("currentImageView", String.valueOf(currentImageView));
    }

    public File bitmapToFile(Context context,Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;

        try {

            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Data/mx.gob.puentesfronterizos.lineaexpres/files/Pictures/" + fileNameToSave);
            file.createNewFile();
            UpdateData.InsertTramites("FIRMA", file.toString());

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file; // it will return null
        }
    }

    public void SendReqInscription(String noSentri, String expirationDateSentri, String domCalle, String domNumeroExt, String domColonia, String domCiudad, String domEstado, String domCP, String facRazonSocial, String facRFC, String facDomFiscal, String facEmail, String facTelefono, String vehMarca, String vehModelo, String vehColor, String vehAnio, String vehPlacas, String vehOrigen, String convSaldo, String convAnualidad, String UserToken){
        new Thread(() -> {
            try {

                InputStream inputStream;
<<<<<<< Updated upstream
                String url_process = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/procs/p01";
=======
                //String url_process = getResources().getString(R.string.apiURL) + "api/v1/procs/p01";
                String url_process = "https://apis.fpfch.gob.mx/api/v1/procs/p01";
>>>>>>> Stashed changes

                URL url = new URL(url_process);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + UserToken);
                conn.setRequestMethod("POST");

                String noSentriClean = noSentri;
                String expirationDateSentriClean = expirationDateSentri;
                String domCalleClean = domCalle;
                String domNumeroExtClean = domNumeroExt;
                String domColoniaClean = domColonia;
                String domCiudadClean = domCiudad;
                String domEstadoClean = domEstado;
                String domCPClean = domCP;
                String facRazonSocialClean = facRazonSocial;
                String facRFCClean = facRFC;
                String facDomFiscalClean = facDomFiscal;
                String facEmailClean = facEmail;
                String facTelefonoClean = facTelefono;
                String vehMarcaClean = vehMarca;
                String vehModeloClean = vehModelo;
                String vehColorClean = vehColor;
                String vehAnioClean = vehAnio;
                String vehPlacasClean = vehPlacas;
                String vehOrigenClean = vehOrigen;


                if (noSentri.endsWith(" ")) {
                    noSentriClean = noSentri.substring(0, noSentri.length() - 1);
                }if (expirationDateSentri.endsWith(" ")) {
                    expirationDateSentriClean = expirationDateSentri.substring(0, expirationDateSentri.length() - 1);
                }if (domCalle.endsWith(" ")) {
                    domCalleClean = domCalle.substring(0, domCalle.length() - 1);
                }if (domNumeroExt.endsWith(" ")) {
                    domNumeroExtClean = domNumeroExt.substring(0, domNumeroExt.length() - 1);
                }if (domColonia.endsWith(" ")) {
                    domColoniaClean = domColonia.substring(0, domColonia.length() - 1);
                }if (domCiudad.endsWith(" ")) {
                    domCiudadClean = domCiudad.substring(0, domCiudad.length() - 1);
                }if (domEstado.endsWith(" ")) {
                    domEstadoClean = domEstado.substring(0, domEstado.length() - 1);
                }if (domCP.endsWith(" ")) {
                    domCPClean = domCP.substring(0, domCP.length() - 1);
                }if (facRazonSocial.endsWith(" ")) {
                    facRazonSocialClean = facRazonSocial.substring(0, facRazonSocial.length() - 1);
                }if (facRFC.endsWith(" ")) {
                    facRFCClean = facRFC.substring(0, facRFC.length() - 1);
                }if (facDomFiscal.endsWith(" ")) {
                    facDomFiscalClean = facDomFiscal.substring(0, facDomFiscal.length() - 1);
                }if (facEmail.endsWith(" ")) {
                    facEmailClean = facEmail.substring(0, facEmail.length() - 1);
                }if (facTelefono.endsWith(" ")) {
                    facTelefonoClean = facTelefono.substring(0, facTelefono.length() - 1);
                }if (vehMarca.endsWith(" ")) {
                    vehMarcaClean = vehMarca.substring(0, vehMarca.length() - 1);
                }if (vehModelo.endsWith(" ")) {
                    vehModeloClean = vehModelo.substring(0, vehModelo.length() - 1);
                }if (vehColor.endsWith(" ")) {
                    vehColorClean = vehColor.substring(0, vehColor.length() - 1);
                }if (vehAnio.endsWith(" ")) {
                    vehAnioClean = vehAnio.substring(0, vehAnio.length() - 1);
                }if (vehPlacas.endsWith(" ")) {
                    vehPlacasClean = vehPlacas.substring(0, vehPlacas.length() - 1);
                }if (vehOrigen.endsWith(" ")) {
                    vehOrigenClean = vehOrigen.substring(0, vehOrigen.length() - 1);
                }


                JSONObject jsonParam = new JSONObject();
                jsonParam.put("sentri", noSentriClean);
                jsonParam.put("sentri_vencimiento", expirationDateSentriClean);
                jsonParam.put("dom_calle", domCalleClean);
                jsonParam.put("dom_numero_ext", domNumeroExtClean);
                jsonParam.put("dom_colonia", domColoniaClean);
                jsonParam.put("dom_ciudad", domCiudadClean);
                jsonParam.put("dom_estado", domEstadoClean);
                jsonParam.put("dom_cp", domCPClean);
                jsonParam.put("fac_razon_social", facRazonSocialClean);
                jsonParam.put("fac_rfc", facRFCClean);
                jsonParam.put("fac_dom_fiscal", facDomFiscalClean);
                jsonParam.put("fac_email", facEmailClean);
                jsonParam.put("fac_telefono", facTelefonoClean);
                jsonParam.put("veh_marca", vehMarcaClean);
                jsonParam.put("veh_modelo", vehModeloClean);
                jsonParam.put("veh_color", vehColorClean);
                jsonParam.put("veh_anio", vehAnioClean);
                jsonParam.put("veh_placas", vehPlacasClean);
                jsonParam.put("veh_origen", vehOrigenClean);
                jsonParam.put("puente_sel", textoPuenteSel);
                jsonParam.put("conv_saldo", convSaldo);
                jsonParam.put("conv_anualidad", convAnualidad);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                int Status = conn.getResponseCode();
                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                }else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);


                ResponseData = ResponseData.replaceAll("Message sent", "");
                System.out.println("Este es el response Data de inscrpition  " + ResponseData);


                JSONObject Result = new JSONObject(ResponseData);
                String msgErr = "No error";
                if (Status == 500) {
                    msgErr = (String) Result.get("text");
                    String finalMsgErr = msgErr;
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Error interno", Toast.LENGTH_SHORT).show();
                        UpdateData.InsertErrores(finalMsgErr);
                        Toast.makeText(requireContext(), finalMsgErr, Toast.LENGTH_SHORT).show();
                    } );
                    return;
                }

                UpdateData.InsertErrores(msgErr);
                String messageRes = "";
                try {
                    messageRes = (String) Result.get("message");
                }catch (Exception e) {
                    Log.e("TAG", "SendReqInscription: ", e);
                    requireActivity().runOnUiThread(() -> {
                        popup_Head.setText("Error interno");
                        popup_Body.setText("Reintente el proceso por favor.");
                        popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                        handler.postDelayed(() -> popup_Window.dismiss(), counter);
                        popupWindow.dismiss();
                    });
                }


                if (Status != 200) {
                    try {
                        String Message = Result.get("message").toString();
                        String messageResError = Result.get("errors").toString();
                        requireActivity().runOnUiThread(() -> {
                            popup_Head.setText(Message);
                            popup_Body.setText(messageResError);
                            popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                            handler.postDelayed(() -> popup_Window.dismiss(), counter);
                            popupWindow.dismiss();
                        });
                    }catch (Exception e){
                        requireActivity().runOnUiThread(() -> {
                            popup_Head.setText("Error interno");
                            popup_Body.setText("Reintente el proceso por favor.");
                            popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                            handler.postDelayed(() -> popup_Window.dismiss(), counter);
                            popupWindow.dismiss();
                        });
                    }
                }

                if (messageRes.contains("registrado")) {

                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Procesando trámite", Toast.LENGTH_SHORT).show();
                        Toast.makeText(requireContext(), "No salga de esta pantalla por favor", Toast.LENGTH_SHORT).show();

                    });

                    getIdProc TramiteProceso = new getIdProc();
                    Thread thread = new Thread(TramiteProceso);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ArrayList<String> value = TramiteProceso.getValue();
                    String Tramite_ID = value.get(0);
                    String Tramite_Estatus = value.get(1);
                    String Tramite_Proceso = value.get(2);

                    try {
                        ArrayList<String> getFullNameTramites = UpdateData.getTramitesArray();
                        for (int i = 0; i < getFullNameTramites.size(); i++) {
                            try {

                                if (getFullNameTramites.get(i).isEmpty()) {
                                    continue;
                                }
                                if (getFullNameTramites.get(i).toString().contains("null")) {
                                    continue;
                                }

                                System.out.println("Este es el nombre de getFullName: " + getFullNameTramites.get(i));

                                int newi = i+1;

                                System.out.println("Este es el nombre de newi: " + newi);

                                int FileTypeCount = 0;

                                if (newi == 1) {
                                    //File for "Sentri"
                                    FileTypeCount = 1;
                                }
                                if (newi == 2) {
                                    //File for "id Oficial Frontal"
                                    FileTypeCount = 2;
                                }
                                if (newi == 3) {
                                    //File for "id Oficial Reverso"
                                    FileTypeCount = 3;
                                }
                                if (newi == 4) {
                                    //File for "Tarjeta Circ"
                                    FileTypeCount = 4;
                                }
                                if (newi == 5) {
                                    //File for "Poliza 1"
                                    FileTypeCount = 5;
                                }
                                if (newi == 6) {
                                    //File for "Poliza 2"
                                    FileTypeCount = 9;
                                }
                                if (newi == 7) {
                                    //File for "aprobación vehiculo"
                                    FileTypeCount = 6;
                                }
                                if (newi == 8) {
                                    //File for "cartaPoder"
                                    FileTypeCount = 7;
                                }
                                if (newi == 9) {
                                    //File for sign
                                    FileTypeCount = 8;
                                }
                                if (newi == 10){
                                    //File por Sentri(Rev)
                                    FileTypeCount = 9;
                                }
                                if (newi == 11){
                                    //File for official identificaction
                                    FileTypeCount = 10;
                                }


                                if (newi == getFullNameTramites.size()) {
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(requireContext(), "Procesando trámite", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(requireContext(), "No salga de esta pantalla por favor", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(requireContext(), "Trámite registrado correctamente", Toast.LENGTH_SHORT).show();

                                        handler.postDelayed(afterProcc, 10000);

                                    });
                                }


                                int finalFileTypeCount = FileTypeCount;
                                int finalI = i;
                                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                                    try {
                                        sendFile(String.valueOf(Tramite_ID), getFullNameTramites.get(finalI), String.valueOf(finalFileTypeCount));
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                                try {
                                    future.get(); // Espera a que la tarea se complete
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Procesando trámite", Toast.LENGTH_SHORT).show();
                            Toast.makeText(requireContext(), "No salga de esta pantalla por favor", Toast.LENGTH_SHORT).show();
                            Toast.makeText(requireContext(), "Trámite registrado correctamente", Toast.LENGTH_SHORT).show();

                            handler.postDelayed(afterProcc, 10000);

                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }


    final Runnable afterProcc = new Runnable() {
        public void run() {
            requireActivity().runOnUiThread(() -> {
                popupWindow.dismiss();
                MainActivity.nav_req(R.id.navigation_profile);
            });
        }
    };

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

    public void sendFile(String Tramite_ID, String FileUpdate, String FileType) throws Exception {
        new Thread(() -> {
            try {
                System.out.println("Este Tramite_ID :" + Tramite_ID);
                System.out.println("Este FileUpdate :" + FileUpdate);
                System.out.println("Este FileType :" + FileType);
                Uri uri = null;
                if (photoOne != null && Integer.parseInt(FileType) == 1) {uri = photoOne; photoOne = null;}
                if (photoTwo != null && Integer.parseInt(FileType) == 1) {uri = photoTwo; photoTwo = null;}
                if (photoThree != null && Integer.parseInt(FileType) == 2) {uri = photoThree; photoThree = null;}
                if (photoFour != null && Integer.parseInt(FileType) == 3) {uri = photoFour; photoFour = null;}
                if (photoFive != null && Integer.parseInt(FileType) == 2) {uri = photoFive; photoFive = null;}
                if (photoSix != null && Integer.parseInt(FileType) == 4) {uri = photoSix; photoSix = null;}
                if (photoSeven != null && Integer.parseInt(FileType) == 5) {uri = photoSeven; photoSeven = null;}
                if (photoEighth != null && Integer.parseInt(FileType) == 6) {uri = photoEighth; photoEighth = null;}
                if (photoNine != null && Integer.parseInt(FileType) == 7) {uri = photoNine; photoNine = null;}
                if (photoTen != null && Integer.parseInt(FileType) == 8) {uri = photoTen; photoTen = null;}
                System.out.println("Este para el filePath?: " + uri);

                String filePath = null;
                String[] projection = {MediaStore.Images.Media.DATA};
                if (uri != null) {
                    if (DocumentsContract.isDocumentUri(requireContext(), uri)) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        String mimeType = requireActivity().getContentResolver().getType(uri);
                        if (mimeType != null && mimeType.startsWith("image/")) {
                            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            String selection = MediaStore.Images.Media._ID + "=?";

                            String[] selectionArgs = new String[]{docId.substring(6)};
                            Cursor cursor = requireActivity().getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                filePath = cursor.getString(columnIndex);
                                System.out.println("Este es el filePath: " + filePath);
                                RequestBody requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("id_proc", Tramite_ID)
                                        .addFormDataPart("docfile", "img", RequestBody.create(MEDIA_TYPE_PNG, new File(String.valueOf(filePath))))
                                        .addFormDataPart("filetype", FileType)
                                        .addFormDataPart("id_proc_type", numTramite)
                                        .build();
                                Request request = new Request.Builder()
                                        .header("Authorization", "Bearer " + Token)
<<<<<<< Updated upstream
                                        .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
=======
                                        //.url(getResources().getString(R.string.apiURL) + "api/v1/files")
                                        .url("https://apis.fpfch.gob.mx/api/v1/files")
>>>>>>> Stashed changes
                                        .post(requestBody)
                                        .build();
                                Response response = client.newCall(request).execute();
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);
                                System.out.println(response.body().string());
                                cursor.close();
                            }
                        } else if (mimeType != null && mimeType.equals("application/pdf")) {
                            try {
                                String numericPart = docId.substring(docId.indexOf(":") + 1);
                                long id = Long.parseLong(numericPart);
                                Uri contentUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id);
                                String selection = null;
                                String[] selectionArgs = null;
                                Cursor cursor = requireActivity().getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                                    filePath = cursor.getString(columnIndex);

                                    RequestBody requestBody = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("id_proc", Tramite_ID)
                                            .addFormDataPart("docfile", "document", RequestBody.create(MediaType.parse("application/pdf"), new File(filePath)))
                                            .addFormDataPart("filetype", FileType)
                                            .addFormDataPart("id_proc_type", numTramite)
                                            .build();

                                    Request request = new Request.Builder()
                                            .header("Authorization", "Bearer " + Token)
<<<<<<< Updated upstream
                                            .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
=======
                                            //.url(getResources().getString(R.string.apiURL) + "api/v1/files")
                                            .url("https://apis.fpfch.gob.mx/api/v1/files")
>>>>>>> Stashed changes
                                            .post(requestBody)
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    if (!response.isSuccessful())
                                        throw new IOException("Unexpected code " + response);
                                    System.out.println(response.body().string());
                                    cursor.close();
                                }
                            } catch (Exception e) {
                                System.out.println("Es el error: " + e);
                            }
                        }
                    }
                }else {
                    try {
                        String imageLocation = FileUpdate.substring(FileUpdate.lastIndexOf('/') + 1);
                        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Data/mx.gob.puentesfronterizos.lineaexpres/files/Pictures/" + imageLocation);

                        //Start to change width, but firstly changing to Bitmap

                        Matrix matrix = new Matrix();
                        matrix.postRotate(0);
                        Bitmap b = BitmapFactory.decodeFile(storageDir.getAbsolutePath());

                        Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);

                        // original measurements
                        int origWidth = b2.getWidth();
                        int origHeight = b2.getHeight();

                        final int destWidth = 1024;//or the width you need

                        if(origWidth > destWidth){
                            // picture is wider than we want it, we calculate its target height
                            int destHeight = origHeight/( origWidth / destWidth ) ;
                            //Rotate again the image

                            // we create an scaled bitmap so it reduces the image, not just trim it
                            Bitmap b3 = Bitmap.createScaledBitmap(b2, destWidth, destHeight, false);
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            // compress to the format you want, JPEG, PNG...
                            // 70 is the 0-100 quality percentage
                            //b2.compress(Bitmap.CompressFormat.JPEG,70 , outStream);


                            b3.compress(Bitmap.CompressFormat.JPEG,70 , outStream);
                            // we save the file, at least until we have made use of it
                            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Data/mx.gob.puentesfronterizos.lineaexpres/files/Pictures/" + imageLocation);
                            //File f = new File(Environment.getExternalStorageDirectory() + File.separator + "test.jpg");
                            f.createNewFile();
                            //write the bytes in file
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(outStream.toByteArray());
                            // remember close de FileOutput
                            fo.close();
                        }
                        //Finishing to change width, but firstly changing to Bitmap

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id_proc", Tramite_ID)
                                .addFormDataPart("docfile", imageLocation, RequestBody.create(MEDIA_TYPE_PNG, new File(String.valueOf(storageDir))))
                                .addFormDataPart("filetype", FileType)
                                .addFormDataPart("id_proc_type", numTramite)
                                .build();

                        Request request = new Request.Builder()
                                .header("Authorization", "Bearer " + Token)
<<<<<<< Updated upstream
                                .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
=======
                                //.url(getResources().getString(R.string.apiURL) + "api/v1/files")
                                .url("https://apis.fpfch.gob.mx/api/v1/files")
>>>>>>> Stashed changes
                                .post(requestBody)
                                .build();

                        Response response = client.newCall(request).execute();
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                        System.out.println(response.body().string());
                    }catch (Exception e){
                        Log.e("SendFiles", "sendFile: ", e);
                    }
                }

            }catch (Exception e){
                Log.e("SendFiles", "sendFile: ", e);
            }
        }).start();
    }


    public class getIdProc implements Runnable {
        private volatile ArrayList<String> getTramites = new ArrayList<>();

        @Override
        public void run() {
            try {
                InputStream inputStream;
                String url_process = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/procs";

                URL url = new URL(url_process);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("GET");

                inputStream = new BufferedInputStream(conn.getInputStream());

                String ResponseData = convertStreamToString(inputStream);
                ResponseData = ResponseData.replaceAll("Message sent", "");
                JsonArray jsonArray = JsonParser.parseString(ResponseData).getAsJsonArray();

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject(); //Converting Json Array to JsonObjects
                    String tramite_id = jsonObject.get("id").getAsString(); // getting id
                    String tramite_status = jsonObject.get("tramite_status").getAsString(); // getting status
                    String id_procedure = jsonObject.get("id_procedure").getAsString(); // getting procedure
                    if (tramite_status.contains("cancelado")) {
                        return;
                    }

                    getTramites.add(tramite_id);
                    getTramites.add(tramite_status);
                    getTramites.add(id_procedure);

                }

                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public ArrayList<String> getValue() {
            return getTramites;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_Notification(String msgTitle, String msgBody){

        Intent intent=new Intent(requireContext(), MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"All", NotificationManager.IMPORTANCE_HIGH);
        PendingIntent pendingIntent=PendingIntent.getActivity(requireContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);
        Notification notification=new Notification.Builder(requireContext(),CHANNEL_ID)
                .setContentText(msgBody)
                .setContentTitle(msgTitle)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),                                                                                                 R.drawable.ic_stat_name))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();

        NotificationManager notificationManager=(NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);
    }

    //Take photo
    static final int REQUEST_TAKE_PHOTO = 1;
    @SuppressLint("SourceLockedOrientationActivity")
    private void takePicture(String Tramite, String imageName) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        pickPhoto.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        Intent chooserIntent = Intent.createChooser(pickPhoto, "Selecciona una foto");

        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        try {

            imageFile = createImageFile(imageName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (imageFile != null) {
            Uri photoURI = FileProvider.getUriForFile(requireContext(), "mx.gob.puentesfronterizos.lineaexpres", imageFile);
            openCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            UpdateData.InsertTramites(Tramite, photoURI.toString());
            PhotoUriTemp = UpdateData.getTramites(Tramite);
            System.out.println("ImageFile" + imageFile);
            Intent[] options = {openCamera};

            if (chooserIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                options = new Intent[]{pickPhoto, openCamera};
            }

            Intent chooser = Intent.createChooser(new Intent(), "Selecciona una foto");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, options);

            startActivityForResult(chooser, REQUEST_TAKE_PHOTO);
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        if (resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                //Manejamos datos de la galeria
                Uri selectedImage = data.getData();

                if (currentTramiteFoto.contains("Photo_Sentri")) {photoOne = selectedImage;}
                if (currentTramiteFoto.contains("Photo_Sentri2")) {photoTwo = selectedImage;}
                if (currentTramiteFoto.contains("Photo_idOficial")) {photoThree = selectedImage;}
                if (currentTramiteFoto.contains("Photo_idOficialReverse")) {photoFour = selectedImage;}
                if (currentTramiteFoto.contains("Photo_idOficialReverse2")) {photoFive = selectedImage;}
                if (currentTramiteFoto.contains("Photo_tarjetaCirculacion")) {photoSix = selectedImage;}
                if (currentTramiteFoto.contains("Photo_polizaSeguro")) {photoSeven = selectedImage;}
                if (currentTramiteFoto.contains("Photo_polizaSeguro2")) {photoEighth = selectedImage;}
                if (currentTramiteFoto.contains("Photo_AprovacionUSA")) {photoNine = selectedImage;}
                if (currentTramiteFoto.contains("Photo_cartaPoder")) {photoTen = selectedImage;}

                try {
                    bitmap = rotateImage(MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),selectedImage), 0);
                    currentImageView.setImageBitmap(bitmap);

                    InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedImage);

                    Uri photoUri = Uri.parse(PhotoUriTemp);
                    File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    String imageName = photoUri.getLastPathSegment();
                    File imageFile = new File(storageDir, imageName);

                    OutputStream outputStream = new FileOutputStream(imageFile);
                    byte[] buffer = new byte[4 * 1024]; // tamaño del buffer de lectura
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                //Manejamos datos de la camara
                String bitmapImage = PhotoUriTemp;
                try {
                    System.out.println("Lo de la camara es: " + bitmapImage.toString());
                    bitmap = rotateImage(MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), Uri.parse(bitmapImage)), 90);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentImageView.setImageBitmap(bitmap);
            }
        }


        if (resultCode == RESULT_CANCELED) {
            currentImageView.setImageResource(0);
        }
    }

    private File createImageFile(String imageName) throws IOException {
        // Create an image file name

        String imageFilename = imageName + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFilename, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();

        // Create a content values object for the image file
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, image.getName());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // Create a document request intent
        Intent createDocumentIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        createDocumentIntent.addCategory(Intent.CATEGORY_OPENABLE);
        createDocumentIntent.setType("image/jpeg");
        createDocumentIntent.putExtra(Intent.EXTRA_TITLE, image.getName());

        // Check if the intent can be resolved
        if (createDocumentIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Start the activity to request write permission
            startActivityForResult(createDocumentIntent, WRITE_REQUEST_CODE);
        }
        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) throws IOException {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap b2 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        // original measurements
        int origWidth = b2.getWidth();
        int origHeight = b2.getHeight();

        final int destWidth = origWidth / 20;
        final int destHeight = origHeight / 20;
        Bitmap b3 = null;
        b3 = Bitmap.createScaledBitmap(b2, destWidth, destHeight, false);

        return b3;
    }

    //Permission
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE);
            int cameraCheck = ContextCompat.checkSelfPermission(requireContext(), CAMERA);
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED && cameraCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    private String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //new AlertDialog.Builder(requireContext())
            //        .setTitle("Permisos de almacenamiento")
            //        .setMessage("Al dar 'Aceptar' se abrirá un menú, en el cual deberá seleccionar la aplicación y dar los permisos \n 'Permitir administrar todos los archivos'' ")
            //        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            //            public void onClick( DialogInterface dialog, int which ) {
            //                try {
            //                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            //                    intent.addCategory("android.intent.category.DEFAULT");
            //                    intent.setData(Uri.parse(String.format("package:%s", requireContext().getPackageName())));
            //                    activityResultLauncher.launch(intent);
            //                } catch (Exception e) {
            //                    Intent intent = new Intent();
            //                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            //                    activityResultLauncher.launch(intent);
            //                }
            //            }
            //        })
            //        .setCancelable(false)
            //        .show();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), permissions, 30);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void getPrices(){
        new Thread(() -> {
<<<<<<< Updated upstream
            String jsonURL = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/config/mobile";
=======
            //String jsonURL = getResources().getString(R.string.apiURL) + "api/v1/config/mobile";
            String jsonURL = "https://apis.fpfch.gob.mx/api/v1/config/mobile";
>>>>>>> Stashed changes
            URL url;
            try {
                url = new URL(jsonURL);
                URLConnection request = url.openConnection();
                request.connect();
                JsonObject jsonObj = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();

                String ytLink = jsonObj.get("mbVideoURL").getAsString();

                anual_zaragoza_mx = (String) jsonObj.get("anual_zaragoza_mx").getAsString();
                anual_lerdo_mx = (String) jsonObj.get("anual_lerdo_mx").getAsString();
                anual_zaragoza_us = (String) jsonObj.get("anual_zaragoza_us").getAsString();
                anual_lerdo_us = (String) jsonObj.get("anual_lerdo_us").getAsString();
                anual_mixto_mx = jsonObj.get("anual_mixto_mx").getAsString();
                anual_mixto_us = jsonObj.get("anual_mixto_us").getAsString();
                saldo_zaragoza1_mx = (String) jsonObj.get("saldo_zaragoza1_mx").getAsString();
                saldo_zaragoza2_mx = (String) jsonObj.get("saldo_zaragoza2_mx").getAsString();
                saldo_zaragoza1_us = (String) jsonObj.get("saldo_zaragoza1_us").getAsString();
                saldo_zaragoza2_us = (String) jsonObj.get("saldo_zaragoza2_us").getAsString();
                pago_minimotp_mx = jsonObj.get("pago_minimotp_mx").getAsString();
                mbPreciosURL = jsonObj.get("mbPreciosURL").getAsString();

                System.out.println(mbPreciosURL);

                requireActivity().runOnUiThread(() -> {
                    Glide.with(requireContext())
                            .load(mbPreciosURL)
                            .into(SaldoImage);
                });



            } catch (IOException e) {
                Log.e("Youtube Embed", "setYoutubeEmbeded: ", e);
            }
        }).start();
    }
}