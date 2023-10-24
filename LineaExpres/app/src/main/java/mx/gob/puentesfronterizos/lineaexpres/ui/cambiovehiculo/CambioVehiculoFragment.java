package mx.gob.puentesfronterizos.lineaexpres.ui.cambiovehiculo;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.google.zxing.pdf417.PDF417Common;

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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentCambioVehiculoBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CambioVehiculoFragment extends Fragment {
    //Database declaration
    UserLog userLog;
    updateData UpdateData;
    private static final int WRITE_REQUEST_CODE = 32;
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

    String localRazonSocial;
    String localRFCFiscal;
    String localDomFiscal;
    String localEmailFiscal;
    String localTelefonoFiscal;

    String Nacionalidad;

    String num_tag;
    String vehMarca;
    String vehModelo;
    String vehColor;
    String vehAnio;
    String vehPlacas;
    String Sentri;
    String FechaSentri;



    Uri photoOne = null;
    Uri photoTwo = null;
    Uri photoThree = null;
    Uri photoFour = null;
    Uri photoFive = null;
    Uri photoSix = null;
    Uri photoSeven = null;
    Uri photoEighth = null;

    File imageFile = null;

    Button uploadInsurancePolicyBtnSecond;
    ImageView PolizaSeguroPhotoImageViewSecond;

    String currentTramiteFoto = "";

    String numTramite = "3";


    private ActivityResultLauncher<Intent> activityResultLauncher;

    private FragmentCambioVehiculoBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mx.gob.puentesfronterizos.lineaexpres.ui.cambiovehiculo.CambioVehiculoViewModel cambioVehiculoFragment =
                new ViewModelProvider(this).get(mx.gob.puentesfronterizos.lineaexpres.ui.cambiovehiculo.CambioVehiculoViewModel.class);

        binding = FragmentCambioVehiculoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        popupInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        popup_View = popupInflater.inflate(R.layout.popup_top, null);
        popup_view = root;
        popup_width = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup_height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popup_Window = new PopupWindow(popup_View, popup_width, popup_height, false);
        popup_Head = popup_View.findViewById(R.id.popupHead);
        popup_Body = popup_View.findViewById(R.id.popupBody);


        loaderInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        popupView = loaderInflater.inflate(R.layout.loader, null);
        view = root;
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(popupView, width, height, false);
        popupWindow.setElevation(200);
        ImageView LoaderGif = popupView.findViewById(R.id.loader_gif);
        Glide.with(requireContext())
                .load(R.drawable.linea_expres_loader)
                .into(LoaderGif);

        userLog = new UserLog(requireContext()); //Open Users db
        UpdateData = new updateData(requireContext()); //Open Users db
        SQLOnInit sqlOnInit = new SQLOnInit(requireContext()); //Open Users db
        sqlOnInit.cleanTramites();
        sqlOnInit.InsertTramitesOnInit();

        Token = userLog.GetUserData().get(1);
        Sentri = userLog.GetUserData().get(4);
        FechaSentri = userLog.GetUserData().get(5);
        localRazonSocial = userLog.GetUserData().get(6);;
        localRFCFiscal = userLog.GetUserData().get(7);
        localDomFiscal = userLog.GetUserData().get(8);
        localEmailFiscal = userLog.GetUserData().get(10);
        localTelefonoFiscal = userLog.GetUserData().get(11);

        String CarSelected = UpdateData.getCarSelected();

        ArrayList<String> CarList = UpdateData.getVehicles();
        for (int i = 0; i < CarList.size(); i++) {
            String[] splitArray = CarList.get(i).split("∑");
            if (splitArray[8].equals(CarSelected)) {
                vehMarca = splitArray[1];
                vehModelo = splitArray[2];
                num_tag = splitArray[3];
                vehPlacas = splitArray[8];
                vehColor = splitArray[9];
                vehAnio = splitArray[10];
            }
        }

        System.out.println(vehMarca + vehModelo + num_tag + vehPlacas + vehColor + vehAnio + "Estos son los datos de usuario");


        EditText BrandNewVehicule = binding.BrandNewVehicleInput;
        EditText ModelNewVehicule = binding.ModelNewVehicleInput;
        EditText ColorNewVehicule = binding.ColorNewVehicleInput;
        EditText YearNewVehicule = binding.YearNewVehicleInput;
        EditText PlatesNewVehicule = binding.PlatesNewVehicleInput;

        Button BtnOriginNational = binding.btnNewVehiculeOriginNational;
        Button BtnOriginUSA = binding.btnNewVehiculeOriginUSA;

        //Circulation Card ---
        Button BtnUploadCirculationCard = binding.BtnUploadCirculationCard;
        ImageView IMGCirculationCard = binding.IMGCirculationCard;
        //Insurance Policy ---
        Button BtnUploadInsurancePolicy = binding.BtnUploadInsurancePolicy;
        ImageView IMGInsurancePolicy = binding.IMGInsurancePolicy;
        //USA Approval ---
        Button BtnUploadUSAApproval = binding.BtnUploadUSAApproval;
        ImageView IMGUSAApproval = binding.IMGUSAApproval;
        //Power Attornet ---
        Button BtnUploadPowerAttorney = binding.BtnUploadPowerAttorney;
        ImageView IMGPowerAttorney = binding.IMGPowerAttorney;

        Button btnSendForm = binding.sendFormBtn;


        uploadInsurancePolicyBtnSecond = binding.BtnUploadInsurancePolicySecond;
        PolizaSeguroPhotoImageViewSecond = binding.IMGInsurancePolicySecond;



        YearNewVehicule.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        //Btn Click Listener

        BtnUploadCirculationCard.setOnClickListener(v -> {
            currentImageView = IMGCirculationCard;
            takePicture("Photo_tarjetaCirculacion", "Solicitud_Inscripcion_PhototarjetaCirculacion");
            currentImageView.setVisibility(View.VISIBLE);
        });

        BtnUploadInsurancePolicy.setOnClickListener(v -> {
            currentImageView = IMGInsurancePolicy;
            takePicture("Photo_polizaSeguro", "Inscripcion_Puente_PhotoidOficial");
            currentImageView.setVisibility(View.VISIBLE);
        });

        BtnUploadUSAApproval.setOnClickListener(v -> {
            currentImageView = IMGUSAApproval;
            takePicture("Photo_AprovacionUSA", "CambioVeh_Aprobacion");
            currentImageView.setVisibility(View.VISIBLE);
        });

        BtnUploadPowerAttorney.setOnClickListener(v -> {
            currentImageView = IMGPowerAttorney;
            takePicture("Photo_cartaPoder", "CambioVeh_CartaPoder");
            currentImageView.setVisibility(View.VISIBLE);
        });

        uploadInsurancePolicyBtnSecond.setOnClickListener(v -> {
            currentImageView = PolizaSeguroPhotoImageViewSecond;
            takePicture("Photo_polizaSeguro2", "Photo_polizaSeguro2");
            currentImageView.setVisibility(View.VISIBLE);
        });

        BtnOriginNational.setOnClickListener(v -> {
            Nacionalidad = "Nacional";
            BtnOriginUSA.setBackgroundResource(R.drawable.buttons2);

            //Set a style as backgroundResource from styles.xml to the button

            BtnOriginNational.setBackgroundResource(R.drawable.buttons);
        });



        BtnOriginUSA.setOnClickListener(v -> {
           Nacionalidad = "USA";
            BtnOriginNational.setBackgroundResource(R.drawable.buttons2);
            BtnOriginUSA.setBackgroundResource(R.drawable.buttons);
        });

        SignaturePad mSignaturePad = binding.signaturePad;
        Button clearSignatureBtn = binding.clearButton;

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



        btnSendForm.setOnClickListener(v -> {

            counter = 0;
            handler = new Handler();
            Bitmap sign = mSignaturePad.getSignatureBitmap();
            bitmapToFile(requireContext(), sign, "Solicitud_Inscripcion_Firma.png");
            popupWindow.showAtLocation(popupView, Gravity.TOP|Gravity.END, 0, 0);
            sendRequest(BrandNewVehicule.getText().toString(), ModelNewVehicule.getText().toString(), ColorNewVehicule.getText().toString(), YearNewVehicule.getText().toString(), PlatesNewVehicule.getText().toString());
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

    public void sendRequest(String veh2Brand, String veh2Model, String veh2Color, String veh2Year, String veh2Plates){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String url_process = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/procs/p03";

                URL url = new URL(url_process);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("sp", 1);
                jsonParam.put("fac_razon_social", localRazonSocial);
                jsonParam.put("fac_rfc", localRFCFiscal);
                jsonParam.put("fac_dom_fiscal", localDomFiscal);
                jsonParam.put("fac_email", localEmailFiscal);
                jsonParam.put("fac_telefono", localTelefonoFiscal);
                jsonParam.put("num_tag", num_tag);
                jsonParam.put("veh1_marca", vehMarca);
                jsonParam.put("veh1_modelo", vehModelo);
                jsonParam.put("veh1_color", vehColor);
                jsonParam.put("veh1_anio", vehAnio);
                jsonParam.put("veh1_placas", vehPlacas);
                jsonParam.put("veh1_origen", "N/A");
                jsonParam.put("veh2_marca", veh2Brand);
                jsonParam.put("veh2_modelo", veh2Model);
                jsonParam.put("veh2_color", veh2Color);
                jsonParam.put("veh2_anio", veh2Year);
                jsonParam.put("veh2_placas", veh2Plates);
                jsonParam.put("veh2_origen", Nacionalidad);
                jsonParam.put("sentri",Sentri);
                jsonParam.put("sentri_vencimiento",FechaSentri);


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

                                if (newi == getFullNameTramites.size()) {
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(requireContext(), "Procesando trámite", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(requireContext(), "No salga de esta pantalla por favor", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(requireContext(), "Trámite registrado correctamente", Toast.LENGTH_SHORT).show();

                                        handler.postDelayed(afterProcc, 10000);

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
                        Toast.makeText(requireContext(), "Trámite registrado correctamente", Toast.LENGTH_SHORT).show();

                        handler.postDelayed(afterProcc, 10000);

                    });
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
                if (photoTwo != null && Integer.parseInt(FileType) == 2) {uri = photoTwo; photoTwo = null;}
                if (photoThree != null && Integer.parseInt(FileType) == 3) {uri = photoThree; photoThree = null;}
                if (photoFour != null && Integer.parseInt(FileType) == 4) {uri = photoFour; photoFour = null;}
                if (photoFive != null && Integer.parseInt(FileType) == 5) {uri = photoFive; photoFive = null;}
                if (photoSix != null && Integer.parseInt(FileType) == 6) {uri = photoSix; photoSix = null;}
                if (photoSeven != null && Integer.parseInt(FileType) == 7) {uri = photoSeven; photoSeven = null;}
                if (photoEighth != null && Integer.parseInt(FileType) == 8) {uri = photoEighth; photoEighth = null;}
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
                                        .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
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
                                            .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
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
                                .url("https://lineaexpressapp.desarrollosenlanube.net/api/v1/files")
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


    //Take photo
    static final int REQUEST_TAKE_PHOTO = 1;

    private void takePicture(String Tramite, String imageName) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png", "image/*"};

        Intent pickPdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickPdfIntent.setType("application/pdf");
        pickPdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, "application/pdf");
        pickPhoto.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            imageFile = createImageFile(imageName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (imageFile != null) {
            Uri photoURI = FileProvider.getUriForFile(requireContext(), "mx.gob.puentesfronterizos.lineaexpres", imageFile);
            openCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            currentTramiteFoto = Tramite;
            UpdateData.InsertTramites(Tramite, photoURI.toString());
            PhotoUriTemp = UpdateData.getTramites(Tramite);
            System.out.println("ImageFile" + imageFile);
            System.out.println("photouri" + photoURI);
            System.out.println("getExtras" + openCamera.getExtras());

            Intent chooserIntent = Intent.createChooser(new Intent(), "Selecciona una opción");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{openCamera, pickPhoto});

            startActivityForResult(chooserIntent, REQUEST_TAKE_PHOTO);
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
                if (currentTramiteFoto.contains("Photo_idOficial")) {photoTwo = selectedImage;}
                if (currentTramiteFoto.contains("Photo_idOficialReverse")) {photoThree = selectedImage;}
                if (currentTramiteFoto.contains("Photo_tarjetaCirculacion")) {photoFour = selectedImage;}
                if (currentTramiteFoto.contains("Photo_polizaSeguro")) {photoFive = selectedImage;}
                if (currentTramiteFoto.contains("Photo_polizaSeguro2")) {photoSix = selectedImage;}
                if (currentTramiteFoto.contains("Photo_AprovacionUSA")) {photoSeven = selectedImage;}
                if (currentTramiteFoto.contains("Photo_cartaPoder")) {photoEighth = selectedImage;}

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

    //Permissions



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}