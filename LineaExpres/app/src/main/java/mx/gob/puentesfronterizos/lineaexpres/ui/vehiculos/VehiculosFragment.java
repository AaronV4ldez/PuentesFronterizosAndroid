package mx.gob.puentesfronterizos.lineaexpres.ui.vehiculos;


import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.VehiculoSliderAdapter;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentVehiculosBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class VehiculosFragment extends Fragment {
    private static final String TAG = "Vehiculos Fragment";
    ViewPager mainPager;
    VehiculoSliderAdapter sliderAdapter;
    Timer timer;
    int main_currentPage = 0;
    long DELAY_MS = 500;
    long PERIOD_MS = 3000;

    ArrayList<String> userData;
    String User;
    String Token;
    UserLog userLog;
    updateData UpdateData;

    Button BtnSolicitudInscripcion;

    LayoutInflater loaderInflater;
    View popupView;
    View view;
    int width;
    int height;
    PopupWindow popupWindow;
    private FragmentVehiculosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        VehiculosViewModel callUsFragment =
                new ViewModelProvider(this).get(VehiculosViewModel.class);

        binding = FragmentVehiculosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userLog = new UserLog(requireActivity()); //Open local db connection
        UpdateData = new updateData(requireActivity()); //Open local db connection
        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
            root = null;
        }



        ScrollView ScrollContainer = binding.ScrollContainer;

        loaderInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        popupView = loaderInflater.inflate(R.layout.loader, null);
        view = binding.ScrollContainer.getRootView();
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(popupView, width, height, false);
        popupWindow.setElevation(20);
        ImageView LoaderGif = popupView.findViewById(R.id.loader_gif);
        Glide.with(requireContext())
                .load(R.drawable.linea_expres_loader)
                .into(LoaderGif);

        popupWindow.showAtLocation(popupView, Gravity.TOP|Gravity.END, 0, 0);

        UpdateData.cleanVehiculos();

        BtnSolicitudInscripcion = binding.BtnSolicitudInscripcion;

        BtnSolicitudInscripcion.setTag("Inscripción");
        BtnSolicitudInscripcion.setOnClickListener(view -> {
            UpdateData.updateCarSelected("Inscripcion");
            MainActivity.nav_req(R.id.navigation_req_inscription);
        });
        getVehiculos();



        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void getVehiculos(){

        if (Token == null || Token.isEmpty()) {
            requireActivity().runOnUiThread(() -> {
                popupWindow.dismiss();
            });
            return;
        }

        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/vehicles";

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
                System.out.println("Este es el responseData de Register Fragment " + ResponseData);

                JsonElement element = JsonParser.parseString(ResponseData);
                if (element.isJsonObject()) {
                    try {
                        JSONObject Res = new JSONObject(ResponseData);
                        String msgString = (String) Res.get("message");



                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), msgString, Toast.LENGTH_SHORT).show();
                                popupWindow.dismiss();
                            if (BtnSolicitudInscripcion.getTag().toString().equals("Inscripción")) {
                                BtnSolicitudInscripcion.setOnClickListener(view -> {
                                    UpdateData.updateCarSelected("Inscripcion");
                                    MainActivity.nav_req(R.id.navigation_req_inscription);
                                });
                            }else {
                                BtnSolicitudInscripcion.setOnClickListener(view -> {
                                    UpdateData.updateCarSelected("");
                                    MainActivity.nav_req(R.id.navigation_req_inscription);
                                });
                            }

                        });

                    }catch (Exception e) {
                        throw e;
                    }
                }else if (element.isJsonArray()) {
                    JSONArray Result = new JSONArray(ResponseData);
                    System.out.println("Este es el Result de Register Fragment " + Result);
                    System.out.println("Debe entrar a los array");

                    for (int i = 0; i < Result.length(); i++) {
                        JSONObject Tramites = (JSONObject) Result.get(i);



                        int tipoVeh = (int) Tramites.get("tipo");
                        String Marca = (String) Tramites.get("marca");
                        String Linea = (String) Tramites.get("linea");
                        String tag = (String) Tramites.get("tag");
                        String imgurl = (String) Tramites.get("imgurl");
                        String ctl_contract_type = Tramites.optString("ctl_contract_type", "undefined");
                        String clt_expiration_date = Tramites.optString("clt_expiration_date", "undefined");
                        String saldo = (String) Tramites.get("saldo");
                        String placa = (String) Tramites.get("placa");
                        String color = (String) Tramites.get("color");
                        String anio = (String) Tramites.get("modelo");
                        String ctl_stall_id = Tramites.optString("ctl_stall_id", "undefined");
                        String ctl_user_id = Tramites.optString("ctl_user_id", "undefined");
                        String ctl_id = Tramites.optString("ctl_id", "undefined");
                        System.out.println("Estos son los tag de Linea Expres: " + tag);
                        if (tipoVeh == 1) {
                            BtnSolicitudInscripcion.setText("Trámite para agregar un vehículo");
                            BtnSolicitudInscripcion.setTag("");
                            UpdateData.insertVehicles(new String(String.valueOf(tipoVeh)), Marca, Linea, tag, imgurl, new String(String.valueOf(ctl_contract_type)), clt_expiration_date, saldo, placa, color, anio, ctl_stall_id, ctl_user_id, ctl_id);
                        }

                    }

                    requireActivity().runOnUiThread(() -> {
                        try {
                            mainPager = binding.sliderViewPager;
                            sliderAdapter = new VehiculoSliderAdapter(getContext());
                            mainPager.setAdapter(sliderAdapter);

                            if (BtnSolicitudInscripcion.getTag().toString().equals("Inscripción")) {
                                BtnSolicitudInscripcion.setOnClickListener(view -> {
                                    UpdateData.updateCarSelected("Inscripcion");
                                    MainActivity.nav_req(R.id.navigation_req_inscription);
                                });
                            }else {
                                BtnSolicitudInscripcion.setOnClickListener(view -> {
                                    UpdateData.updateCarSelected("");
                                    MainActivity.nav_req(R.id.navigation_req_inscription);
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    });

                    requireActivity().runOnUiThread(() -> {
                        popupWindow.dismiss();
                    });
                }
                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    popupWindow.dismiss();
                });
            }

        }).start();
    }

}