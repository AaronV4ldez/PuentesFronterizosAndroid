package mx.gob.puentesfronterizos.lineaexpres.ui.whoweare;

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

import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentWhoWeAreBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WhoWeAreFragment extends Fragment {

    private FragmentWhoWeAreBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WhoWeAreViewModel whoWeAreViewModel =
                new ViewModelProvider(this).get(WhoWeAreViewModel.class);

        binding = FragmentWhoWeAreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView layoutTitle = binding.Title;
        TextView layoutBody = binding.Body;

        new Thread(() -> {
            //posible error
            String QuienesSomos = "https://lineaexpress.desarrollosenlanube.net/wp-json/wp/v2/pages/647?_embed";
            URL urlQuienesSomos;
            try {
                urlQuienesSomos = new URL(QuienesSomos);
                URLConnection requestQuienesSomos = urlQuienesSomos.openConnection();
                requestQuienesSomos.connect();
                JsonObject jsonArray_QuienesSomos = JsonParser.parseReader(new InputStreamReader((InputStream) requestQuienesSomos.getContent())).getAsJsonObject();

                JsonObject titleContent_QuienesSomos = jsonArray_QuienesSomos.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_QuienesSomos = jsonArray_QuienesSomos.get("content").getAsJsonObject(); //Getting Body Content

                String titleArrayTitle_QuienesSomos = titleContent_QuienesSomos.get("rendered").getAsString(); // getting title
                String BodyText_QuienesSomos = BodyContent_QuienesSomos.get("rendered").getAsString(); // getting BodyText

                requireActivity().runOnUiThread(() -> {
                layoutTitle.setText(titleArrayTitle_QuienesSomos);
                layoutBody.setText(Html.fromHtml(BodyText_QuienesSomos));
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