package mx.gob.puentesfronterizos.lineaexpres.ui.addtelepeaje;


import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentAddTelepeajeBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;

public class AddTelepeajeFragment extends Fragment implements View.OnClickListener {
    EditText tagValue;
    String BarCode = "";

    UserLog userLog;
    ArrayList<String> userData;
    String User;
    String Token;
    EditText TagVehicleInput;

    ScrollView tagChecker;
    ScrollView tagUploader;

    String TAGC = "";

    private FragmentAddTelepeajeBinding binding;
    private String TAG = "TelepeajeADD";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddTelepeajeViewModel AddTelepeajeFragment =
                new ViewModelProvider(this).get(AddTelepeajeViewModel.class);

        binding = FragmentAddTelepeajeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userLog = new UserLog(requireContext()); //Open Users db
        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);

        Button scanTAG = binding.scanTAG;
        Button validarTag = binding.validarTag;
        tagValue = binding.tagValue;

        scanTAG.setOnClickListener(this);



        //Telepeaje upload
        EditText YearVehicleInput = binding.YearVehicleInput;
        EditText BrandVehicleInput = binding.BrandVehicleInput;
        EditText ModelVehicleInput = binding.ModelVehicleInput;
        EditText ColorVehicleInput = binding.ColorVehicleInput;
        EditText PlatesVehicleInput = binding.PlatesVehicleInput;
        EditText VIMNumberInput = binding.VIMNumberInput;
        TagVehicleInput = binding.TagVehicleInput;
       // EditText BridgeVehicleInput = binding.BridgeVehicleInput;
        Button uploadTeleVehicle = binding.uploadTeleVehicle;

        tagChecker = binding.tagChecker;
        tagUploader = binding.tagUploader;




        validarTag.setOnClickListener(view -> {

            TAGC = tagValue.getText().toString();
           //if (TAGC.length() > 0 && !Character.isLetter(TAGC.charAt(0))) {
           //    TAGC = TAGC.substring(0, TAGC.length() - 1);
           //}

            System.out.println("Se supone que este es: " + TAGC);

            verifyTag(TAGC);
        });

        uploadTeleVehicle.setOnClickListener(view -> {


           uploadVeh(BrandVehicleInput.getText().toString(), ModelVehicleInput.getText().toString(), YearVehicleInput.getText().toString(), ColorVehicleInput.getText().toString(), PlatesVehicleInput.getText().toString(), TAGC, "N/A");
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        tagValue.setText(BarCode);
        TagVehicleInput.setText(BarCode);

    }

    public void uploadVeh(String Marca, String Modelo, String Anio, String Color, String Placas, String tagValue, String Puente) {
        new Thread(() -> {
            try {

                InputStream inputStream;
                String url_process = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/vehicles";

                URL url = new URL(url_process);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("marca", Marca);
                jsonParam.put("linea", Modelo);
                jsonParam.put("modelo", Anio);
                jsonParam.put("color", Color);
                jsonParam.put("placa", Placas);
                jsonParam.put("tag", tagValue);
                jsonParam.put("puente", Puente);
                jsonParam.put("tt", 0);

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

                System.out.println("Este es el response Data de inscrpition  " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);
                System.out.println("Este es el response Data de sentri add  " + Result);

                if (Result.has("message")) {
                    String Message = Result.getString("message");
                    if (Message.contains("El tag ya está asignado a otro vehículo.")) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), Message, Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                if (Result.has("id")) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "El vehiculo ha sido agregado correctamente", Toast.LENGTH_SHORT).show();
                            MainActivity.nav_req(R.id.navigation_vehiculos_perfil);
                        });

                }



                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void verifyTag(String Tag) {

        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/tags/exists/" + Tag;

                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "text");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("GET");

                int Status = conn.getResponseCode();

                Log.i(TAG, "httpPostRequest: status = " + conn.getResponseCode());
                Log.i(TAG, "httpPostRequest: msg = " + conn.getResponseMessage());

                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                }else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);
                System.out.println("Este es el responseData de Telepeaje " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);
                String tpMsg = "";
                if (Result.has("tp")) {
                    Object tp = Result.get("tp");
                    if (tp instanceof JSONObject) {
                        JSONObject tpObj = (JSONObject) tp;
                        if (tpObj.has("noTag")) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Tag encontrado, continua agregando tus datos", Toast.LENGTH_SHORT).show();
                                TagVehicleInput.setText(Tag);
                                tagChecker.setVisibility(View.GONE);
                                tagUploader.setVisibility(View.VISIBLE);
                            });
                            return;
                        }
                    } else if (tp instanceof String) {
                        if (tp.toString().contains("Not Found")) {
                            tpMsg = "No se ha encontrado información en nuestra base de datos, intentalo más tarde.";
                            String finalTpMsg = tpMsg;
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), finalTpMsg, Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                    }


                }


                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    public void onClick(View view) {
        // Crear un objeto IntentIntegrator para iniciar el escaneo del código de barras
        IntentIntegrator integrator = new IntentIntegrator(requireActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_128); // Configurar el formato del código de barras
        integrator.setPrompt("Escanea el código de barras"); // Establecer un mensaje para el usuario
        integrator.setCameraId(0); // Usar la cámara trasera por defecto
        integrator.setBeepEnabled(false); // Desactivar el sonido de la cámara
        integrator.setBarcodeImageEnabled(false); // Mostrar una imagen del código de barras escaneado
        integrator.forSupportFragment(AddTelepeajeFragment.this).initiateScan();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Obtener el resultado del escaneo del código de barras
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                // Obtener el contenido del código de barras escaneado
                String barcode = result.getContents();
                if (barcode.startsWith("01") && barcode.length() == 14) {
                    // El código de barras contiene un GTIN de 14 dígitos (EAN-14)
                    String gtin = barcode.substring(2, 14);
                    BarCode = gtin;
                    Toast.makeText(getContext(), "Código de barras escaneado: " + gtin, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "El código de barras escaneado no es un código de barras 128 válido", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}