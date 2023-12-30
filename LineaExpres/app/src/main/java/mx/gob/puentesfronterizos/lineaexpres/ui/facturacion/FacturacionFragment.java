package mx.gob.puentesfronterizos.lineaexpres.ui.facturacion;


import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.os.Bundle;
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

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentFacturacionBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class FacturacionFragment extends Fragment {
    UserLog userLog;
    ArrayList<String> userData;
    String User;
    String Token;
    updateData UpdateData;
    String localRazonSocial = "";
    String localrfc = "";
    String localDomFiscal = "";
    String localCodigoPostal = "";
    String localEmail = "";
    String localTelefono = "";

    private FragmentFacturacionBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FacturacionViewModel callUsFragment =
                new ViewModelProvider(this).get(FacturacionViewModel.class);

        binding = FragmentFacturacionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        UpdateData = new updateData(requireActivity()); //Open local db connection
        userLog = new UserLog(requireContext()); //Open Users db
        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);
        localRazonSocial = userData.get(6);
        localrfc = userData.get(7);
        localDomFiscal = userData.get(8);
        localCodigoPostal = userData.get(9);
        localEmail = userData.get(10);
        localTelefono = userData.get(11);

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
            root = null;
        }


        EditText razonSocial = binding.razonSocial;
        EditText rfc = binding.rfc;
        EditText DomFiscal = binding.DomFiscal;
        EditText CodigoPostal = binding.CodigoPostal;
        EditText Email = binding.Email;
        EditText Telefono = binding.Telefono;

        razonSocial.setText(localRazonSocial);
        rfc.setText(localrfc);
        DomFiscal.setText(localDomFiscal);
        CodigoPostal.setText(localCodigoPostal);
        Email.setText(localEmail);
        Telefono.setText(localTelefono);

        Button sendFormBtn = binding.sendFormBtn;

        sendFormBtn.setOnClickListener(view ->  {
            sendBillingData(razonSocial.getText().toString(), rfc.getText().toString(), DomFiscal.getText().toString(), CodigoPostal.getText().toString(), Email.getText().toString(), Telefono.getText().toString());
        });

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void sendBillingData(String fac_razon_social, String fac_rfc, String fac_dom_fiscal, String fac_cp, String fac_email, String fac_telefono) {
        new Thread(() -> {
            try {

                InputStream inputStream;
                String url_process = "https://apis.fpfch.gob.mx/api/v1/user/fac";

                URL url = new URL(url_process);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Token);
                conn.setRequestMethod("POST");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("fac_razon_social", fac_razon_social);
                jsonParam.put("fac_rfc", fac_rfc);
                jsonParam.put("fac_dom_fiscal", fac_dom_fiscal);
                jsonParam.put("fac_email", fac_email);
                jsonParam.put("fac_telefono", fac_telefono);
                jsonParam.put("fac_cp", fac_cp);

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
                JSONObject Result = new JSONObject(ResponseData);
                System.out.println("Este es el response Data de sentri add  " + Result);
                String Message = Result.getString("message");

                if (Status == 200) {
                    requireActivity().runOnUiThread(() -> {
                        userLog.setBillingData(fac_razon_social, fac_rfc, fac_dom_fiscal, fac_cp, fac_email, fac_telefono);
                        Toast.makeText(requireContext(), Message, Toast.LENGTH_SHORT).show();
                    });
                }



                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }
}