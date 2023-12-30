package mx.gob.puentesfronterizos.lineaexpres.ui.lineamientos;

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

import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentLineamientosBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class LineamientosFragment extends Fragment {

    private FragmentLineamientosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LineamientosViewModel whoWeAreViewModel =
                new ViewModelProvider(this).get(LineamientosViewModel.class);

        binding = FragmentLineamientosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView layoutTitle = binding.Title;
        TextView layoutBody = binding.Body;

        new Thread(() -> {
            String Lineamientos = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/1305?_embed";
            URL urlLineamientos;
            try {
                urlLineamientos = new URL(Lineamientos);
                URLConnection requestLineamientos = urlLineamientos.openConnection();
                requestLineamientos.connect();
                JsonObject jsonArray_Lineamientos = JsonParser.parseReader(new InputStreamReader((InputStream) requestLineamientos.getContent())).getAsJsonObject();
                //Getting title
                JsonObject titleContent = jsonArray_Lineamientos.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                String titleArrayTitle = titleContent.get("rendered").getAsString(); // getting title
                //Getting Body
                JsonObject BodyContent = jsonArray_Lineamientos.get("content").getAsJsonObject(); //Getting Body Content
                String BodyText = BodyContent.get("rendered").getAsString(); // getting BodyText


                requireActivity().runOnUiThread(() -> {
                    layoutTitle.setText(titleArrayTitle);
                    layoutBody.setText(Html.fromHtml(BodyText));
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