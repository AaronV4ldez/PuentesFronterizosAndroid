package mx.gob.puentesfronterizos.lineaexpres.ui.sendpasschange;

import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentSendpasschangeBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class SendPassChangeFragment extends Fragment {


    private static final String TAG = "SendPassChange";
    ArrayList<String> userData;
    String User;
    String Token;

    UserLog userLog;
    updateData UpdateData;

    EditText currentEmail;
    TextView response;
    Button NextStepChangePass;

    String burl = "https://apis.fpfch.gob.mx/api/v1/user/resetpass";
    private FragmentSendpasschangeBinding binding;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SendPassChangeViewModel ProfileFragment =
                new ViewModelProvider(this).get(SendPassChangeViewModel.class);

        binding = FragmentSendpasschangeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userLog = new UserLog(requireActivity()); //Open local db connection
        UpdateData = new updateData(requireActivity()); //Open local db connection

        currentEmail = binding.currentEmail;
        NextStepChangePass = binding.NextStepChangePass;
        response = binding.response;

        userData = userLog.GetUserData();
        Token = userData.get(1);

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
            root = null;
        }
        currentEmail.setText(User);

        NextStepChangePass.setOnClickListener(v -> {
            sendTempPassword(User); //Enviar reset de password
            response.setVisibility(View.VISIBLE);
            //MainActivity.Navigation_Requests("ChangePass");


        });


        return root;
    }

    public void sendTempPassword(String email){
        new Thread(() -> {
            try {
                InputStream inputStream;

                URL url = new URL(burl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);

                Log.i(TAG, "httpPostRequest: " + jsonParam);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                int Status = conn.getResponseCode();
                String msg = conn.getResponseMessage();

                Log.i(TAG, "sendTempPassword: Status: " + Status);
                Log.i(TAG, "sendTempPassword: Message: " + msg);


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

                conn.disconnect();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //MainActivity.Navigation_Requests("ChangePass");
    }
}