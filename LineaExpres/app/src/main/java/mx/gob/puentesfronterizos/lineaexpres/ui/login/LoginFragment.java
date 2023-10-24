package mx.gob.puentesfronterizos.lineaexpres.ui.login;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentLoginBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;


public class LoginFragment extends Fragment {
    private static final String TAG = "Login Fragment";
    MainActivity mainActivity;
    EditText User;
    TextView userEmptyTView;
    EditText Password;
    TextView passwordEmptyTView;
    //EditText Sentri;
    TextView sentriEmptyTView;
    String loginUrl = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/session/login";

    UserLog userLog;

    Button Login_ForgotPass;
    Button Login_TermsAndConditionsBtn;
    Button Login_LoginBtn;
    Button login_registerBtn;

    private FragmentLoginBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LoginViewModel loginViewModel =
                new ViewModelProvider(this).get(LoginViewModel.class);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userLog = new UserLog(requireContext()); //Open Users db

        ArrayList<String> UserLoggedConfirmation = userLog.GetUserData();
        if (UserLoggedConfirmation.get(0) != null) {
            MainActivity.Navigation_Requests("BackView");
        }

        User = binding.LoginEmailInput;
        userEmptyTView = binding.userEmpty;

        mainActivity = new MainActivity();

        Password = binding.LoginPasswordInput;
        passwordEmptyTView = binding.passwordEmpty;

        //Sentri = binding.LoginSentriInput;
        //sentriEmptyTView = binding.sentriEmpty;

        //Buttons
        Login_ForgotPass = binding.LoginForgotPasswordBtn;
        Login_LoginBtn = binding.LoginLoginBtn;
        Login_TermsAndConditionsBtn = binding.LoginTermsAndConditionsBtn;

        login_registerBtn = binding.RegisterBtn;


        login_registerBtn.setOnClickListener(v -> {
            MainActivity.nav_req(R.id.navigation_register);
        });

        Login_LoginBtn.setOnClickListener(v -> {

            if (!User.getText().toString().contains("@"))  {
                userEmptyTView.setText("El email debe tener un formato válido.");
            }else{
                userEmptyTView.setText("");
            }
            if (Password.getText().length() <= 7)  {
                passwordEmptyTView.setText("La contraseña debe tener al menos 8 digitos.");
            }else {
                passwordEmptyTView.setText("");
            }
            boolean checkUserContains = User.getText().toString().contains("@");
            boolean checkUserNotEmpty = !TextUtils.isEmpty(User.getText().toString());

            boolean checkPasswordNotEmpty = !TextUtils.isEmpty(Password.getText().toString());
            boolean checkPasswordNotMinor = Password.getText().length()  >= 8;

            if (checkUserNotEmpty && checkUserContains && checkPasswordNotEmpty && checkPasswordNotMinor) {
                httpPostRequest(requireContext(), loginUrl, User.getText().toString(), Password.getText().toString());
            }
        });

        Login_TermsAndConditionsBtn.setOnClickListener(v -> {
            MainActivity.nav_req(R.id.navigation_terms);
        });

        Login_ForgotPass.setOnClickListener(v -> {
            MainActivity.nav_req(R.id.navigation_forgotpass);
        });

        return root;
    }

    public void httpPostRequest(Context context, String url, String userlogin, String password) {

        new Thread(() -> {
            try {
                // Create JSON request body
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("userlogin", userlogin);
                jsonParam.put("password", password);

                // Open connection and set properties
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Write JSON request body to connection
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                // Check HTTP response code
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response body
                    Scanner scanner = new Scanner(new BufferedInputStream(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        stringBuilder.append(scanner.nextLine());
                    }
                    scanner.close();
                    String responseData = stringBuilder.toString();

                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(responseData);
                    String tokenAccess = jsonObject.getString("access_token");
                    String fullName = jsonObject.getString("name");
                    String passwordTemp = jsonObject.getString("user_set_pwd");
                    String Sentri = jsonObject.getString("sentri").isEmpty() ? "" : jsonObject.getString("sentri");
                    String FechaSentri = jsonObject.getString("sentri_exp_date").isEmpty() ? "" : jsonObject.getString("sentri_exp_date");
                    String facRazonSocial = jsonObject.getString("fac_razon_social").isEmpty() ? "" : jsonObject.getString("fac_razon_social");
                    String facRFC = jsonObject.getString("fac_rfc").isEmpty() ? "" : jsonObject.getString("fac_rfc");
                    String facDomFiscal = jsonObject.getString("fac_dom_fiscal").isEmpty() ? "" : jsonObject.getString("fac_dom_fiscal");
                    String facCP = jsonObject.getString("fac_cp").isEmpty() ? "" : jsonObject.getString("fac_cp");
                    String facEmail = jsonObject.getString("fac_email").isEmpty() ? "" : jsonObject.getString("fac_email");
                    String facTelefono = jsonObject.getString("fac_telefono").isEmpty() ? "" : jsonObject.getString("fac_telefono");

                    // Save user data
                    boolean access = userLog.SetUserData(userlogin, tokenAccess, fullName, Sentri, FechaSentri, passwordTemp);
                    userLog.setBillingData(facRazonSocial, facRFC, facDomFiscal, facCP, facEmail, facTelefono);

                    if (access) {
                        // Show notification and navigate
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            show_Notification("Haz iniciado sesión.", "¡Bienvenido a Linea Exprés!");
                            getFireToken();
                        }
                        if (passwordTemp.contains("0")) {
                            requireActivity().runOnUiThread(() -> {
                                MainActivity.nav_req(R.id.navigation_changepass);
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                MainActivity.Navigation_Requests("BackView");
                            });
                        }
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    // Read error message from response body
                    Scanner scanner = new Scanner(new BufferedInputStream(conn.getErrorStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        stringBuilder.append(scanner.nextLine());
                    }
                    scanner.close();
                    String errorMessage = stringBuilder.toString();

                    // Show error message
                    if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            Toast.makeText(requireContext(), "Contacte a soporte para más información", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                // Disconnect connection
                conn.disconnect();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_Notification(String msgTitle, String msgBody){

        Intent intent=new Intent(requireContext(),MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"All", NotificationManager.IMPORTANCE_HIGH);
        PendingIntent pendingIntent=PendingIntent.getActivity(requireContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);
        Notification notification=new Notification.Builder(requireContext(),CHANNEL_ID)
                .setContentText(msgBody)
                .setContentTitle(msgTitle)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),                                                                                                 R.drawable.ic_stat_name))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();

        NotificationManager notificationManager=(NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);
    }

    public void getFireToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed" + task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println("MainAct Token: " + token);
                        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


                        userLog.UserFireToken("set", token);
                    }
                });
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}