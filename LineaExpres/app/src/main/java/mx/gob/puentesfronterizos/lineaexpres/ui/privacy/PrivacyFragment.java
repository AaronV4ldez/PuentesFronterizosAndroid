package mx.gob.puentesfronterizos.lineaexpres.ui.privacy;

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

import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentPrivacyBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class PrivacyFragment extends Fragment {

    private FragmentPrivacyBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PrivacyViewModel privacyViewModel =
                new ViewModelProvider(this).get(PrivacyViewModel.class);

        binding = FragmentPrivacyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView layoutTitle = binding.Title;
        TextView layoutBody = binding.Body;

        new Thread(() -> {
            String Privacy = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/3?_embed";
            URL urlPrivacy;
            try {
                urlPrivacy = new URL(Privacy);
                URLConnection requestPrivacy = urlPrivacy.openConnection();
                requestPrivacy.connect();
                JsonObject jsonArray_Privacy = JsonParser.parseReader(new InputStreamReader((InputStream) requestPrivacy.getContent())).getAsJsonObject();

                JsonObject titleContent_Privacy = jsonArray_Privacy.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_Privacy = jsonArray_Privacy.get("content").getAsJsonObject(); //Getting Body Content

                String titleArrayTitle_Privacy = titleContent_Privacy.get("rendered").getAsString(); // getting title
                String BodyText_Privacy = BodyContent_Privacy.get("rendered").getAsString(); // getting BodyText

                requireActivity().runOnUiThread(() -> {
                    layoutTitle.setText(titleArrayTitle_Privacy);
                    layoutBody.setText(Html.fromHtml(BodyText_Privacy));
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