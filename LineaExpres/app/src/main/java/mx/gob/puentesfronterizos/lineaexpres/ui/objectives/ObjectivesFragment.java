package mx.gob.puentesfronterizos.lineaexpres.ui.objectives;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentObjectivesBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ObjectivesFragment extends Fragment {

    private FragmentObjectivesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ObjectivesViewModel newsViewModel =
                new ViewModelProvider(this).get(ObjectivesViewModel.class);

        binding = FragmentObjectivesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView layoutTitle = binding.Title;
        TextView layoutBody = binding.Body;




        new Thread(() -> {
            String Objetivo = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/662?_embed";
            URL url_Objetivo;
            try {
                url_Objetivo = new URL(Objetivo);
                URLConnection requestObjetivo = url_Objetivo.openConnection();
                requestObjetivo.connect();
                JsonObject jsonArray_Objetivo = JsonParser.parseReader(new InputStreamReader((InputStream) requestObjetivo.getContent())).getAsJsonObject();

                JsonObject titleContent_Objetivo = jsonArray_Objetivo.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_titleContent_Objetivo = jsonArray_Objetivo.get("content").getAsJsonObject(); //Getting Body Content

                String titleArrayContent_Objetivo = titleContent_Objetivo.get("rendered").getAsString(); // getting title
                String BodyText_Content_Objetivo = BodyContent_titleContent_Objetivo.get("rendered").getAsString(); // getting BodyText


                requireActivity().runOnUiThread(() -> {
                    layoutTitle.setText(titleArrayContent_Objetivo);
                    layoutBody.setText(Html.fromHtml(BodyText_Content_Objetivo));
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