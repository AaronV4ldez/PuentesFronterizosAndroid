package mx.gob.puentesfronterizos.lineaexpres.ui.banortest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentBanortestBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class BanortestFragment extends Fragment {

    private static final String PREFS_NAME = "BanortestPrefs";
    private static final String PREFS_KEY_LAST_PAY_TIME = "lastPayTime";
    private static final long DISABLE_DURATION = 15 * 60 * 1000;

    WebView RespWebView;
    updateData UpdateData;
    UserLog userLog;
    ArrayList<String> userData;
    LinearLayout Container;
    String num_tag;
    String MontoSeleccionado;
    String User;
    String Token;
    String ctl_contract_type = "";
    String TIPOP = "";
    private FragmentBanortestBinding binding;
    private Handler handler = new Handler();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BanortestViewModel BanortestFragment = new ViewModelProvider(this).get(BanortestViewModel.class);

        binding = FragmentBanortestBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        UpdateData = new updateData(requireActivity()); // Open local db connection
        userLog = new UserLog(requireContext()); // Open Users db

        Container = binding.container;

        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);

        RespWebView = new WebView(requireContext());

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
        for (String car : CarList) {
            String[] splitArray = car.split("∑");
            if (splitArray[8].equals(CarSelected)) {
                String tipoVeh = splitArray[0];
                num_tag = splitArray[3];
                ctl_contract_type = splitArray[5];

                if (ctl_contract_type.contains("null")) {
                    TIPOP = tipoVeh.contains("2") ? "5" : "1";
                } else if (ctl_contract_type.contains("C")) {
                    TIPOP = "2";
                } else if (ctl_contract_type.contains("V")) {
                    TIPOP = "3";
                } else if (ctl_contract_type.contains("M")) {
                    TIPOP = "4";
                }
            }
        }

        MontoSeleccionado = UpdateData.getCantidadRecargar();

        ViewGroup.LayoutParams params = Container.getLayoutParams();
        RespWebView.setLayoutParams(params);
        Container.addView(RespWebView);

        requireActivity().runOnUiThread(() -> {

            RespWebView.loadUrl(getResources().getString(R.string.apiURL) + "pagosmovil/#/" + num_tag + "/" + MontoSeleccionado + "/" + TIPOP);


            RespWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    long currentTime = System.currentTimeMillis();
                    SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    long lastPayTime = prefs.getLong(PREFS_KEY_LAST_PAY_TIME, 0);

                    if (currentTime - lastPayTime < DISABLE_DURATION) {
                        disablePayButton(currentTime - lastPayTime);
                    }

                    RespWebView.evaluateJavascript(getJavascriptToInject(), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            // No need to handle the result here
                        }
                    });

                    setupPayButtonListener();
                }
            });
        });

        return root;
    }

    private String getJavascriptToInject() {
        int waitSec = 300;
        return "setTimeout(function(){document.getElementById('NUM_TAG').disabled = true; }, " + waitSec + ");" +
                "setTimeout(function(){document.getElementById('banorte-logo').style.maxWidth = '100%'; }, " + waitSec + ");" +
                "setTimeout(function(){document.getElementById('MONTO').disabled = true; }, " + waitSec + ");" +
                "setTimeout(function(){document.body.style.backgroundColor = 'white';document.body.style.borderRadius = '10px'; }, " + waitSec + ");" +
                "setTimeout(function(){document.getElementById('pagar').style.marginBottom = '15px'; }, 1000);" +
                "setTimeout(function(){document.getElementById('pagar').style.width = '100%'; }, " + waitSec + ");" +
                "setTimeout(function(){document.getElementById('NUMERO_TARJETA').type = 'number'; document.getElementById('NUMERO_TARJETA').type = 'number'; document.getElementById('CVC').type = 'number'; " +
                "document.getElementById('CODIGO_POSTAL').type = 'number'; document.getElementById('NUMERO_CELULAR').type = 'number'; }, " + waitSec + ");";
    }

    private void disablePayButton(long remainingTime) {
        handler.postDelayed(() -> {
            RespWebView.evaluateJavascript("document.getElementById('pagar').disabled = false;", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    // Button re-enabled
                }
            });
        }, remainingTime);

        RespWebView.evaluateJavascript("document.getElementById('pagar').disabled = true;", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                // Button disabled
            }
        });
    }

    private void handlePayButtonClick() {
        long currentTime = System.currentTimeMillis();
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(PREFS_KEY_LAST_PAY_TIME, currentTime).apply();

        disablePayButton(DISABLE_DURATION);
    }

    private void setupPayButtonListener() {
        RespWebView.addJavascriptInterface(new WebAppInterface(), "Android");

        RespWebView.evaluateJavascript("document.getElementById('pagar').addEventListener('click', function() {" +
                "    window.Android.handlePayButtonClick();" +
                "});", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                // No need to handle the result here
            }
        });
    }

    private class WebAppInterface {
        @JavascriptInterface
        public void handlePayButtonClick() {
            BanortestFragment.this.handlePayButtonClick();
        }
    }
}