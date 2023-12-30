package mx.gob.puentesfronterizos.lineaexpres.ui.addlineaexpres;


import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.VehiculoSliderAdapter;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentAddLineaExpresBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class AddLineaExpresFragment extends Fragment {
    UserLog userLog;
    ArrayList<String> userData;
    String User;
    String Token;

    LayoutInflater loaderInflater;
    View popupView;
    View view;
    int width;
    int height;
    updateData UpdateData;

    PopupWindow popupWindow;
    private FragmentAddLineaExpresBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddLineaExpresViewModel AddLineaExpresAddFragment =
                new ViewModelProvider(this).get(AddLineaExpresViewModel.class);

        binding = FragmentAddLineaExpresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userLog = new UserLog(requireContext()); //Open Users db
        UpdateData = new updateData(requireContext()); //Open Users db


        TextView Sentri = binding.Sentri;
        Button btnAgregarSentri = binding.AgregarSentri;





        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);

        btnAgregarSentri.setOnClickListener(view -> {
            checkSentri(Sentri.getText().toString());
        });



        loaderInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        popupView = loaderInflater.inflate(R.layout.loader, null);
        view = binding.welcomeMsg.getRootView();
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(popupView, width, height, false);
        popupWindow.setElevation(20);
        ImageView LoaderGif = popupView.findViewById(R.id.loader_gif);
        Glide.with(requireContext())
                .load(R.drawable.linea_expres_loader)
                .into(LoaderGif);




        return root;
    }


    public void checkSentri(String sentri) {
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = "https://apis.fpfch.gob.mx/api/v1/le/user/"+sentri+"/"+User+"";

                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "text");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("GET");

                int Status = conn.getResponseCode();
                Log.i("TAG", "httpPostRequest: status = " + conn.getResponseCode());
                Log.i("TAG", "httpPostRequest: msg = " + conn.getResponseMessage());

                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                }else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);

                JsonElement element = JsonParser.parseString(ResponseData);
                if (element.isJsonObject()) {
                    try {
                        JSONObject Res = new JSONObject(ResponseData);
                        String msgString = (String) Res.get("message");
                        System.out.println("entró al object");
                        System.out.println(Res);
                        requireActivity().runOnUiThread(() -> {
                            popupWindow.dismiss();
                            Toast.makeText(requireContext(), msgString, Toast.LENGTH_SHORT).show();
                        });

                    }catch (Exception e) {
                        throw e;
                    }
                }else if (element.isJsonArray()) {
                    JSONArray Result = new JSONArray(ResponseData);
                    System.out.println("entró al array");
                    uploadVeh(sentri);

                }else {
                    requireActivity().runOnUiThread(() -> {
                        System.out.println("error?");
                        popupWindow.dismiss();
                        Toast.makeText(requireContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                    });
                }



                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }



    public void uploadVeh(String sentri) {
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        new Thread(() -> {
            try {

                InputStream inputStream;
                String url_process = "https://apis.fpfch.gob.mx/api/v1/user/sentri";

                URL url = new URL(url_process);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("sentri", sentri);

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

                System.out.println(Status);

                System.out.println("Este es el response Data  " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);
                String message = Result.optString("message", "undefined");

                if (message.contains("Cambio realizado exitosamente.")) {
                    requireActivity().runOnUiThread(() -> {
                        popupWindow.dismiss();
                        userLog.updateSentri(sentri);
                        MainActivity.nav_req(R.id.navigation_vehiculos_perfil);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        Toast.makeText(requireContext(), "Revisa tus vehículos", Toast.LENGTH_SHORT).show();
                    });
                }



                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();

                requireActivity().runOnUiThread(() -> {
                    popupWindow.dismiss();
                    Toast.makeText(requireContext(), "Revisa tus vehículos", Toast.LENGTH_SHORT).show();
                });
            }

        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}