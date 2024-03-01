package mx.gob.puentesfronterizos.lineaexpres.ui.register;

import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentRegisterBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private FragmentRegisterBinding binding;

    EditText ETFullName;
    EditText InputEmail;
    EditText Number;
    EditText passwordTyped;
    EditText passwordReTyped;
    EditText InputCode;

    TextView smsLabel;
    TextView lblCode;

    Button ConfirmCode;
    Button btnSendCode;
    Button confirmRegister;
    Button btnWrongNumber;
    String lada = "";

    Boolean wrongNumber = false;


    //db
    UserLog userLog;
    Spinner numberLabel;

    String u =  "https://apis.fpfch.gob.mx/api/v1/user/signup";
    //String u =  requireContext().getResources().getString(R.string.apiURL) + "api/v1/user/signup";

    String accountActivation_url =  "https://apis.fpfch.gob.mx/api/v1/user/validate";
    //String accountActivation_url =  requireContext().getResources().getString(R.string.apiURL) + "api/v1/user/validate";

    String Email = "";
    String PhoneNumber = "";
    String Password = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RegisterViewModel registerViewModel =
                new ViewModelProvider(this).get(RegisterViewModel.class);

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userLog = new UserLog(requireContext()); //Open Users db

        ArrayList<String> UserLoggedConfirmation = userLog.GetUserData();
        if (UserLoggedConfirmation.get(0) != null) {
            MainActivity.nav_req(R.id.navigation_home);
        }


        //Binding layout
        ETFullName = binding.ETFullName;
        InputEmail = binding.Email;
        Number = binding.Number;
        passwordTyped = binding.passwordTyped;
        passwordReTyped = binding.passwordReTyped;
        InputCode = binding.EnterCode;
        //Binding Button
        confirmRegister = binding.RegisterUser;
        lblCode = binding.codeLbl;
        btnSendCode = binding.BtnReSendCode;
        ConfirmCode = binding.ConfirmCode;
        numberLabel = binding.numberLabel;
        btnWrongNumber = binding.btnWrongNumber;
        String[] datos = new String[] {"MEX", "USA"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(datos));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(numberLabel.getContext(), android.R.layout.simple_list_item_1,arrayList);
        numberLabel.setAdapter(arrayAdapter);

        smsLabel = binding.smsLabel;

        confirmRegister.setOnClickListener(v -> {

            //Getting all field data
            String FullNameString = ETFullName.getText().toString();
            String EmailString = InputEmail.getText().toString();
            String NumberString = Number.getText().toString();
            String NumberLada = numberLabel.getSelectedItem().toString();
            String PasswordString = passwordTyped.getText().toString();
            String RePasswordString = passwordReTyped.getText().toString();

            if (NumberLada.equals("MEX")) {
                lada = "+52";
            }
            if (NumberLada.equals("USA")) {
                lada = "+1";
            }
            if (NumberLada.equals("")) {
                lada = "";
            }
            if (lada.equals("")) {
                return;
            }


            //Checking password
            if (FullNameString.equals("") || EmailString.equals("") || NumberString.equals("")) {
                Toast.makeText(requireContext(), "Todos los campos deben ser rellenados.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PasswordString.length() < 8) {
                Toast.makeText(requireContext(), "La contraseña debe tener al menos 8 caracteres.", Toast.LENGTH_SHORT).show();

            } else if (!PasswordString.equals(RePasswordString)) {
               Toast.makeText(requireContext(), "Las contraseñas deben ser iguales", Toast.LENGTH_SHORT).show();
                Log.d("Registro", Email);
                Log.d("Registro2", NumberString);
                Log.d("Registro3", PasswordString);
            }else {
                Email = EmailString;
                PhoneNumber = NumberString;
                Password = PasswordString;
                doRegister(FullNameString, EmailString, NumberString, lada, PasswordString);

                //hashingPassword(passwordReTyped.getText().toString());
                Log.d("Registro", EmailString);
                Log.d("Registro2", NumberString);
                Log.d("Registro3", PasswordString);

            }


        });

        ConfirmCode.setOnClickListener(v -> {
            if (wrongNumber) {
                System.out.println("Número equivocado");
                wrongNumber = false;
                Number.setFocusable(false);
                Number.setEnabled(false);
                ConfirmCode.setText("Confirmar código");
                numberLabel.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Se ha enviado un código de verificación.", Toast.LENGTH_SHORT).show();
                PhoneNumber = Number.getText().toString();
                sendCodeAgain(Email, PhoneNumber);

            }else {
            String EmailString = InputEmail.getText().toString();
            String CodeString = InputCode.getText().toString();
            if (EmailString.equals("")) {
                Toast.makeText(requireContext(), "El correo debe estar completo", Toast.LENGTH_SHORT).show();
                return;
            }
            if (CodeString.equals("")) {
                Toast.makeText(requireContext(), "El código debe ser correcto", Toast.LENGTH_SHORT).show();
                return;
            }
            if (CodeString.length() != 5) {
                Toast.makeText(requireContext(), "El código consta de 5 dígitos.", Toast.LENGTH_SHORT).show();
                return;
            }

            confirmCode(EmailString, CodeString);
            }

        });

        btnSendCode.setOnClickListener(v -> {
            //Reenviar cógido
            sendCodeAgain(Email, PhoneNumber);

        });

        btnWrongNumber.setOnClickListener(v -> {
            //Se equivocó de número
            wrongNumber = true;
            numberLabel.setVisibility(View.VISIBLE);
            ConfirmCode.setText("Confirmar Número");
            Number.setFocusable(true);
            Number.setEnabled(true);
            Number.setFocusableInTouchMode(true);
            Number.setActivated(true);


        });

        return root;
    }

    public void doRegister(String FullName, String Email, String Phone, String lada, String Password) {
        new Thread(() -> {
            String messageRes = "";
            String smsRes = "";
            try {

                InputStream inputStream;

                URL url = new URL(u);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("fullname", FullName);
                jsonParam.put("email", Email);
                jsonParam.put("phone", Phone);
                jsonParam.put("cc", lada);
                jsonParam.put("password", Password);
                Log.d("Test password", Password);

                Log.i(TAG, "httpPostRequest: " + jsonParam);
                Log.d("numero", lada  + Phone );

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                int Status = conn.getResponseCode();
                Log.i(TAG, "httpPostRequest: status = " + conn.getResponseCode());
                Log.i(TAG, "httpPostRequest: msg = " + conn.getResponseMessage());


                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                }else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);

                ResponseData = ResponseData.replaceAll("Message sent", "");
                System.out.println("Este es el responseData de Register Fragmnet " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);


                messageRes = (String) Result.get("message");

                try {
                    String finalMessageRes = messageRes;
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), finalMessageRes, Toast.LENGTH_SHORT).show();
                    });
                }catch (Exception e) {
                    throw e;
                }


                if (messageRes.equals("Usuario registrado exitosamente.")) {



                        requireActivity().runOnUiThread(() -> {
                            if (lada.equals("")) {
                                MainActivity.nav_req(R.id.navigation_account_verified);
                                return;
                            }
                            InputCode.setVisibility(View.VISIBLE);
                            smsLabel.setVisibility(View.VISIBLE);
                            ConfirmCode.setVisibility(View.VISIBLE);
                            lblCode.setVisibility(View.VISIBLE);
                            btnSendCode.setVisibility(View.VISIBLE);
                            btnWrongNumber.setVisibility(View.VISIBLE);
                            InputEmail.setEnabled(false);
                            InputEmail.setFocusable(false);


                            //Visibilty GONE after press register
                            ETFullName.setVisibility(View.GONE);
                            Number.setVisibility(View.GONE);
                            Number.setFocusable(false);
                            Number.setEnabled(false);
                            passwordTyped.setVisibility(View.GONE);
                            passwordReTyped.setVisibility(View.GONE);
                            confirmRegister.setVisibility(View.GONE);



                        });
                    return;
                }
                if (!messageRes.equals("Usuario ya existe.")) {
                    requireActivity().runOnUiThread(() -> {
                        if (lada.equals("")) {
                            MainActivity.nav_req(R.id.navigation_account_verified);
                            return;
                        }
                        System.out.println("El usuario que estás tratando de registrar, ya existe");
                        InputCode.setVisibility(View.VISIBLE);
                        ConfirmCode.setVisibility(View.VISIBLE);
                        smsLabel.setVisibility(View.VISIBLE);
                        lblCode.setVisibility(View.VISIBLE);
                        btnSendCode.setVisibility(View.VISIBLE);
                        btnWrongNumber.setVisibility(View.VISIBLE);


                        InputEmail.setEnabled(false);
                        InputEmail.setFocusable(false);


                        //Visibilty GONE after press register
                        ETFullName.setVisibility(View.GONE);
                        Number.setFocusable(false);
                        Number.setEnabled(false);


                        passwordTyped.setVisibility(View.GONE);
                        passwordReTyped.setVisibility(View.GONE);
                        confirmRegister.setVisibility(View.GONE);
                        numberLabel.setVisibility(View.GONE);

                    });

                    return;
                }

                conn.disconnect();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void confirmCode(String email, String activation_code){
        new Thread(() -> {
            try {

                InputStream inputStream;

                URL url = new URL("https://apis.fpfch.gob.mx/api/v1/user/validate");
                //URL url = new URL(getResources().getString(R.string.apiURL) + "api/v1/user/validate");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("activation_code", activation_code);

                Log.i(TAG, "httpPostRequest: " + jsonParam);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                int Status = conn.getResponseCode();
                Log.i(TAG, "httpPostRequest: status = " + conn.getResponseCode());
                Log.i(TAG, "httpPostRequest: msg = " + conn.getResponseMessage());


                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                }else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);

                ResponseData = ResponseData.replaceAll("Message sent", "");
                System.out.println("Este es el responseData de Register Fragmnet " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);

                String messageRes = (String) Result.get("message");
                requireActivity().runOnUiThread(() -> {
                    Spannable resCentered = new SpannableString(messageRes);
                    resCentered.setSpan(
                            new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                            0, messageRes.length() - 1,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    );

                    Toast.makeText(requireContext(), resCentered, Toast.LENGTH_SHORT).show();
                });

                if (messageRes.equals("Cuenta verificada exitosamente.") || messageRes.contains("Cuenta ya ha sido previamente verificada.")) {
                    requireActivity().runOnUiThread(() -> {
                    MainActivity.nav_req(R.id.navigation_login);
                        //Toast.makeText(requireContext(), "Inicia sesión para continuar", Toast.LENGTH_SHORT).show();
                        Toast.makeText(requireContext(), "Verifica tu correo para poder ingresar", Toast.LENGTH_SHORT).show();
                    });
                }

                conn.disconnect();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void sendCodeAgain(String email, String phone){
        new Thread(() -> {
            try {

                InputStream inputStream;

                URL url = new URL("https://apis.fpfch.gob.mx/api/v1/user/newvcode");
                //URL url = new URL(getResources().getString(R.string.apiURL) + "api/v1/user/newvcode");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("phone", phone);

                Log.i(TAG, "httpPostRequest: " + jsonParam);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                int Status = conn.getResponseCode();
                Log.i(TAG, "httpPostRequest: status = " + conn.getResponseCode());
                Log.i(TAG, "httpPostRequest: msg = " + conn.getResponseMessage());


                if (Status != 200) {
                    inputStream = new BufferedInputStream(conn.getErrorStream());
                }else {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                }
                String ResponseData = convertStreamToString(inputStream);

                ResponseData = ResponseData.replaceAll("Message sent", "");
                System.out.println("Este es el responseData de Register Fragmnet " + ResponseData);

                JSONObject Result = new JSONObject(ResponseData);

                String messageRes = (String) Result.get("message");
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show();
                });
                conn.disconnect();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}