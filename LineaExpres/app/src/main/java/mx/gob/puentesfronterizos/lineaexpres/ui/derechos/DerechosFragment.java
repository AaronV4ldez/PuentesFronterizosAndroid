package mx.gob.puentesfronterizos.lineaexpres.ui.derechos;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentDerechosBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;


public class DerechosFragment extends Fragment {

    private FragmentDerechosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DerechosViewModel privacyViewModel =
                new ViewModelProvider(this).get(DerechosViewModel.class);

        binding = FragmentDerechosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        updateData DownloadData = new updateData(requireContext()); //Open local db connection
        TextView layoutTitle = binding.Title;
        TextView layoutBody = binding.Body;

        ArrayList<String> WWA = DownloadData.getPrivacy();

        String title = WWA.get(0);
        String body = WWA.get(1);


        new Thread(() -> {

                String Servicios = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/1445?_embed";
                URL urlServicios;
            try {
                urlServicios = new URL(Servicios);
                URLConnection requestCurrentRates = urlServicios.openConnection();
                requestCurrentRates.connect();
                JsonObject jsonArray_CurrentRates = JsonParser.parseReader(new InputStreamReader((InputStream) requestCurrentRates.getContent())).getAsJsonObject();
                JsonObject titleContent_CurrentRates = jsonArray_CurrentRates.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_CurrentRates = jsonArray_CurrentRates.get("content").getAsJsonObject(); //Getting Body Content
                String titleArrayTitle_CurrentRates = titleContent_CurrentRates.get("rendered").getAsString(); // getting title
                String BodyText_CurrentRates = BodyContent_CurrentRates.get("rendered").getAsString(); // getting BodyText
                requireActivity().runOnUiThread( () -> {
                    layoutTitle.setText(titleArrayTitle_CurrentRates);
                    layoutBody.setText(Html.fromHtml(BodyText_CurrentRates));
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