package mx.gob.puentesfronterizos.lineaexpres.ui.unsubscribe;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentReqBajaVehiculoOTagLineaExpresBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UnsubscribeFragment extends Fragment {

    UserLog userLog;
    updateData UpdateData;

    EditText noSentriInput;
    EditText noSentriExpInput;
    EditText TAGName;

    EditText CarBrand;
    EditText CarColor;
    EditText CarPlates;
    EditText CarModel;
    EditText CarYear;
    RadioGroup PlatesStateRG;
    RadioGroup BridgesRG;
    EditText OtherPlates;
    EditText AdscrInput;
    EditText UnsubscribeMotiveInput;

    SignaturePad mSignaturePad;

    String Token;

    Button sendFormBtn;

    LayoutInflater popupInflater;
    View popup_View;
    View popup_view;
    int popup_width;
    int popup_height;
    PopupWindow popup_Window;
    TextView popup_Head;
    TextView popup_Body;

    ImageView SentriPhotoImageView;
    ImageView IdOficialPhotoImageView;
    ImageView IdOficialReversePhotoImageView;


    //Layout Photo Declarations
    Button uploadSentriPhotoBtn;
    Button uploadOfficialIDPhotoBtn;
    Button uploadOfficialReverseIDPhotoBtn;
    Handler handler;
    int counter;

    String PhotoUriTemp;
    String currentPhotoPath;
    ImageView currentImageView;

    LayoutInflater loaderInflater;
    View popupView;
    View view;
    int width;
    int height;
    PopupWindow popupWindow;
    final Calendar myCalendar= Calendar.getInstance();
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private FragmentReqBajaVehiculoOTagLineaExpresBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UnsubscribeViewModel FormalitiesViewModel = new ViewModelProvider(this).get(UnsubscribeViewModel.class);
        binding = FragmentReqBajaVehiculoOTagLineaExpresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        userLog = new UserLog(requireContext()); //Open Users db
        UpdateData = new updateData(requireContext()); //Open Users db
        SQLOnInit sqlOnInit = new SQLOnInit(requireContext()); //Open Users db
        sqlOnInit.cleanTramites();
        sqlOnInit.InsertTramitesOnInit();

        noSentriInput = binding.noSentriInput;
        noSentriExpInput = binding.expirationDateSentriInput;
        TAGName = binding.OldTagTitleInput;
        CarBrand = binding.BrandOldVehicleInput;
        CarColor = binding.ColorOldVehicleInput;
        CarPlates = binding.PlatesOldVehicleInput;
        CarModel = binding.ModelOldVehicleInput;
        CarYear = binding.YearOldVehicleInput;
        PlatesStateRG = binding.OldPlatesStateRG;
        OtherPlates = binding.OldOtherPlates;
        BridgesRG = binding.BridgesRG;
        AdscrInput = binding.AdscrInput;
        UnsubscribeMotiveInput = binding.UnsubscribeMotiveInput;

        uploadSentriPhotoBtn = binding.uploadSentriPhotoBtn;
        uploadOfficialIDPhotoBtn = binding.uploadOfficialIDPhotoBtn;
        uploadOfficialReverseIDPhotoBtn = binding.uploadOfficialReverseIDPhotoBtn;

        SentriPhotoImageView = binding.SentriPhotoImageView;
        IdOficialPhotoImageView = binding.IdOficialPhotoImageView;
        IdOficialReversePhotoImageView = binding.IdOficialReversePhotoImageView;

        sendFormBtn = binding.sendFormBtn;

        mSignaturePad = binding.signaturePad;
        Button clearSignatureBtn = binding.clearButton;


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
        clearSignatureBtn.setOnClickListener(v -> {
            mSignaturePad.clearView();
        });

        BridgesRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = BridgesRG.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.ZaragozaRadioButton:
                        AdscrInput.getText().clear();
                        AdscrInput.setText("Zaragoza");

                        break;
                    case R.id.LerdoRadioButton:
                        AdscrInput.getText().clear();
                        AdscrInput.setText("Lerdo");
                        break;
                }
            }
        });

        PlatesStateRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = PlatesStateRG.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.OldChihRadioButton:
                        OtherPlates.getText().clear();
                        OtherPlates.setText("Chihuahua");
                        OtherPlates.setVisibility(View.GONE);
                        break;
                    case R.id.OldTxRadioButton:
                        OtherPlates.getText().clear();
                        OtherPlates.setText("Texas");
                        OtherPlates.setVisibility(View.GONE);
                        break;
                    case R.id.OldOtherRadioButton:
                        OtherPlates.getText().clear();
                        OtherPlates.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };

        noSentriExpInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(requireActivity(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
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

        sendFormBtn.setOnClickListener(v -> {
            counter = 0;

            counter = 5000;
            handler = new Handler();
            String Sentri = noSentriInput.getText().toString();
            String SentriExp = noSentriExpInput.getText().toString();
            String num_tag = TAGName.getText().toString();
            String veh_brand = CarBrand.getText().toString();
            String veh_model = CarModel.getText().toString();
            String veh_color = CarColor.getText().toString();
            String veh_year = CarYear.getText().toString();
            String veh_plates = CarPlates.getText().toString();
            String veh_orig = OtherPlates.getText().toString();
            String motive_unsub = UnsubscribeMotiveInput.getText().toString();
            String bridgeSelected = AdscrInput.getText().toString();
            String Lerdo = "";
            String Zaragoza = "";
            if (bridgeSelected.contains("Lerdo")) {
                Lerdo = "1";
                Zaragoza = "0";
            }
            if (bridgeSelected.contains("Zaragoza")) {
                Lerdo = "0";
                Zaragoza = "1";
            }
            if (bridgeSelected.isEmpty()) {
                Toast.makeText(requireContext(), "Seleccione un puente, porfavor.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Sentri.equals("")) {
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
            if (SentriExp.equals("")) {
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


            if (veh_brand.equals("")) {
                popup_Head.setText("Datos Vehiculo: Marca");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (veh_model.equals("")) {
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
            if (veh_year.equals("")) {
                popup_Head.setText("Datos Vehiculo: Año");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (veh_plates.equals("")) {
                popup_Head.setText("Datos Vehiculo: Placas");
                popup_Body.setText("Rellene este campo, por favor.");
                popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                handler.postDelayed(() -> popup_Window.dismiss(), counter);
                return;
            }
            if (veh_orig.equals("")) {
                popup_Head.setText("Datos Vehiculo: Estado");
                popup_Body.setText("Rellene este campo, por favor.");
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
            bitmapToFile(requireContext(), sign, "Solicitud_Baja_Vehiculo_Firma.png");
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            SendReqInscription(Sentri, SentriExp, num_tag, veh_brand, veh_model, veh_color, veh_year, veh_plates, veh_orig, motive_unsub, Lerdo, Zaragoza, Token);


        });

        return root;
    }
    private void updateLabel(){
        String myFormat="yyyy/MM/dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        noSentriExpInput.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void SendReqInscription(String Sentri, String SentriExp, String num_tag, String veh_brand, String veh_model, String veh_color, String veh_year, String veh_plates, String veh_orig, String motive_unsub, String Lerdo, String Zaragoza, String Token){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String url_process = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/procs/p05";

                URL url = new URL(url_process);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("sentri", Sentri);
                jsonParam.put("sentri_vencimiento", SentriExp);
                jsonParam.put("num_tag", num_tag);
                jsonParam.put("veh_marca", veh_brand);
                jsonParam.put("veh_modelo", veh_model);
                jsonParam.put("veh_color", veh_color);
                jsonParam.put("veh_anio", veh_year);
                jsonParam.put("veh_placas", veh_plates);
                jsonParam.put("veh_origen", veh_orig);
                jsonParam.put("motivo", motive_unsub);
                jsonParam.put("veh_adscrip_zaragoza", Lerdo);
                jsonParam.put("veh_adscrip_lerdo", Zaragoza);

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
                System.out.println("Este es el response Data de change vehicle  " + ResponseData);


                JSONObject Result = new JSONObject(ResponseData);
                if (Status == 500) {
                    String msgErr = (String) Result.get("text");
                    requireActivity().runOnUiThread(() -> {
                        popup_Head.setText("Error interno");
                        popup_Body.setText("Internar nuevamente.");
                        popup_Window.showAtLocation(popup_view, Gravity.TOP|Gravity.END, 0, 0);
                        handler.postDelayed(() -> popup_Window.dismiss(), counter);
                        popupWindow.dismiss();
                    } );
                    return;
                }

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
                                int newi = i+1;
                                String FileTypeCount = Integer.toString(newi++);

                                if (newi == getFullNameTramites.size()) {
                                    requireActivity().runOnUiThread(() -> {

                                        Toast.makeText(requireContext(), "Trámite registrado correctamente", Toast.LENGTH_SHORT).show();

                                        handler.postDelayed(() ->popupWindow.dismiss(), 10000);
                                    });
                                }

                                if (getFullNameTramites.get(i).isEmpty()) {
                                    return;
                                }

                                sendFile(String.valueOf(Tramite_ID), getFullNameTramites.get(i), FileTypeCount);

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

                        SentriPhotoImageView.setImageResource(0);
                        IdOficialPhotoImageView.setImageResource(0);
                        IdOficialReversePhotoImageView.setImageResource(0);

                        noSentriInput.getText().clear();
                        noSentriExpInput.getText().clear();
                        TAGName.getText().clear();
                        CarBrand.getText().clear();
                        CarColor.getText().clear();
                        CarPlates.getText().clear();
                        CarModel.getText().clear();
                        CarYear.getText().clear();
                        PlatesStateRG.clearCheck();
                        OtherPlates.getText().clear();
                        mSignaturePad.clearView();

                        Toast.makeText(requireContext(), "Ya puede salir de esta pantalla.", Toast.LENGTH_SHORT).show();
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

    public void sendFile(String Tramite_ID, String FileUpdate, String FileType) throws Exception {
        new Thread(() -> {
            try {
                String imageLocation = FileUpdate.substring(FileUpdate.lastIndexOf('/') + 1);
                File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Data/mx.gob.puentesfronterizos.lineaexpres/files/Pictures/" + imageLocation);

                //Start to change width, but firstly changing to Bitmap

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
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
                        .addFormDataPart("id_proc_type", "5")
                        .build();

                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + Token)
                        .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
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

    static final int REQUEST_TAKE_PHOTO = 1;
    private void takePicture(String Tramite, String imageName) {
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (openCamera.resolveActivity(requireActivity().getPackageManager()) != null) {
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
                startActivityForResult(openCamera, REQUEST_TAKE_PHOTO);
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            String bitmapImage = PhotoUriTemp;
            Bitmap bitmap = null;
            try {
                bitmap = rotateImage(MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), Uri.parse(bitmapImage)), 90);
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentImageView.setImageBitmap(bitmap);
        }
        if (resultCode == RESULT_CANCELED) {
            currentImageView.setImageResource(0);
        }
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