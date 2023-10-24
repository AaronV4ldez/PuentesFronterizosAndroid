package mx.gob.puentesfronterizos.lineaexpres.ui.miscruces;


import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.VehiculoSliderAdapter;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentMisCrucesBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;


public class MisCrucesFragment extends Fragment {
    updateData UpdateData;
    UserLog userLog;

    LinearLayout linearLayout;

    String num_tag;
    String vehMarca;
    String vehModelo;
    String vehColor;
    String vehAnio;
    String vehPlacas;
    String Sentri;
    String FechaSentri;
    String ctl_contract_type;
    String ctl_stall_id;
    String ctl_user_id;
    String ctl_id;

    ArrayList<String> userData;
    String User;
    String Token;

    LayoutInflater loaderInflater;
    View popupView;
    View view;
    int width;
    int height;
    PopupWindow popupWindow;

    private FragmentMisCrucesBinding binding;
    private String TAG = "MisCruces";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MisCrucesViewModel callUsFragment =
                new ViewModelProvider(this).get(MisCrucesViewModel.class);

        binding = FragmentMisCrucesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        UpdateData = new updateData(requireContext());
        userLog = new UserLog(requireActivity()); //Open local db connection
        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);

        linearLayout = binding.LinearLayoutContainer;

        String CarSelected = UpdateData.getCarSelected();

        ArrayList<String> CarList = UpdateData.getVehicles();
        for (int i = 0; i < CarList.size(); i++) {
            String[] splitArray = CarList.get(i).split("∑");
            if (splitArray[8].equals(CarSelected)) {
                vehMarca = splitArray[1];
                vehModelo = splitArray[2];
                num_tag = splitArray[3];
                ctl_contract_type = splitArray[5];
                vehPlacas = splitArray[8];
                vehColor = splitArray[9];
                vehAnio = splitArray[10];
                ctl_stall_id = splitArray[11];
                ctl_user_id = splitArray[12];
                ctl_id = splitArray[13];
            }
        }
        ConstraintLayout mainContainer = binding.mainContainer;

        loaderInflater = (LayoutInflater) requireActivity().getSystemService(requireActivity().LAYOUT_INFLATER_SERVICE);
        popupView = loaderInflater.inflate(R.layout.loader, null);
        view = binding.LinearLayoutContainer.getRootView();
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(popupView, width, height, false);
        popupWindow.setElevation(20);
        ImageView LoaderGif = popupView.findViewById(R.id.loader_gif);
        Glide.with(requireContext())
                .load(R.drawable.linea_expres_loader)
                .into(LoaderGif);

        popupWindow.showAtLocation(popupView, Gravity.TOP|Gravity.END, 0, 0);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream;
                    String accountActivation_url = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/le/crossings/"+ctl_user_id+"/"+ctl_id+"";

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
                    JSONArray Result = new JSONArray(ResponseData);
                    System.out.println("Este es el Result de Register Fragment " + Result);

                    if (Result.length() == 0) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Su vehículo no cuenta con cruces.", Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                            MainActivity.Navigation_Requests("BackView");
                        });
                    }

                    for (int i = 0; i < Result.length(); i++) {
                        JSONObject Cruces = (JSONObject) Result.get(i);
                        String crossing_date = (String) Cruces.getString("crossing_date");
                        String prev_balance = (String) Cruces.getString("previous_crossings");
                        String current_balance = (String) Cruces.getString("current_crossings");

                        String[] parts = crossing_date.split("T");

                        String Date = parts[0];
                        String Hour = parts[1];

                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                        symbols.setDecimalSeparator('.');
                        symbols.setGroupingSeparator(',');

                        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
                        double prev_Balance = Double.parseDouble(prev_balance);
                        double current_Calance = Double.parseDouble(current_balance);
                        prev_balance = df.format(prev_Balance);
                        current_balance = df.format(current_Calance);

                        String finalPrev_balance = prev_balance;
                        String finalCurrent_balance = current_balance;
                        requireActivity().runOnUiThread(() -> {
                            View plantillaView = inflater.inflate(R.layout.vehiculos_perfil_cruces_plantilla, linearLayout, false);
                            TextView dateCrossing = (TextView) plantillaView.findViewById(R.id.dateCrossing);
                            TextView hourCrossing = (TextView) plantillaView.findViewById(R.id.hourCrossing);
                            TextView vehicleData = (TextView) plantillaView.findViewById(R.id.vehicleData);
                            TextView vehiclePrevBalance = (TextView) plantillaView.findViewById(R.id.vehiclePrevBalance);
                            TextView vehicleCurrentBalance = (TextView) plantillaView.findViewById(R.id.vehicleCurrentBalance);



                            dateCrossing.setText(Date);
                            hourCrossing.setText(Hour);
                            vehicleData.setText(vehMarca + " " + vehModelo + " " + vehAnio);
                            vehiclePrevBalance.setText("Saldo antes del cruce: $" + finalPrev_balance);
                            vehicleCurrentBalance.setText("Saldo después del cruce: $" + finalCurrent_balance);


                            linearLayout.addView(plantillaView);
                        });
                    }

                    requireActivity().runOnUiThread(() -> {
                        popupWindow.dismiss();
                    });
                    conn.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        thread.start();



        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}