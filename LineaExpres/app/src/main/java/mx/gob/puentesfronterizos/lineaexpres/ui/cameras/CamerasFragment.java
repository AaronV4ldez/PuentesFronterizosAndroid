package mx.gob.puentesfronterizos.lineaexpres.ui.cameras;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentCamerasBinding;
import mx.gob.puentesfronterizos.lineaexpres.ui.cameras.CamerasViewModel;


public class CamerasFragment extends Fragment {
    private static final String TAG = "CamerasFragment";
    private FragmentCamerasBinding binding;
    WebView ZaragozaNorteYT;
    WebView ZaragozaSurYT;

    WebView PasoDelNorteNorteYT;
    WebView PasoDelNorteSurYT;

    WebView LerdoNorteYT;
    WebView LerdoSurYT;
    WebView LerdoFilaYT;

    ArrayList<String> Links = new ArrayList<>();
    ArrayList<WebView> webViews = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CamerasViewModel CamerasViewModel = new ViewModelProvider(this).get(CamerasViewModel.class);
        binding = FragmentCamerasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ZaragozaNorteYT = binding.ZaragozaNorteYT;;
        ZaragozaSurYT = binding.ZaragozaSurYT;
        PasoDelNorteNorteYT = binding.PasoDelNorteNorteYT;
        PasoDelNorteSurYT = binding.PasoDelNorteSurYT;
        LerdoNorteYT = binding.LerdoNorteYT;
        LerdoSurYT = binding.LerdoSurYT;
        LerdoFilaYT = binding.LerdoFilaYT;

        webViews = new ArrayList<>();
        webViews.add(ZaragozaNorteYT);
        webViews.add(ZaragozaSurYT);
        webViews.add(PasoDelNorteNorteYT);
        webViews.add(PasoDelNorteSurYT);
        webViews.add(LerdoNorteYT);
        webViews.add(LerdoSurYT);
        webViews.add(LerdoFilaYT);



        getYoutubeEmbeded();

        return root;
    }

    private void getYoutubeEmbeded() {
        new Thread(() -> {
            String jsonURL = "https://lineaexpressapp.desarrollosenlanube.net/api/v1/config/mobile";
            URL url;
            try {
                url = new URL(jsonURL);
                URLConnection request = url.openConnection();
                request.connect();
                JsonObject jsonObj = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();
                JsonPrimitive mbPuenteLive1 = jsonObj.get("mbPuenteLive5").getAsJsonPrimitive();
                String mbPuenteLive1Link = mbPuenteLive1.toString();
                mbPuenteLive1Link = mbPuenteLive1Link.replace("\"", "");

                JsonPrimitive mbPuenteLive2 = jsonObj.get("mbPuenteLive4").getAsJsonPrimitive();
                String mbPuenteLive2Link = mbPuenteLive2.toString();
                mbPuenteLive2Link = mbPuenteLive2Link.replace("\"", "");

                JsonPrimitive mbPuenteLive3 = jsonObj.get("mbPuenteLive2").getAsJsonPrimitive();
                String mbPuenteLive3Link = mbPuenteLive3.toString();
                mbPuenteLive3Link = mbPuenteLive3Link.replace("\"", "");

                JsonPrimitive mbPuenteLive4 = jsonObj.get("mbPuenteLive1").getAsJsonPrimitive();
                String mbPuenteLive4Link = mbPuenteLive4.toString();
                mbPuenteLive4Link = mbPuenteLive4Link.replace("\"", "");

                JsonPrimitive mbPuenteLive5 = jsonObj.get("mbPuenteLive3").getAsJsonPrimitive();
                String mbPuenteLive5Link = mbPuenteLive5.toString();
                mbPuenteLive5Link = mbPuenteLive5Link.replace("\"", "");

                JsonPrimitive mbPuenteLive6 = jsonObj.get("mbPuenteLive6").getAsJsonPrimitive();
                String mbPuenteLive6Link = mbPuenteLive6.toString();
                mbPuenteLive6Link = mbPuenteLive6Link.replace("\"", "");

                JsonPrimitive mbPuenteLive7 = jsonObj.get("mbPuenteLive7").getAsJsonPrimitive();
                String mbPuenteLive7Link = mbPuenteLive7.toString();
                mbPuenteLive7Link = mbPuenteLive7Link.replace("\"", "");

                Links.add(mbPuenteLive1Link);
                Links.add(mbPuenteLive2Link);
                Links.add(mbPuenteLive3Link);
                Links.add(mbPuenteLive4Link);
                Links.add(mbPuenteLive5Link);
                Links.add(mbPuenteLive6Link);
                Links.add(mbPuenteLive7Link);

                for (int i = 0; i < webViews.size(); i++) {

                    setYoutubeWebViews(webViews.get(i), Links.get(i));

                    System.out.println("Links: " + Links.get(i));
                }

            } catch (IOException e) {
                Log.e("Youtube Embed", "setYoutubeEmbeded: ", e);
            }
        }).start();
    }

    public void setYoutubeWebViews(WebView WebView, String Links) {
        requireActivity().runOnUiThread(() -> {
            WebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
            WebSettings webSettings = WebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            try {
                WebView.loadUrl(Links);
            } catch (Exception e) {
                Log.e(TAG, "setCamera: ", e);
            }
        });

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
