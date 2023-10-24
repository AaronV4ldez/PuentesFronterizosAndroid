package mx.gob.puentesfronterizos.lineaexpres.ui.rates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentRatesBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class RatesFragment extends Fragment {

    private FragmentRatesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RatesViewModel ratesViewModel =
                new ViewModelProvider(this).get(RatesViewModel.class);

        binding = FragmentRatesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView layoutTitle = binding.Title;
        WebView layoutBody = binding.Body;

        new Thread(() -> {
            String CurrentRates = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/1119?_embed";
            URL urlCurrentRates;
            try {
                urlCurrentRates = new URL(CurrentRates);
                URLConnection requestCurrentRates = urlCurrentRates.openConnection();
                requestCurrentRates.connect();
                JsonObject jsonArray_CurrentRates = JsonParser.parseReader(new InputStreamReader((InputStream) requestCurrentRates.getContent())).getAsJsonObject();
                JsonObject titleContent_CurrentRates = jsonArray_CurrentRates.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_CurrentRates = jsonArray_CurrentRates.get("content").getAsJsonObject(); //Getting Body Content
                String titleArrayTitle_CurrentRates = titleContent_CurrentRates.get("rendered").getAsString(); // getting title
                String BodyText_CurrentRates = BodyContent_CurrentRates.get("rendered").getAsString(); // getting BodyText
                requireActivity().runOnUiThread(() -> {
                    layoutTitle.setText(titleArrayTitle_CurrentRates);
                    String iFrame = "<html><body style='margin: 1px; padding: 1px; width:99vw; heigth: 100%;'> "+BodyText_CurrentRates+" <style>img {max-width: 100%; height:auto; max-height:470px;}</style> </body></html>";

                    layoutBody.loadDataWithBaseURL(null, iFrame, "text/html", "UTF-8", null);

                });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}