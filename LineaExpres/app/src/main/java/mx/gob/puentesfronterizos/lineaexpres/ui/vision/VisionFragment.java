package mx.gob.puentesfronterizos.lineaexpres.ui.vision;

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

import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentVisionBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class VisionFragment extends Fragment {

    private FragmentVisionBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        VisionViewModel newsViewModel =
                new ViewModelProvider(this).get(VisionViewModel.class);

        binding = FragmentVisionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView layoutTitle = binding.Title;
        TextView layoutBody = binding.Body;

        new Thread(() -> {
            String Vision = getResources().getString(R.string.noticiasURL) + "wp-json/wp/v2/pages/730?_embed";
            URL url_Vision;
            try {
                url_Vision = new URL(Vision);
                URLConnection requestVision = url_Vision.openConnection();
                requestVision.connect();
                JsonObject jsonArray_Vision = JsonParser.parseReader(new InputStreamReader((InputStream) requestVision.getContent())).getAsJsonObject();

                JsonObject titleContent_Vision = jsonArray_Vision.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_titleContent_Vision= jsonArray_Vision.get("content").getAsJsonObject(); //Getting Body Content
                String titleArrayContent_Vision = titleContent_Vision.get("rendered").getAsString(); // getting title
                String BodyText_Content_Vision = BodyContent_titleContent_Vision.get("rendered").getAsString(); // getting BodyText


                requireActivity().runOnUiThread(() -> {
                    layoutTitle.setText(titleArrayContent_Vision);
                    layoutBody.setText(Html.fromHtml(BodyText_Content_Vision));
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