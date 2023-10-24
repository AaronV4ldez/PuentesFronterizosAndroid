package mx.gob.puentesfronterizos.lineaexpres.ui.changeemail;


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
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentChangeEmailBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.ui.changeemail.ChangeEmailViewModel;

public class ChangeEmailFragment extends Fragment {

    private static final String TAG = "ChangeEmail";

    Button ConfirmChangeEmailBtn;

    EditText newEmail;
    EditText currentEmail;

    TextView msgShow;
    UserLog userLog;
    SQLOnInit sqlOnInit;
    Handler handler;
    LayoutInflater inflater;
    View popupView;
    View view;
    int width;
    int height;
    PopupWindow popupWindow;

    private FragmentChangeEmailBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChangeEmailViewModel ChangePassFragment =
                new ViewModelProvider(this).get(ChangeEmailViewModel.class);

        binding = FragmentChangeEmailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Bindings


        msgShow = binding.msgShow;
        newEmail = binding.newEmail;
        currentEmail = binding.currentEmail;

        ConfirmChangeEmailBtn = binding.ConfirmChangeEmailBtn;

        userLog = new UserLog(requireContext()); //Open Users db
        sqlOnInit = new SQLOnInit(requireContext());
        String CurrentEmail = userLog.GetUserData().get(0);
        currentEmail.setText(CurrentEmail);
        ArrayList<String> userData = userLog.GetUserData();
        String Token = userData.get(1);

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
            root = null;
        }

        handler = new Handler();
        ConfirmChangeEmailBtn.setOnClickListener(v -> {
            String NewEmail = newEmail.getText().toString();
            msgShow.setText("Recibirás un código por mensaje y un correo, favor de revisar tu bandeja de entrada.");
            handler.postDelayed(() -> sendNewEMail(NewEmail, Token), 2000);
            //handler.postDelayed(() -> popupWindow.dismiss(), 5000);

        });

        return root;
    }
    public void sendNewEMail(String NewEmail, String Token){
        new Thread(() -> {
            try {

                InputStream inputStream;
                String accountActivation_url = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/user/emailchangereq";

                URL url = new URL(accountActivation_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("newemail", NewEmail);
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
                });

                if (Status == 200){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        show_Notification("Cambio de email.", "Revise SMS e Email");
                    }
                    userLog.Logout();
                    sqlOnInit.UserSetIdOnInit();
                    requireActivity().runOnUiThread(() -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        show_Notification("Cambio de email", "Recibirás un código por SMS y un correo, favor de revisar tu bandeja de entrada o spam.");
                    }
                    });
                }


                conn.disconnect();
            } catch (IOException | JSONException e) {
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