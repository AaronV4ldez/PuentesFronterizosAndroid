package mx.gob.puentesfronterizos.lineaexpres.ui.changepass;


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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentChangePassBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;

public class ChangePassFragment extends Fragment {

    private static final String TAG = "ChangePass";
    EditText TempPass;
    EditText NewPass;
    EditText ConfirmNewPass;
    Button ConfirmChangePasswordBtn;

    TextView msgShow;
    TextView RegisterLbl;

    UserLog userLog;



    private FragmentChangePassBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChangePassViewModel ChangePassFragment =
                new ViewModelProvider(this).get(ChangePassViewModel.class);

        binding = FragmentChangePassBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Bindings
        TempPass = binding.TempPassword;
        NewPass = binding.newPassword;
        ConfirmNewPass = binding.ConfirmNewPass;
        RegisterLbl = binding.RegisterLbl;

        msgShow = binding.msgShow;

        ConfirmChangePasswordBtn = binding.ConfirmChangePasswordBtn;

        userLog = new UserLog(requireContext()); //Open Users db
        ArrayList<String> userData = userLog.GetUserData();
        String Token = userData.get(1);
        String user_set_pwd = userData.get(12);

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
            root = null;
        }else {
            if (user_set_pwd.contains("1")) {
                TempPass.setHint("Contraseña actual");
                RegisterLbl.setText("Confirmación de cambio de contraseña");
            }

            ConfirmChangePasswordBtn.setOnClickListener(v -> {

                //Get Values
                String TempPassText = TempPass.getText().toString();
                String NewPassText = NewPass.getText().toString();
                String ConfirmNewPassText = ConfirmNewPass.getText().toString();

                if (TempPassText.length() == 0) {
                    Toast.makeText(requireContext(), "Coloque la contraseña temporal que llegó a su correo.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (NewPassText.length() < 8) {
                    Toast.makeText(requireContext(), "La nueva contraseña debe tener al menos 8 caracteres.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ConfirmNewPassText.length() < 8) {
                    Toast.makeText(requireContext(), "Debe reescribir la nueva contraseña en el tercer campo.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!ConfirmNewPassText.contains(NewPassText)) {
                    Toast.makeText(requireContext(), "Confirme su contraseña por favor.", Toast.LENGTH_SHORT).show();
                    return;
                }
                TempPass.setText("");
                NewPass.setText("");
                ConfirmNewPass.setText("");

                ChangePass(TempPassText, NewPassText, Token);


            });

        }




        return root;
    }
    public void ChangePass(String TempPass, String NewPass, String Token){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/user/changepass";

                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("current_password", TempPass);
                jsonParam.put("new_password", NewPass);

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

                if (messageRes.contains("Invalid token format")) {
                    requireActivity().runOnUiThread(() -> {
                        Spannable resCentered = new SpannableString("Inicia sesión para hacer esto.");
                        resCentered.setSpan(
                                new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, messageRes.length() - 1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        );

                        Toast.makeText(requireContext(), resCentered, Toast.LENGTH_SHORT).show();
                    });
                    return;
                }


                requireActivity().runOnUiThread(() -> {

                    msgShow.setText(messageRes);

                    //Spannable resCentered = new SpannableString(messageRes);
                    //resCentered.setSpan(
                    //        new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                    //        0, messageRes.length() - 1,
                    //        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    //);
//
                    //Toast.makeText(requireContext(), resCentered, Toast.LENGTH_SHORT).show();
                });

                if (Status == 200){
                    requireActivity().runOnUiThread(() -> {
                        // MainActivity.nav_req(R.id.navigation_home);
                    });
                }


                conn.disconnect();
            } catch (IOException | JSONException e) {
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