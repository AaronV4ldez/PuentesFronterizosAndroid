package mx.gob.puentesfronterizos.lineaexpres.ui.forgotpass;

import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
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

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentForgotpassBinding;

public class ForgotpassFragment extends Fragment {
    private static final String TAG = "ForgotPassword";
    String burl = getResources().getString(R.string.apiURL) + "api/v1/user/resetpass";
    TextView mailLabel;
    private FragmentForgotpassBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ForgotpassViewModel ForgotpassViewModel = new ViewModelProvider(this).get(ForgotpassViewModel.class);
        binding = FragmentForgotpassBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText inputMail = binding.inputMail;
        mailLabel = binding.mailLabel;
        Button sendReqChangePass = binding.sendReqChangePass;

        sendReqChangePass.setOnClickListener(v -> {
            if (inputMail.getText().length() == 0) {
                mailLabel.setText("Por favor, coloque su usuario.");
                mailLabel.setVisibility(View.VISIBLE);
                return;
            }

            sendTempPassword(inputMail.getText().toString());

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

                if (messageRes.contains("Usuario inexistente")) {
                    String newMessage = "Usuario inexistente.";
                    requireActivity().runOnUiThread(() -> {
                        mailLabel.setText(newMessage);
                        mailLabel.setVisibility(View.VISIBLE);
                    });
                    return;
                }
                if (msg.contains("OK")) {

                    String newMessage = "Contraseña generada exitosamente\nRevise su email, ahí podrá realizar el cambio de contraseña y podrá iniciar sesión en la app rápidamente.";
                    requireActivity().runOnUiThread(() -> {
                        mailLabel.setText(newMessage);
                        mailLabel.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            show_Notification("Reset realizado", "Mira tu bandeja de email y sigue las instrucciones");
                        }
                    });
                    return;
                }
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
