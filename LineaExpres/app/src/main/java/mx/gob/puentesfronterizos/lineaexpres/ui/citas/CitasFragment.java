package mx.gob.puentesfronterizos.lineaexpres.ui.citas;


import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentCitasBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;


public class CitasFragment extends Fragment {

    String TAG = "Citas";
    LinearLayout inscriptionContainerLayout;
    LinearLayout bridgeContainerLayout;
    LinearLayout changeVehicleContainerLayout;
    LinearLayout balanceTransferContainerLayout;
    LinearLayout bajaVehicleOrTagContainerLayout;

    TextView inscriptionContainerLayoutTitle;
    TextView bridgeContainerLayoutTitle;
    TextView changeVehicleContainerLayoutTitle;
    TextView balanceTransferContainerLayoutTitle;
    TextView bajaVehicleOrTagContainerLayoutTitle;

    UserLog userLog;
    updateData UpdateData;
    SQLOnInit sqlOnInit;
    String Token;

    private FragmentCitasBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CitasViewModel FormalitiesViewModel = new ViewModelProvider(this).get(CitasViewModel.class);
        binding = FragmentCitasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        userLog = new UserLog(requireContext()); //Open Users db
        UpdateData = new updateData(requireContext());
        sqlOnInit = new SQLOnInit(requireContext());
        sqlOnInit.cleanTramites();
        sqlOnInit.InsertTramitesOnInit();

        inscriptionContainerLayout = binding.inscriptionContainer;
        bridgeContainerLayout = binding.bridgesContainer;
        changeVehicleContainerLayout = binding.vehicleChangeContainer;
        balanceTransferContainerLayout = binding.balanceTransferContainer;
        bajaVehicleOrTagContainerLayout = binding.bajaVehTagContainer;

        inscriptionContainerLayoutTitle = binding.inscriptionContainerTitle;
        bridgeContainerLayoutTitle = binding.bridgesContainerTitle;
        changeVehicleContainerLayoutTitle = binding.vehicleChangeContainerTitle;
        balanceTransferContainerLayoutTitle = binding.balanceTransferContainerTitle;
        bajaVehicleOrTagContainerLayoutTitle = binding.bajaVehTagContainerTitle;


        Token = userLog.GetUserData().get(1);
        getTramites(Token);
        return root;
    }

    public void getTramites(String Token){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/procs";

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

                //ResponseData = ResponseData.replaceAll("\\[", "");
                //ResponseData = ResponseData.replaceAll("]", "");
                System.out.println("Este es el responseData de citas " + ResponseData);


                JSONArray Result = new JSONArray(ResponseData);



                for (int i = 0; i < Result.length(); i++) {
                    JSONObject Tramites = (JSONObject) Result.get(i);
                    int id = (int) Tramites.get("id");
                    int id_procedure = (int) Tramites.get("id_procedure");
                    int id_procedure_status = (int) Tramites.get("id_procedure_status");
                    String TramiteStatus = (String) Tramites.get("tramite_status");
                    //String Tramite = (String) Tramites.get("tramite");
                    Button badDocument = new Button(new ContextThemeWrapper(requireActivity(), R.style.login_btns), null, R.style.login_btns);
                    badDocument.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) badDocument.getLayoutParams();

                    int buttonHeight = 200;
                    requireActivity().runOnUiThread(() -> {
                        if (id_procedure_status == 4) {
                            if (id_procedure == 1) {
                                inscriptionContainerLayout.setVisibility(View.VISIBLE);
                                inscriptionContainerLayoutTitle.setVisibility(View.VISIBLE);
                                badDocument.setText("\n" + TramiteStatus);

                                inscriptionContainerLayout.addView(badDocument);
                                params.bottomMargin = 30;
                                badDocument.setHeight(buttonHeight);
                                badDocument.setTypeface(null, Typeface.BOLD);
                                badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setTextSize(16);
                                badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setOnClickListener(view -> {
                                    modifyValues(id, id_procedure);
                                });

                            }
                            if (id_procedure == 2) {
                                bridgeContainerLayout.setVisibility(View.VISIBLE);
                                bridgeContainerLayoutTitle.setVisibility(View.VISIBLE);
                                badDocument.setText("\n" + TramiteStatus);

                                bridgeContainerLayout.addView(badDocument);
                                params.bottomMargin = 30;
                                badDocument.setHeight(buttonHeight);
                                badDocument.setTypeface(null, Typeface.BOLD);
                                badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setTextSize(16);
                                badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setOnClickListener(view -> {
                                     modifyValues(id, id_procedure);
                                });
                            }
                            if (id_procedure == 3) {
                                changeVehicleContainerLayout.setVisibility(View.VISIBLE);
                                changeVehicleContainerLayoutTitle.setVisibility(View.VISIBLE);
                                badDocument.setText("\n" + TramiteStatus);

                                changeVehicleContainerLayout.addView(badDocument);
                                params.bottomMargin = 30;
                                badDocument.setHeight(buttonHeight);
                                badDocument.setTypeface(null, Typeface.BOLD);
                                badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setTextSize(16);
                                badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setOnClickListener(view -> {
                                     modifyValues(id, id_procedure);
                                });
                            }
                            if (id_procedure == 4) {
                                balanceTransferContainerLayout.setVisibility(View.VISIBLE);
                                balanceTransferContainerLayoutTitle.setVisibility(View.VISIBLE);
                                badDocument.setText("\n" + TramiteStatus);

                                balanceTransferContainerLayout.addView(badDocument);
                                params.bottomMargin = 30;
                                badDocument.setHeight(buttonHeight);
                                badDocument.setTypeface(null, Typeface.BOLD);
                                badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setTextSize(16);
                                badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setOnClickListener(view -> {
                                     modifyValues(id, id_procedure);
                                });
                            }
                            if (id_procedure == 5) {
                                bajaVehicleOrTagContainerLayout.setVisibility(View.VISIBLE);
                                bajaVehicleOrTagContainerLayoutTitle.setVisibility(View.VISIBLE);
                                badDocument.setText("\n" + TramiteStatus);

                                bajaVehicleOrTagContainerLayout.addView(badDocument);
                                params.bottomMargin = 30;
                                badDocument.setHeight(buttonHeight);
                                badDocument.setTypeface(null, Typeface.BOLD);
                                badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setTextSize(16);
                                badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                badDocument.setOnClickListener(view -> {
                                     modifyValues(id, id_procedure);
                                });
                            }
                        }
                    });

                }

                if (Status == 200){
                    System.out.println("Result");
                }
                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }
    private void modifyValues(int id_proc, int id_proc_type) {
            UpdateData.InsertCita(id_proc, id_proc_type);
            requireActivity().runOnUiThread(() -> {
                MainActivity.nav_req(R.id.navigation_crearcita);
            });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}