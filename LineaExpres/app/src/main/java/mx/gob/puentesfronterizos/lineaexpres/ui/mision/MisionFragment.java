package mx.gob.puentesfronterizos.lineaexpres.ui.mision;

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
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentMisionBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MisionFragment extends Fragment {

    private FragmentMisionBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MisionViewModel misionViewModel =
                new ViewModelProvider(this).get(MisionViewModel.class);

        binding = FragmentMisionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView layoutTitle = binding.Title;
        TextView layoutBody = binding.Body;


        new Thread(() -> {
            String Mision = getResources().getString(R.string.noticiasURL) + "wp-json/wp/v2/pages/724?_embed";
            URL url_Mision;
            try {
                url_Mision = new URL(Mision);
                URLConnection requestMision = url_Mision.openConnection();
                requestMision.connect();
                JsonObject jsonArray_Mision = JsonParser.parseReader(new InputStreamReader((InputStream) requestMision.getContent())).getAsJsonObject();

                JsonObject titleContent_Mision = jsonArray_Mision.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_titleContent_Mision= jsonArray_Mision.get("content").getAsJsonObject(); //Getting Body Content
                String titleArrayContent_Mision = titleContent_Mision.get("rendered").getAsString(); // getting title
                String BodyText_Content_Mision = BodyContent_titleContent_Mision.get("rendered").getAsString(); // getting BodyText


                requireActivity().runOnUiThread(() -> {
                    layoutTitle.setText(titleArrayContent_Mision);
                    layoutBody.setText(Html.fromHtml(BodyText_Content_Mision));
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