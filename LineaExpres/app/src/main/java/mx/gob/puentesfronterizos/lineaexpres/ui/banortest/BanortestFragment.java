package mx.gob.puentesfronterizos.lineaexpres.ui.banortest;


import static mx.gob.puentesfronterizos.lineaexpres.ui.login.LoginFragment.convertStreamToString;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentBanortestBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BanortestFragment extends Fragment {

    EditText Tag;
    String num_tag;
    WebView RespWebView;
    String MontoSeleccionado;
    updateData UpdateData;
    UserLog userLog;
    ArrayList<String> userData;
    LinearLayout Container;
    String User;
    String Token;
    String ctl_contract_type = "";
    String TIPOP = "";
    private FragmentBanortestBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BanortestViewModel BanortestFragment =
                new ViewModelProvider(this).get(BanortestViewModel.class);

        binding = FragmentBanortestBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        UpdateData = new updateData(requireActivity()); //Open local db connection
        userLog = new UserLog(requireContext()); //Open Users db

        Container = binding.container;


        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);


        RespWebView = new WebView(requireContext()); //Este si lo utilizaré ------------

        WebSettings settings = RespWebView.getSettings();
        RespWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager cookieManager = CookieManager.getInstance();
        CookieManager.getInstance().setAcceptThirdPartyCookies(RespWebView, true);
        cookieManager.setAcceptCookie(true);




        String CarSelected = UpdateData.getCarSelected();
        ArrayList<String> CarList = UpdateData.getProfileVehicles();
        for (int i = 0; i < CarList.size(); i++) {
            String[] splitArray = CarList.get(i).split("∑");
            System.out.println("Este debe ser el array: " + Arrays.toString(splitArray));
            if (splitArray[8].equals(CarSelected)) {
                String tipoVeh = splitArray[0];
                num_tag = splitArray[3];
                ctl_contract_type = splitArray[5];

                System.out.println("Esto para lo ctl_ " + ctl_contract_type);

                if (ctl_contract_type.contains("null")) {
                    if (tipoVeh.contains("2")) {
                        TIPOP = "5";
                    }else {
                        TIPOP = "1";
                    }

                }
                if (ctl_contract_type.contains("C")) {
                    TIPOP = "2";
                }
                if (ctl_contract_type.contains("V")) {
                    TIPOP = "3";
                }
                if (ctl_contract_type.contains("M")) {
                    TIPOP = "4";
                }
            }

        }

        MontoSeleccionado = UpdateData.getCantidadRecargar();

        System.out.println("TAG seleccionado: " + num_tag);
        System.out.println("Monto seleccionado: " + MontoSeleccionado);


        ViewGroup.LayoutParams params = Container.getLayoutParams();

        RespWebView.setLayoutParams(params);

        Container.addView(RespWebView);

        requireActivity().runOnUiThread(() -> {

          //String Nombre = "";
          //String Nombre = "";
          //String Nombre = "";
          //String Nombre = "";
          //String Nombre = "";
          //String Nombre = "";

            RespWebView.loadUrl("https://lineaexpressapp.desarrollosenlanube.net/pagosmovil/#/"+num_tag+"/"+MontoSeleccionado+"/"+TIPOP);

            RespWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    int waitSec = 300;

                    String firstScript = "setTimeout(function(){document.getElementById('NUM_TAG').disabled = true; }, "+waitSec+");";
                    String secondScript = "setTimeout(function(){document.getElementById('banorte-logo').style.maxWidth = '100%'; }, "+waitSec+");";
                    //String thirdScript = "setTimeout(function(){document.getElementById('MONTO').value = '" + MontoSeleccionado + "'; }, "+waitSec+");";
                    String fourScript = "setTimeout(function(){document.getElementById('MONTO').disabled = true; }, "+waitSec+");";
                    String fiftyScript = "setTimeout(function(){document.body.style.backgroundColor = 'white';document.body.style.borderRadius = '10px'; }, "+waitSec+");";
                    String sixtyScript = "setTimeout(function(){document.getElementById('pagar').style.marginBottom = '15px'; }, 1000);";
                    String seventyScript = "setTimeout(function(){document.getElementById('pagar').style.width = '100%'; }, "+waitSec+");";
                    String Script = "setTimeout(function(){document.getElementById('NUMERO_TARJETA').type = 'number'; document.getElementById('NUMERO_TARJETA').type = 'number'; document.getElementById('CVC').type = 'number'; " +
                            "document.getElementById('CODIGO_POSTAL').type = 'number';" +
                            "document.getElementById('NUMERO_CELULAR').type = 'number';" +
                            "}, "+waitSec+");";


                    RespWebView.evaluateJavascript(firstScript + secondScript + fourScript + fiftyScript + sixtyScript + seventyScript + Script, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            System.out.println("Resultado de js: " + value);
                        }
                    });
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    String url = request.getUrl().toString();
                    Log.d("WebView", "URL entrante de shouldInterceptRequest: " + url);
                    return super.shouldInterceptRequest(view, request);
                }
            });
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
