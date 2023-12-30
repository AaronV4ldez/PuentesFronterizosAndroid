package mx.gob.puentesfronterizos.lineaexpres.ui.termsandconditions;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentTermsandconditionsBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;
import mx.gob.puentesfronterizos.lineaexpres.ui.termsandconditions.TermsandconditionsViewModel;

public class TermsandconditionsFragment extends Fragment {
    private FragmentTermsandconditionsBinding binding;
    UserLog userLog;

    WebView webView;
    TextView layoutTitle;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TermsandconditionsViewModel FormalitiesViewModel = new ViewModelProvider(this).get(TermsandconditionsViewModel.class);
        binding = FragmentTermsandconditionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userLog = new UserLog(requireContext());

        webView = binding.Body;
        layoutTitle = binding.Title;

        new Thread(() -> {
            String TerminosYCondiciones = "https://noticias.fpfch.gob.mx/wp-json/wp/v2/pages/1309?_embed";
            URL urlTerminosYCondiciones;
            try {
                urlTerminosYCondiciones = new URL(TerminosYCondiciones);
                URLConnection requestTerminosYCondiciones = urlTerminosYCondiciones.openConnection();
                requestTerminosYCondiciones.connect();
                JsonObject jsonTerminosYCondiciones = JsonParser.parseReader(new InputStreamReader((InputStream) requestTerminosYCondiciones.getContent())).getAsJsonObject();

                JsonObject titleContent_TerminosYCondiciones = jsonTerminosYCondiciones.get("title").getAsJsonObject(); //Converting Json Array to JsonObjects
                JsonObject BodyContent_TerminosYCondiciones = jsonTerminosYCondiciones.get("content").getAsJsonObject(); //Getting Body Content

                String titleArrayTitle_TerminosYCondiciones = titleContent_TerminosYCondiciones.get("rendered").getAsString(); // getting title
                String BodyText_TerminosYCondiciones = BodyContent_TerminosYCondiciones.get("rendered").getAsString(); // getting BodyText

                requireActivity().runOnUiThread(() -> {
                    layoutTitle.setText(titleArrayTitle_TerminosYCondiciones);
                    String iFrame = "<html><body style='margin: 1px; padding: 1px; width:99vw; heigth: 100%;'> "+BodyText_TerminosYCondiciones+" <style>img {max-width: 100%; height:auto; max-height:470px;}</style> </body></html>";

                    webView.loadDataWithBaseURL(null, iFrame, "text/html", "UTF-8", null);
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
