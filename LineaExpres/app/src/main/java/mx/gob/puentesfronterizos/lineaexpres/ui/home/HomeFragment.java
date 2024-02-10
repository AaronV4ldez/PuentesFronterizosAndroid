package mx.gob.puentesfronterizos.lineaexpres.ui.home;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.SliderAdapter;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentHomeBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    ViewPager mainPager;
    SliderAdapter sliderAdapter;
    Timer timer;
    int main_currentPage = 0;
    long DELAY_MS = 500;
    long PERIOD_MS = 3000;

    ArrayList<String> lastNotes;

    ImageView image_weather;
    TextView actual_weather;
    TextView max_weather;
    TextView min_weather;
    TextView feelslike_weather;
    TextView windspeednumber_weather;
    TextView humiditynumber_weather;
    TextView time_weather;

    updateData UpdateData;
    SQLOnInit sqlOnInit;
    UserLog userLog;

    Button OpenCameras;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        UpdateData = new updateData(requireContext());
        sqlOnInit = new SQLOnInit(requireContext());
        userLog = new UserLog(requireContext());

        setHasOptionsMenu(true);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed" + task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println("MainAct Token: " + token);
                        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


                        userLog.UserFireToken("set", token);



                    }
                });

        mainPager = binding.sliderViewPager;
        sliderAdapter = new SliderAdapter(getContext());
        mainPager.setAdapter(sliderAdapter);

        if (main_currentPage == 0) {
            reloadMainSlider();
        }

        image_weather = binding.imageWeather;
        actual_weather = binding.actualWeather;
        max_weather = binding.maxWeather;
        min_weather = binding.minWeather;
        feelslike_weather = binding.feelslikeWeather;
        windspeednumber_weather = binding.windspeednumberWeather;
        humiditynumber_weather = binding.humiditynumberWeather;
        time_weather = binding.timeWeather;

        OpenCameras = binding.goToCameras;

        ConstraintLayout Nota1 = binding.Nota1Body;
        TextView Nota_title1 = binding.Nota1Title;
        ImageView Nota_Image1 = binding.Nota1Image;

        ConstraintLayout Nota2 = binding.Nota2Body;
        TextView Nota_title2 = binding.Nota2Title;
        ImageView Nota_Image2 = binding.Nota2Image;

        ConstraintLayout Nota3 = binding.Nota3Body;
        TextView Nota_title3 = binding.Nota3Title;
        ImageView Nota_Image3 = binding.Nota3Image;

        ConstraintLayout Nota4 = binding.Nota4Body;
        TextView Nota_title4 = binding.Nota4Title;
        ImageView Nota_Image4 = binding.Nota4Image;

        updateData openUpdateDB = new updateData(getActivity());
        lastNotes = openUpdateDB.getLastNotes();
        for (int i = 0; i < lastNotes.size() ; i++) {
            String[] splitArray = lastNotes.get(i).split("∑");
            String idLastNotes = splitArray[0];
            String TituloLastNotes = splitArray[1];
            String ImageLastNotes = splitArray[2];


            if (i == 0) {
                Nota_title1.setText(TituloLastNotes);
                Glide.with(this)
                        .load(ImageLastNotes)
                        .into(Nota_Image1);
                Nota1.setTag(idLastNotes);
            }
            if (i == 1) {
                Nota_title2.setText(TituloLastNotes);
                Glide.with(this)
                        .load(ImageLastNotes)
                        .into(Nota_Image2);
                Nota2.setTag(idLastNotes);
            }
            if (i == 2) {
                Nota_title3.setText(TituloLastNotes);
                Glide.with(this)
                        .load(ImageLastNotes)
                        .into(Nota_Image3);
                Nota3.setTag(idLastNotes);
            }
            if (i == 3) {
                Nota_title4.setText(TituloLastNotes);
                Glide.with(this)
                        .load(ImageLastNotes)
                        .into(Nota_Image4);
                Nota4.setTag(idLastNotes);
            }

        }

        Nota1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = 0;
                try {
                    id = Integer.parseInt((String) Nota1.getTag());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                sendNotasData(id);
            }
        });
        Nota2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = 0;
                try {
                    id = Integer.parseInt((String) Nota1.getTag());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                sendNotasData(id);
            }
        });
        Nota3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = 0;
                try {
                    id = Integer.parseInt((String) Nota1.getTag());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                sendNotasData(id);
            }
        });
        Nota4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = 0;
                try {
                    id = Integer.parseInt((String) Nota1.getTag());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                sendNotasData(id);
            }
        });
        OpenCameras.setOnClickListener(v -> {
           MainActivity.nav_req(R.id.navigation_cameras);
        });



        setYoutubeEmbeded();
        setMainAds();
        getWeatherInfo();


        return root;
    }

    public void getWeatherInfo() {
        new Thread(() -> {

            URL url;
            try {
                url = new URL(getResources().getString(R.string.apiURL) + "api/v1/clima");
                URLConnection requestWeather = url.openConnection();
                requestWeather.connect();
                JsonArray jsonArray = JsonParser.parseReader(new InputStreamReader((InputStream) requestWeather.getContent())).getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject(); //Converting Json Array to JsonObjects
                    String CityName = jsonObject.get("cityName").getAsString(); // getting CityName
                    String ImageIcon = jsonObject.get("iconBigURL").getAsString(); // getting ImageURL

                    String TempCString = jsonObject.get("tempC").getAsString(); // getting ActualTempCString
                    String TempC = Integer.toString((int) Math.round(Double.parseDouble(TempCString)));

                    String tempMaxCString = jsonObject.get("tempMaxC").getAsString(); // getting MaxTempCString
                    String tempMaxC = Integer.toString((int) Math.round(Double.parseDouble(tempMaxCString)));

                    String tempMinCString = jsonObject.get("tempMinC").getAsString(); // getting MinTempCString
                    String tempMinC = Integer.toString((int) Math.round(Double.parseDouble(tempMinCString)));

                    String FeelsLikeString = jsonObject.get("feelsLikeC").getAsString(); // getting FeelsTempCString
                    String FeelsLike = Integer.toString((int) Math.round(Double.parseDouble(FeelsLikeString)));

                    String WindSpeedString = jsonObject.get("windSpeed").getAsString(); // getting WindSpeedString
                    String WindSpeed = Integer.toString((int) Math.round(Double.parseDouble(WindSpeedString)));

                    String Humidity = jsonObject.get("humidity").getAsString(); // getting HumidityString
                    String Date = jsonObject.get("dt").getAsString(); // getting DateString

                    String[] DateParts = Date.split(" ");
                    String Hour = DateParts[DateParts.length - 1];
                    String[] HourParts = Hour.split(":");
                    String HourShort = HourParts[0] + ":" + HourParts[1];


                    if (CityName.equals("Juarez")){
                        requireActivity().runOnUiThread(() -> {

                            Glide.with(requireContext())
                                    .load(ImageIcon)
                                    .into(image_weather);
                            actual_weather.setText(TempC + "°");
                            max_weather.setText(tempMaxC + "°");
                            min_weather.setText(tempMinC + "°");
                            feelslike_weather.setText(Html.fromHtml("<b style=\"color:#e3e3e3;\">SENSACIÓN </b><b>"+FeelsLike+"°</b>" ));
                            windspeednumber_weather.setText(WindSpeed + " E");
                            humiditynumber_weather.setText(Humidity + "%");
                            time_weather.setText("Desde las: " + HourShort);

                        });
                        return;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void sendNotasData(int id) {
        updateData openUpdateDB = new updateData(getActivity());
        openUpdateDB.NoteOnClick(id);
        openUpdateDB.PrevNoteOnClick(id);
        openUpdateDB.NextNoteOnClick(id);

        MainActivity.nav_req(R.id.navigation_notas);
    }

    private void setYoutubeEmbeded() {
        WebView youtubeWebView = binding.youtubeEmbed;

        youtubeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        new Thread(() -> {
            String jsonURL = getResources().getString(R.string.apiURL) + "api/v1/config/mobile";
            URL url;
            try {
                url = new URL(jsonURL);
                URLConnection request = url.openConnection();
                request.connect();
                JsonObject jsonObj = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();
                JsonPrimitive youtubeLink = jsonObj.get("mbVideoURL").getAsJsonPrimitive();
                String ytLink = youtubeLink.toString();
                ytLink = ytLink.replace("\"", "");
                String finalYtLink = ytLink;

                requireActivity().runOnUiThread(() -> {
                    System.out.println(finalYtLink);
                    youtubeWebView.loadUrl(finalYtLink);
                });

            } catch (IOException e) {
                Log.e("Youtube Embed", "setYoutubeEmbeded: ", e);
            }
        }).start();
    }


    private void setMainAds() {
        final ImageView imageAd;
        imageAd = (ImageView) binding.adCarousel;

        new Thread(() -> {
            String jsonURL = getResources().getString(R.string.apiURL) + "api/v1/config/mobile";
            URL url;
            try {

                url = new URL(jsonURL);
                URLConnection request = url.openConnection();
                request.connect();
                JsonObject jsonObj = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();
                JsonPrimitive youtubeLink = jsonObj.get("mbPortadaURL").getAsJsonPrimitive();
                String ytLink = youtubeLink.toString();

                requireActivity().runOnUiThread(() -> {
                    String urlConComillas = ytLink;
                    String urlSinComillas = urlConComillas.substring(1, urlConComillas.length() - 1);

                    Glide.with(requireContext())
                            .load(urlSinComillas)
                            .into(imageAd);

                });

            } catch (IOException e) {
                Log.e("Youtube Embed", "setYoutubeEmbeded: ", e);
            }
        }).start();
    }

    //private void setMainAds() {
    //    try {
    //        final WebView webView;
    //        webView = (WebView)binding.adCarousel;
    //        webView.setWebChromeClient(new WebChromeClient());
    //        webView.setWebViewClient(new WebViewClient());
    //        webView.getSettings().setJavaScriptEnabled(true);
    //        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//
//
    //        System.out.println("Debería entrar aqui y mostrar error si hace falta algo");
//
    //        String Script = "<script type='text/javascript'><!--//<![CDATA[\n" +
    //                "   var m3_u = (location.protocol=='https:'?'https://ads.desarrollosenlanube.com/www/delivery/ajs.php':'http://ads.desarrollosenlanube.com/www/delivery/ajs.php');\n" +
    //                "   var m3_r = Math.floor(Math.random()*99999999999);\n" +
    //                "   if (!document.MAX_used) document.MAX_used = ',';\n" +
    //                "   document.write (\"<scr\"+\"ipt type='text/javascript' src='\"+m3_u);\n" +
    //                "   document.write (\"?zoneid=44\");\n" +
    //                "   document.write ('&amp;cb=' + m3_r);\n" +
    //                "   if (document.MAX_used != ',') document.write (\"&amp;exclude=\" + document.MAX_used);\n" +
    //                "   document.write (document.charset ? '&amp;charset='+document.charset : (document.characterSet ? '&amp;charset='+document.characterSet : ''));\n" +
    //                "   document.write (\"&amp;loc=\" + escape(window.location));\n" +
    //                "   if (document.referrer) document.write (\"&amp;referer=\" + escape(document.referrer));\n" +
    //                "   if (document.context) document.write (\"&context=\" + escape(document.context));\n" +
    //                "   if (document.mmm_fo) document.write (\"&amp;mmm_fo=1\");\n" +
    //                "   document.write (\"'><\\/scr\"+\"ipt>\");\n" +
    //                "//]]>--></script><noscript><a href='https://ads.desarrollosenlanube.com/www/delivery/ck.php?n=a42f8709&amp;cb=INSERT_RANDOM_NUMBER_HERE' target='_blank'><img src='https://ads.desarrollosenlanube.com/www/delivery/avw.php?zoneid=44&amp;cb=INSERT_RANDOM_NUMBER_HERE&amp;n=a42f8709' border='0' alt='' /></a></noscript>";
//
//
    //        String iFrame = "<html><body style='margin: 0; padding: 0;'> "+Script+" </body></html>";
//
    //        webView.loadDataWithBaseURL(null, iFrame, "text/html", "UTF-8", null);
    //    }catch (Exception e) {
    //        System.out.println("Aqui debería estar el error");
    //        throw e;
    //    }
//
    //}

    public void reloadMainSlider() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (main_currentPage == 4) {
                    main_currentPage = 0;
                }
                mainPager.setCurrentItem(main_currentPage++, true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}