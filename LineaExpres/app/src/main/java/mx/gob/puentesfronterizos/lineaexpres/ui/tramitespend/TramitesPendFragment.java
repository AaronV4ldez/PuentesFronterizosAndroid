


package mx.gob.puentesfronterizos.lineaexpres.ui.tramitespend;


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
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentTramitesPendBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class TramitesPendFragment extends Fragment {
    String TAG = "Tramites Pendientes";
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
    private FragmentTramitesPendBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TramitesPendViewModel tramitesPendFragment =
                new ViewModelProvider(this).get(TramitesPendViewModel.class);

        binding = FragmentTramitesPendBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userLog = new UserLog(requireContext()); //Open Users db
        UpdateData = new updateData(requireContext());
        sqlOnInit = new SQLOnInit(requireContext());
        sqlOnInit.cleanTramites();
        sqlOnInit.InsertTramitesOnInit();

        ArrayList<String> userData = userLog.GetUserData();
        Token = userData.get(1);

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
            root = null;
        }

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

       

        return root;
    }

    public void getTramites(String Token){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = getResources().getString(R.string.apiURL) + "api/v1/procs";

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
                System.out.println("Este es el responseData de Register Fragment " + ResponseData);


                JSONArray Result = new JSONArray(ResponseData);


                for (int i = 0; i < Result.length(); i++) {
                    JSONObject Tramites = (JSONObject) Result.get(i);
                    int TramiteID = (int) Tramites.get("id");
                    //String Tramite = (String) Tramites.get("tramite");
                    int idProcedure = (int) Tramites.get("id_procedure");


                    checkFilesStatus(TramiteID, idProcedure, Token);
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

    public void checkFilesStatus(int id_proc, int id_proc_type, String Token){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = getResources().getString(R.string.apiURL) + "api/v1/files?id_proc="+id_proc+"&id_proc_type="+id_proc_type+"";

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

                System.out.println("CheckFiles Res: " + ResponseData);


                JSONArray Result = new JSONArray(ResponseData);
                String comment = "";

                for (int i = 0; i < Result.length(); i++) {
                    JSONObject FilesRes = (JSONObject) Result.get(i);
                    int idFile = (int) FilesRes.get("id_file_type");
                    int file_status = (int) FilesRes.get("file_status");
                    String file_type_desc = (String) FilesRes.get("file_type_desc");
                    try {
                        comment = (String) FilesRes.get("comment");
                    } catch (Exception e) {
                        comment = "";
                    }

                    if (getActivity() != null ) {
                        Button badDocument = new Button(new ContextThemeWrapper(requireActivity(), R.style.login_btns), null, R.style.login_btns);
                        badDocument.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) badDocument.getLayoutParams();
                        if (file_status == 1) {
                            String finalComment = comment;
                            requireActivity().runOnUiThread(() -> {
                                if (id_proc_type == 1) {
                                    inscriptionContainerLayout.setVisibility(View.VISIBLE);
                                    inscriptionContainerLayoutTitle.setVisibility(View.VISIBLE);
                                    badDocument.setText(file_type_desc + ": Rechazado \n Razon: " + finalComment);

                                    inscriptionContainerLayout.addView(badDocument);
                                    params.bottomMargin = 30;
                                    badDocument.setHeight(180);
                                    badDocument.setTypeface(null, Typeface.BOLD);
                                    badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setTextSize(16);
                                    badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setOnClickListener(view -> {
                                        modifyValues(id_proc, id_proc_type, "file", idFile, file_type_desc, finalComment);
                                    });

                                }
                                if (id_proc_type == 2) {
                                    bridgeContainerLayout.setVisibility(View.VISIBLE);
                                    bridgeContainerLayoutTitle.setVisibility(View.VISIBLE);
                                    badDocument.setText(file_type_desc + ": Rechazado \n Razon: " + finalComment);

                                    bridgeContainerLayout.addView(badDocument);
                                    params.bottomMargin = 30;
                                    badDocument.setHeight(180);
                                    badDocument.setTypeface(null, Typeface.BOLD);
                                    badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setTextSize(16);
                                    badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setOnClickListener(view -> {
                                        modifyValues(id_proc, id_proc_type, "file", idFile, file_type_desc, finalComment);
                                    });
                                }
                                if (id_proc_type == 3) {
                                    changeVehicleContainerLayout.setVisibility(View.VISIBLE);
                                    changeVehicleContainerLayoutTitle.setVisibility(View.VISIBLE);
                                    badDocument.setText(file_type_desc + ": Rechazado \n Razon: " + finalComment);

                                    changeVehicleContainerLayout.addView(badDocument);
                                    params.bottomMargin = 30;
                                    badDocument.setHeight(180);
                                    badDocument.setTypeface(null, Typeface.BOLD);
                                    badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setTextSize(16);
                                    badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setOnClickListener(view -> {
                                        modifyValues(id_proc, id_proc_type, "file", idFile, file_type_desc, finalComment);
                                    });
                                }
                                if (id_proc_type == 4) {
                                    balanceTransferContainerLayout.setVisibility(View.VISIBLE);
                                    balanceTransferContainerLayoutTitle.setVisibility(View.VISIBLE);
                                    badDocument.setText(file_type_desc + ": Rechazado \n Razon: " + finalComment);

                                    balanceTransferContainerLayout.addView(badDocument);
                                    params.bottomMargin = 30;
                                    badDocument.setHeight(180);
                                    badDocument.setTypeface(null, Typeface.BOLD);
                                    badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setTextSize(16);
                                    badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setOnClickListener(view -> {
                                        modifyValues(id_proc, id_proc_type, "file", idFile, file_type_desc, finalComment);
                                    });
                                }
                                if (id_proc_type == 5) {
                                    bajaVehicleOrTagContainerLayout.setVisibility(View.VISIBLE);
                                    bajaVehicleOrTagContainerLayoutTitle.setVisibility(View.VISIBLE);
                                    badDocument.setText(file_type_desc + ": Rechazado \n Razon: " + finalComment);

                                    bajaVehicleOrTagContainerLayout.addView(badDocument);
                                    params.bottomMargin = 30;
                                    badDocument.setHeight(180);
                                    badDocument.setTypeface(null, Typeface.BOLD);
                                    badDocument.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setTextSize(16);
                                    badDocument.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    badDocument.setOnClickListener(view -> {
                                        modifyValues(id_proc, id_proc_type, "file", idFile, file_type_desc, finalComment);
                                    });
                                }
                            });
                        }
                    }






                }

                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void modifyValues(int id_proc, int id_proc_type, String val_type, int idFile, String file_desc, String comment) {
        if (val_type.contains("file")) {
            UpdateData.FixDataFiles(id_proc, id_proc_type, idFile, file_desc, comment);
            requireActivity().runOnUiThread(() -> {
                MainActivity.nav_req(R.id.navigation_fix_data);
            });
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTramites(Token);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}