package mx.gob.puentesfronterizos.lineaexpres.ui.profile;

import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import java.util.ArrayList;
import java.util.List;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.VehiculoSliderAdapter;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentProfileBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
import mx.gob.puentesfronterizos.lineaexpres.ui.profile.ProfileViewModel;

public class ProfileFragment extends Fragment {

    //Borrar Cuenta
    ConstraintLayout MainBorraCuentaLayout;
    Button btnBorrarCuenta;
    Button btnCancelarBorrarCuenta;
    Button btnConfirmarBorrarCuenta;

    ArrayList<String> userData;
    String User;
    String Token;
    String FName;
    String FirebaseToken;

    UserLog userLog;
    updateData UpdateData;
    SQLOnInit sqlOnInit;

    Button ButtonChangePassword;
    Button ButtonChangeEmail;
    Button ButtonTramitesPend;
    Button ButtonCitas;
    Button currentCitaChangeButton;

    Button btnProfileCars;
    Button btnAddCars;
    Button btnBillData;
    Button btnLogout;


    TextView titleProfile;
    TextView emailProfile;
    TextView titleTramites;
    TextView titleCitas;
    TextView currentCitaChangeTitle;

    View hr1;
    View hr2;
    View hr3;
    View hr4;

    Button btnLineaExpres;
    Button btnTelepeaje;
    Button btnADP;
    Button goToSolInsc;
    TextView textView2;


    public final String TAG = "Profile";
    private FragmentProfileBinding binding;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel ProfileFragment =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userLog = new UserLog(requireActivity()); //Open local db connection
        sqlOnInit = new SQLOnInit(requireActivity()); //Open local db connection
        UpdateData = new updateData(requireActivity()); //Open local db connection

        //Borrar cuenta
        MainBorraCuentaLayout = binding.MainBorraCuentaLayout;
        btnBorrarCuenta = binding.btnBorrarCuenta;

        btnCancelarBorrarCuenta = binding.btnCancelarBorrarCuenta;
        btnConfirmarBorrarCuenta = binding.btnAceptarCancelarCuenta;

        btnBorrarCuenta.setOnClickListener(v -> {
            MainBorraCuentaLayout.setVisibility(View.VISIBLE);
        });

        btnCancelarBorrarCuenta.setOnClickListener(v -> {
            MainBorraCuentaLayout.setVisibility(View.GONE);
        });

        btnConfirmarBorrarCuenta.setOnClickListener(v -> {
            postDeleteAccount();
        });

        ButtonTramitesPend = binding.ButtonTramites;
        ButtonCitas = binding.ButtonCitas;

        titleProfile = binding.titleProfile;
        emailProfile = binding.emailProfile;
        titleTramites = binding.titleTramites;
        titleCitas = binding.citasTitle;

        currentCitaChangeTitle = binding.currentCitaChangeTitle;
        currentCitaChangeButton = binding.currentCitaChangeButton;

        hr1 = binding.hr1;
        hr2 = binding.hr2;
        hr3 = binding.hr3;
        hr4 = binding.hr4;

        btnLineaExpres = binding.btnLineaExpres;
        btnTelepeaje = binding.btnTelepeaje;
        btnADP = binding.btnADP;
        goToSolInsc = binding.goToSolInsc;
        textView2 = binding.textView2;

        sqlOnInit.cleanTramites();
        sqlOnInit.InsertTramitesOnInit();


        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);
        FName = userData.get(2);
        FirebaseToken = userData.get(3);
        titleProfile.setText("Bienvenido \n" + FName);
        emailProfile.setText("Email registrado: " + User);

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
            root = null;
        }

        //ButtonChangePassword.setOnClickListener(v -> {
        //    MainActivity.nav_req(R.id.navigation_sendpasschange);
        //});
        //ButtonChangeEmail.setOnClickListener(view -> {
        //    MainActivity.nav_req(R.id.navigation_changeemail);
        //});
        ButtonTramitesPend.setOnClickListener(view -> {
            MainActivity.nav_req(R.id.navigation_tramites_pend);
        });
        ButtonCitas.setOnClickListener(view -> {
            MainActivity.nav_req(R.id.navigation_current_citas);
        });
        currentCitaChangeButton.setOnClickListener(view -> {
            MainActivity.nav_req(R.id.navigation_current_change_citas);
        });
        //btnBillData.setOnClickListener(view -> {
        //    MainActivity.nav_req(R.id.navigation_facturacion);
        //});
        //btnLogout.setOnClickListener(view -> {
        //    MainActivity.nav_req(R.id.navigation_logout);
        //});
        //btnProfileCars.setOnClickListener(view -> {
        //    MainActivity.nav_req(R.id.navigation_vehiculos_perfil);
        //});
        //btnAddCars.setOnClickListener(view -> {
        //    MainActivity.nav_req(R.id.navigation_choose_veh_add);
        //});



        btnLineaExpres.setOnClickListener(view -> {
            MainActivity.nav_req(R.id.navigation_linea_expres_add_vehicle);
        });
        btnTelepeaje.setOnClickListener(view -> {
            MainActivity.nav_req(R.id.navigation_telepeaje_add_vehicle);
        });

        btnADP.setOnClickListener(view -> {
            MainActivity.nav_req(R.id.navigation_adp_add_vehicle);
        });

        goToSolInsc.setOnClickListener(view -> {
            UpdateData.updateCarSelected("Inscripcion");
            MainActivity.nav_req(R.id.navigation_req_inscription);

        });


        getVehiculos();
        sendFirebaseToken();

        return root;
    }

    public void getTramites(){
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

                int citasCount = 0;
                int CurrentcitasCount = 0;
                for (int i = 0; i < Result.length(); i++) {
                    JSONObject Tramites = (JSONObject) Result.get(i);
                    System.out.println(Tramites);
                    int TramiteID = (int) Tramites.get("id");

                    int idProcedure = (int) Tramites.get("id_procedure");
                    int id_procedure_status = (int) Tramites.get("id_procedure_status");

                    if (id_procedure_status == 4) {
                        citasCount++;
                        int finalCitasCount = citasCount;
                        requireActivity().runOnUiThread(() -> {
                            titleCitas.setVisibility(View.VISIBLE);
                            titleCitas.setText("Tienes " + finalCitasCount + " citas disponibles");
                            hr3.setVisibility(View.VISIBLE);
                            hr1.setVisibility(View.GONE);
                            ButtonCitas.setVisibility(View.VISIBLE);
                        });
                    }

                    if (id_procedure_status == 5) {
                        CurrentcitasCount++;
                        int finalCurrentcitasCount = CurrentcitasCount;
                        requireActivity().runOnUiThread(() -> {
                            currentCitaChangeTitle.setVisibility(View.VISIBLE);
                            currentCitaChangeTitle.setText("Puedes cambiar la fecha de " + finalCurrentcitasCount + " citas");
                            hr4.setVisibility(View.VISIBLE);
                            hr1.setVisibility(View.GONE);
                            currentCitaChangeButton.setVisibility(View.VISIBLE);
                        });
                    }

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

    public void sendFirebaseToken(){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String url_process = getResources().getString(R.string.apiURL) + "api/v1/user/saveid";

                URL url = new URL(url_process);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", User);
                jsonParam.put("device_id", FirebaseToken);


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
                System.out.println("Este es el response Data de firebasetoken  " + ResponseData);


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
                int tramitesCount = 0;

                for (int i = 0; i < Result.length(); i++) {

                    JSONObject FilesRes = (JSONObject) Result.get(i);
                    int file_status = (int) FilesRes.get("file_status");

                    if (file_status == 1) {
                        tramitesCount = tramitesCount + 1;
                        int finalTramitesCount = tramitesCount;
                        requireActivity().runOnUiThread(() -> {
                            titleTramites.setVisibility(View.VISIBLE);
                            titleTramites.setText("Tienes "+ finalTramitesCount +" tramites pendientes");
                            hr2.setVisibility(View.VISIBLE);
                            ButtonTramitesPend.setVisibility(View.VISIBLE);
                        });
                    }

                }



                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTramites();
    }

    public void getVehiculos(){

        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = getResources().getString(R.string.apiURL) + "api/v1/vehicles";

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
                if (element.isJsonArray()) {
                    JSONArray Result = new JSONArray(ResponseData);
                    System.out.println("Este es el Result de Register Fragment " + Result);
                    List<Integer> numeros = new ArrayList<Integer>();

                    for (int i = 0; i < Result.length(); i++) {
                        JSONObject Tramites = (JSONObject) Result.get(i);
                        int tipoVeh = (int) Tramites.get("tipo");

                        System.out.println("Este es el tipoVeh: " + tipoVeh);

                        numeros.add(tipoVeh);



                    }
                    boolean contieneUno = false;

                    for (int i = 0; i < numeros.size(); i++) {
                        if (numeros.get(i) == 1) {
                            contieneUno = true;
                            break; // Si encontramos un "1", salimos del bucle
                        }
                    }

                    if (!contieneUno) {
                        requireActivity().runOnUiThread(() -> {
                            btnLineaExpres.setVisibility(View.VISIBLE);
                            textView2.setVisibility(View.VISIBLE);
                            goToSolInsc.setVisibility(View.VISIBLE);
                        });
                    }



                }
                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void postDeleteAccount(){
        new Thread(() -> {
            try {
                InputStream inputStream;
                String accountActivation_url = getResources().getString(R.string.apiURL) + "api/v1/user";

                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "text");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("DELETE");

                int Status = conn.getResponseCode();

                Log.i(TAG, "httpPostRequest: status = " + conn.getResponseCode());
                Log.i(TAG, "httpPostRequest: msg = " + conn.getResponseMessage());

                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                }else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);
                System.out.println("Este es el responseData de delete account " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);
                String message = (String) Result.get("message");

                if (message.contains("Usuario eliminado exitosamente.") || message.contains("La petición la realizó un usuario no válido.")) {
                    requireActivity().runOnUiThread(() -> {
                        MainActivity.nav_req(R.id.navigation_logout);
                    });
                }
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                });

                System.out.println(message);



                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}