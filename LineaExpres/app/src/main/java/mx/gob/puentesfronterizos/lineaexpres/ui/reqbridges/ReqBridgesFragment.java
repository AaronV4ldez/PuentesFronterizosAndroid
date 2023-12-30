package mx.gob.puentesfronterizos.lineaexpres.ui.reqbridges;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import androidx.appcompat.app.AlertDialog;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentReqbridgesBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
import mx.gob.puentesfronterizos.lineaexpres.ui.reqinscription.ReqInscriptionFragment;
import mx.gob.puentesfronterizos.lineaexpres.ui.reqinscription.ReqInscriptionViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ReqBridgesFragment extends Fragment {

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
    RadioGroup PlatesStateRG;
    EditText OtherPlates;
    RadioGroup group;
    RadioGroup group2;
    EditText AnualidadOSaldo;

    ImageView SentriPhotoImageView;
    ImageView IdOficialPhotoImageView;
    ImageView IdOficialReversePhotoImageView;


    //Layout Photo Declarations
    Button uploadSentriPhotoBtn;
    Button uploadOfficialIDPhotoBtn;
    Button uploadOfficialReverseIDPhotoBtn;

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

    LayoutInflater loaderInflater;
    View popupView;
    View view;
    int width;
    int height;
    PopupWindow popupWindow;
    final Calendar myCalendar= Calendar.getInstance();

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private FragmentReqbridgesBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReqInscriptionViewModel reqBrdigeViewModel =
                new ViewModelProvider(this).get(ReqInscriptionViewModel.class);

        binding = FragmentReqbridgesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        userLog = new UserLog(requireContext()); //Open Users db
        UpdateData = new updateData(requireContext()); //Open Users db
        SQLOnInit sqlOnInit = new SQLOnInit(requireContext()); //Open Users db
        sqlOnInit.cleanTramites();
        sqlOnInit.InsertTramitesOnInit();

        //EditText
        noSentriInput = binding.noSentriInput;
        expirationDateSentriInput = binding.expirationDateSentriInput;
        nameInput = binding.nameInput;
        middleNameInput = binding.middleNameInput;
        lastNameInput = binding.lastNameInput;
        PersonalEmailInput = binding.PersonalEmailInput;
        PersonalCelInput = binding.PersonalCelInput;
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
        PlatesStateRG = binding.PlatesStateRG;
        OtherPlates = binding.OtherPlates;
        AnualidadOSaldo = binding.AnualidadOSaldo;

        uploadSentriPhotoBtn = binding.uploadSentriPhotoBtn;
        uploadOfficialIDPhotoBtn = binding.uploadOfficialIDPhotoBtn;
        uploadOfficialReverseIDPhotoBtn = binding.uploadOfficialReverseIDPhotoBtn;

        SentriPhotoImageView = binding.SentriPhotoImageView;
        IdOficialPhotoImageView = binding.IdOficialPhotoImageView;
        IdOficialReversePhotoImageView = binding.IdOficialReversePhotoImageView;

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

        Token = userLog.GetUserData().get(1);


        PlatesStateRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = PlatesStateRG.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.ChihRadioButton:
                        OtherPlates.getText().clear();
                        OtherPlates.setText("Chihuahua");
                        OtherPlates.setVisibility(View.GONE);
                        break;
                    case R.id.TxRadioButton:
                        OtherPlates.getText().clear();
                        OtherPlates.setText("Texas");
                        OtherPlates.setVisibility(View.GONE);
                        break;
                    case R.id.OtherRadioButton:
                        OtherPlates.getText().clear();
                        OtherPlates.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });


        group = (RadioGroup) binding.BridgesRG;
        group2 = (RadioGroup) binding.BalanceRG;

        sendFormBtn = binding.sendFormBtn;


        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = group.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.ZaragozaRadioButton:
                        AnualidadOSaldo.getText().clear();
                        AnualidadOSaldo.setText("10000");
                        group2.clearCheck();
                        break;
                    case R.id.LerdoRadioButton:
                        AnualidadOSaldo.getText().clear();
                        AnualidadOSaldo.setText("10001");
                        group2.clearCheck();
                        break;
                    case R.id.MixRadioButton:
                        AnualidadOSaldo.getText().clear();
                        AnualidadOSaldo.setText("10002");
                        group2.clearCheck();
                        break;
                }
            }
        });


        group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = group2.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.TwoThousandRadioButton:
                        AnualidadOSaldo.getText().clear();
                        AnualidadOSaldo.setText("2000");
                        group.clearCheck();
                        break;
                    case R.id.FourThousandButton:
                        AnualidadOSaldo.getText().clear();
                        AnualidadOSaldo.setText("4000");
                        group.clearCheck();
                        break;
                }
            }
        });


        TextView SentriImportant = binding.SentriImportant;
        SentriImportant.setText(Html.fromHtml("Necesitas como requisito tu SENTRI, si no lo tienes, <a href='https://ttp.cbp.dhs.gov/'>entra aquí</>"));
        SentriImportant.setMovementMethod(LinkMovementMethod.getInstance());


        mSignaturePad = binding.signaturePad;
        Button clearSignatureBtn = binding.clearButton;


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
            String numSentri = noSentriInput.getText().toString();
            String numSentriExp = expirationDateSentriInput.getText().toString();
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
            String veh_origen = OtherPlates.getText().toString();
            String conv_saldo = null;
            String conv_anualidad = null;


            counter = 5000;
            handler = new Handler();
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
            if (fac_razon_social.equals("")) {
                popup_Head.setText("Facturación: Razon Social");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (fac_rfc.equals("")) {
                popup_Head.setText("Facturación: RFC");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (fac_dom_fiscal.equals("")) {
                popup_Head.setText("Facturación: Domicilio");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (fac_email.equals("")) {
                popup_Head.setText("Facturación: Correo Electronico");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (fac_telefono.equals("")) {
                popup_Head.setText("Facturación: Número Cel.");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
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

            if (AnualidadOSaldo.getText().toString().contains("2000")) {
                conv_saldo = "2000";
                conv_anualidad = "0";
            }
            if (AnualidadOSaldo.getText().toString().contains("4000")) {
                conv_saldo = "4000";
                conv_anualidad = "0";
            }
            if (AnualidadOSaldo.getText().toString().contains("10000")) {
                conv_saldo = "0";
                conv_anualidad = "10000";
            }
            if (AnualidadOSaldo.getText().toString().contains("10001")) {
                conv_saldo = "0";
                conv_anualidad = "10001";
            }
            if (AnualidadOSaldo.getText().toString().contains("10002")) {
                conv_saldo = "0";
                conv_anualidad = "10002";
            }

            if (AnualidadOSaldo.getText().equals("")) {
                popup_Head.setText("Tipo de convenio");
                popup_Body.setText("Asigne un tipo de convenio, Anualidad o por Saldo");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
            }

            if (AnualidadOSaldo.getText().toString().contains("2000")) {
                conv_saldo = "2000";
                conv_anualidad = "0";
            }
            if (AnualidadOSaldo.getText().toString().contains("4000")) {
                conv_saldo = "4000";
                conv_anualidad = "0";
            }
            if (AnualidadOSaldo.getText().toString().contains("10000")) {
                conv_saldo = "0";
                conv_anualidad = "10000";
            }
            if (AnualidadOSaldo.getText().toString().contains("10001")) {
                conv_saldo = "0";
                conv_anualidad = "10001";
            }
            if (AnualidadOSaldo.getText().toString().contains("10002")) {
                conv_saldo = "0";
                conv_anualidad = "10002";
            }

            if (AnualidadOSaldo.getText().equals("")) {
                popup_Head.setText("Tipo de convenio");
                popup_Body.setText("Asigne un tipo de convenio, Anualidad o por Saldo");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
            }

            if (Firma[0] == 0) {
                popup_Head.setText("Firma no válida");
                popup_Body.setText("Debe firmar el documento.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }

            if (AnualidadOSaldo.getText().toString().contains("2000")) {
                conv_saldo = "2000";
                conv_anualidad = "0";
            }
            if (AnualidadOSaldo.getText().toString().contains("4000")) {
                conv_saldo = "4000";
                conv_anualidad = "0";
            }
            if (AnualidadOSaldo.getText().toString().contains("10000")) {
                conv_saldo = "0";
                conv_anualidad = "10000";
            }
            if (AnualidadOSaldo.getText().toString().contains("10001")) {
                conv_saldo = "0";
                conv_anualidad = "10001";
            }
            if (AnualidadOSaldo.getText().toString().contains("10002")) {
                conv_saldo = "0";
                conv_anualidad = "10002";
            }
            Bitmap sign = mSignaturePad.getSignatureBitmap();
            bitmapToFile(requireContext(), sign, "Solicitud_Puente_Firma.png");
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            SendReqInscription(numSentri, numSentriExp, dom_calle, dom_numero_ext, dom_colonia, dom_ciudad, dom_estado, dom_cp, fac_razon_social, fac_rfc, fac_dom_fiscal, fac_email, fac_telefono, veh_marca, veh_modelo, veh_color, veh_anio, veh_placas, veh_origen, conv_saldo, conv_anualidad, Token);

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
                android.Manifest.permission.CAMERA
        };
        if (!hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_ALL);
        }


        uploadSentriPhotoBtn.setOnClickListener(v -> {
            currentImageView = SentriPhotoImageView;
            takePicture("Photo_Sentri", "Inscripcion_Puente_PhotoSentri");
            SentriPhotoImageView.setVisibility(View.VISIBLE);

        });
        uploadOfficialIDPhotoBtn.setOnClickListener(v -> {
            currentImageView = IdOficialPhotoImageView;
            takePicture("Photo_idOficial", "Inscripcion_Puente_PhotoidOficial");
            IdOficialPhotoImageView.setVisibility(View.VISIBLE);
        });
        uploadOfficialReverseIDPhotoBtn.setOnClickListener(v -> {
            currentImageView = IdOficialReversePhotoImageView;
            takePicture("Photo_idOficialReverse", "Inscripcion_Puente_PhotoidOficialReverse");
            IdOficialReversePhotoImageView.setVisibility(View.VISIBLE);
        });

        return root;
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

    public File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) {
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
                String url_process = "https://apis.fpfch.gob.mx/api/v1/procs/p02";

                URL url = new URL(url_process);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + UserToken);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("sentri", noSentri);
                jsonParam.put("sentri_vencimiento", expirationDateSentri);
                jsonParam.put("dom_calle", domCalle);
                jsonParam.put("dom_numero_ext", domNumeroExt);
                jsonParam.put("dom_colonia", domColonia);
                jsonParam.put("dom_ciudad", domCiudad);
                jsonParam.put("dom_estado", domEstado);
                jsonParam.put("dom_cp", domCP);
                jsonParam.put("fac_razon_social", facRazonSocial);
                jsonParam.put("fac_rfc", facRFC);
                jsonParam.put("fac_dom_fiscal", facDomFiscal);
                jsonParam.put("fac_email", facEmail);
                jsonParam.put("fac_telefono", facTelefono);
                jsonParam.put("veh_marca", vehMarca);
                jsonParam.put("veh_modelo", vehModelo);
                jsonParam.put("veh_color", vehColor);
                jsonParam.put("veh_anio", vehAnio);
                jsonParam.put("veh_placas", vehPlacas);
                jsonParam.put("veh_origen", vehOrigen);
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
                        popup_Head.setText("Error interno");
                        popup_Body.setText("Internar nuevamente.");
                        UpdateData.InsertErrores(finalMsgErr);
                        popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                        handler.postDelayed(() -> popup_Window.dismiss(), counter);
                        popupWindow.dismiss();
                    } );
                    return;
                }
                UpdateData.InsertErrores(msgErr);
                String messageRes = (String) Result.get("message");
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
                int Tramite_ID = (int) Result.get("ID");

                if (messageRes.contains("registrado")) {

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
                                int newi = i+1;

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
                                if (newi == getFullNameTramites.size()) {
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(requireContext(), "Trámite registrado correctamente", Toast.LENGTH_SHORT).show();
                                        //handler.postDelayed(()->popupWindow.dismiss(), 10000);
                                    });
                                }
                                sendFile(String.valueOf(Tramite_ID), getFullNameTramites.get(i), String.valueOf(FileTypeCount));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Procesando trámite", Toast.LENGTH_SHORT).show();
                        Toast.makeText(requireContext(), "No salga de esta pantalla por favor", Toast.LENGTH_SHORT).show();

                        SentriPhotoImageView.setVisibility(View.GONE);
                        IdOficialPhotoImageView.setVisibility(View.GONE);

                        IdOficialReversePhotoImageView.setVisibility(View.GONE);

                        SentriPhotoImageView.setImageResource(0);
                        IdOficialPhotoImageView.setImageResource(0);
                        IdOficialReversePhotoImageView.setImageResource(0);
                        noSentriInput.getText().clear();
                        expirationDateSentriInput.getText().clear();
                        nameInput.getText().clear();
                        middleNameInput.getText().clear();
                        lastNameInput.getText().clear();
                        PersonalEmailInput.getText().clear();
                        PersonalCelInput.getText().clear();
                        streetInput.getText().clear();
                        noExtInput.getText().clear();
                        ColonyInput.getText().clear();
                        CityInput.getText().clear();
                        StateInput.getText().clear();
                        CPInput.getText().clear();
                        BusinessNameInput.getText().clear();
                        RFCInput.getText().clear();
                        ResidenceInput.getText().clear();
                        FactEmailInput.getText().clear();
                        FactCelInput.getText().clear();
                        BrandVehicleInput.getText().clear();
                        ModelVehicleInput.getText().clear();
                        ColorVehicleInput.getText().clear();
                        YearVehicleInput.getText().clear();
                        PlatesVehicleInput.getText().clear();
                        PlatesStateRG.clearCheck();
                        OtherPlates.getText().clear();
                        AnualidadOSaldo.getText().clear();
                        mSignaturePad.clearView();
                    });
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Trámite registrado correctamente", Toast.LENGTH_SHORT).show();
                        });
                }
                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
    private void updateLabel(){
        String myFormat="yyyy/MM/dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        expirationDateSentriInput.setText(dateFormat.format(myCalendar.getTime()));
    }
    public void sendFile(String Tramite_ID, String FileUpdate, String FileType) throws Exception {
        new Thread(() -> {
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
                        .addFormDataPart("id_proc_type", "2")
                        .build();

                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + Token)
                        .url("https://apis.fpfch.gob.mx/api/v1/files")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                System.out.println(response.body().string());
            }catch (Exception e){
                Log.e("SendFiles", "sendFile: ", e);
            }
        }).start();
    }

    //Take photo
    static final int REQUEST_TAKE_PHOTO = 1;
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
            if (data.getData() != null) {
                //Manejamos datos de la galeria
                String selectedImage = String.valueOf(data.getData());
                System.out.println("Lo de la imagen es: " + selectedImage.toString());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), Uri.parse(selectedImage));
                    currentImageView.setImageBitmap(bitmap);

                    InputStream inputStream = requireActivity().getContentResolver().openInputStream(Uri.parse(selectedImage));

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
            new AlertDialog.Builder(requireContext())
                    .setTitle("Permisos de almacenamiento")
                    .setMessage("Al dar 'Aceptar' se abrirá un menú, en el cual deberá seleccionar la aplicación y dar los permisos \n 'Permitir administrar todos los archivos'' ")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick( DialogInterface dialog, int which ) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse(String.format("package:%s", requireContext().getPackageName())));
                                activityResultLauncher.launch(intent);
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                activityResultLauncher.launch(intent);
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), permissions, 30);
        }
    }

    //Permissions



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}