package mx.gob.puentesfronterizos.lineaexpres.ui.contactanos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentContactanosBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
public class ContactanosFragment extends Fragment {
    updateData UpdateData;
    UserLog userLog;

    ArrayList<String> userData;
    String User;
    String Token;
    Button Send;
    private FragmentContactanosBinding binding;
    private String TAG = "Contactanos";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactanosViewModel callUsFragment =
                new ViewModelProvider(this).get(ContactanosViewModel.class);

        binding = FragmentContactanosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        UpdateData = new updateData(requireContext());
        userLog = new UserLog(requireActivity()); //Open local db connection
        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);

        EditText NombreInput = binding.NombresInput;
        EditText ApellidosInput = binding.ApellidosInput;
        EditText EmailInput = binding.EmailInput;
        EditText NumberInput = binding.NumberInput;
        EditText CommentInput = binding.ComentarioInput;
        Send = binding.sendFormBtn;

        Send.setOnClickListener(v -> {
            String Nombre = NombreInput.getText().toString();
            String Apellidos = ApellidosInput.getText().toString();
            String Email = EmailInput.getText().toString();
            String Number = NumberInput.getText().toString();
            String Comment = CommentInput.getText().toString();
            List<EditText> campos = Arrays.asList(NombreInput, ApellidosInput, EmailInput, NumberInput, CommentInput);

            int camposInvalidos = 0;
            boolean hayCamposVacios = false;

            for (int i = 0; i < campos.size(); i++) {
                if (campos.get(i).getText().toString().isEmpty()) {
                    campos.get(i).setBackgroundResource(R.drawable.box_rounded_wrong);
                    camposInvalidos++;
                    hayCamposVacios = true;
                } else {
                    campos.get(i).setBackgroundResource(R.drawable.box_rounded);
                }
            }

            if (hayCamposVacios) {
                camposInvalidos = Math.max(1, camposInvalidos);
            }
            System.out.println(camposInvalidos);
            if (camposInvalidos != 0) {
                Toast.makeText(requireContext(), "Debes llenar todos los campos.", Toast.LENGTH_SHORT).show();
            } else {
                sendContactForm(Nombre, Apellidos, Email, Number, Comment);
            }

        });

        return root;
    }


    public void sendContactForm(String Nombre, String Apellido, String email, String tel, String message){
        new Thread(() -> {
            try {

                InputStream inputStream;

                URL url = new URL(getResources().getString(R.string.apiURL) + "api/v1/contact");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("nombre", Nombre);
                jsonParam.put("apellido", Apellido);
                jsonParam.put("email", email);
                jsonParam.put("tel", tel);
                jsonParam.put("mensaje", message);


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
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Tuvimos problemas al recibir tu comentario, intentalo más tarde.", Toast.LENGTH_SHORT).show();
                    });
                }else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Hemos recibido tu comentario.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(requireContext(), "Nos contactaremos contigo lo más pronto posible.", Toast.LENGTH_SHORT).show();
                        MainActivity.nav_req(R.id.navigation_profile);
                    });

                }

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