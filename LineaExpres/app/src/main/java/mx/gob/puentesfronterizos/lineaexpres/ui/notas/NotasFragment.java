package mx.gob.puentesfronterizos.lineaexpres.ui.notas;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentNotasBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class NotasFragment extends Fragment {

    private FragmentNotasBinding binding;
    private ArrayList<String> Nota;
    private String PrevNota;
    private String NextNota;
    private int id;
    private String Title;
    private String Body;
    private String Img;
    TextView TextView_Titulo;
    ImageView ImageView_ImgPortada;
    TextView TextView_Cuerpo;
    Button PrevBtn;
    Button NextBtn;
    ArrayList<Integer> idArrays;
    updateData NotasDB;
    int NextID;
    int PrevID;
    TextView PrevLbl;
    TextView NextLbl;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotasViewModel notasViewModel = new ViewModelProvider(this).get(NotasViewModel.class);
        binding = FragmentNotasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        NotasDB = new updateData(requireActivity()); //Open local db connection

        idArrays = NotasDB.getNoteID();
        PrevBtn = binding.prevBtn;
        NextBtn = binding.nextBtn;

        try {
            id = idArrays.get(0);

            PrevLbl = binding.prevLabel;
            NextLbl = binding.nextLabel;


            TextView_Titulo = binding.titleNota;
            ImageView_ImgPortada = binding.imageView2;
            TextView_Cuerpo = binding.textInformative;

            Nota = NotasDB.getNote(id);

            Title = Nota.get(0);
            Img = Nota.get(2);
            Body = Nota.get(1);

            TextView_Titulo.setText(Title);
            Glide.with(requireContext())
                    .load(Img)
                    .into(ImageView_ImgPortada);
            TextView_Cuerpo.setText(Html.fromHtml(Body));
            setNotesAds();

            changePrevNextBtns();

            ScrollView mainScroll = binding.mainScroll;


            //Click listener buttons...
            PrevBtn.setOnClickListener(v -> {
                if (PrevBtn.getText().toString().isEmpty()) {
                    System.out.println("No hay nada en PrevBtn");
                    return;
                }
                mainScroll.fullScroll(ScrollView.FOCUS_UP);

                Nota = NotasDB.getNote(PrevID);
                Title = Nota.get(0);
                Img = Nota.get(2);
                Body = Nota.get(1);

                TextView_Titulo.setText(Title);
                Glide.with(requireContext())
                        .load(Img)
                        .into(ImageView_ImgPortada);
                TextView_Cuerpo.setText(Html.fromHtml(Body));

                String newPrevID = String.valueOf(PrevID);

                NotasDB.NoteOnClick(Integer.parseInt(newPrevID));
                NotasDB.PrevNoteOnClick(Integer.parseInt(newPrevID));
                NotasDB.NextNoteOnClick(Integer.parseInt(newPrevID));

                changePrevNextBtns();

            });
            NextBtn.setOnClickListener(v -> {
                if (NextBtn.getText().toString().isEmpty()) {
                    System.out.println("No hay nada en NextBtn");
                    return;
                }
                mainScroll.fullScroll(ScrollView.FOCUS_UP);

                Nota = NotasDB.getNote(NextID);
                Title = Nota.get(0);
                Img = Nota.get(2);
                Body = Nota.get(1);

                TextView_Titulo.setText(Title);
                Glide.with(requireContext())
                        .load(Img)
                        .into(ImageView_ImgPortada);
                TextView_Cuerpo.setText(Html.fromHtml(Body));

                String newNextID = String.valueOf(NextID);

                NotasDB.NoteOnClick(Integer.parseInt(newNextID));
                NotasDB.PrevNoteOnClick(Integer.parseInt(newNextID));
                NotasDB.NextNoteOnClick(Integer.parseInt(newNextID));

                changePrevNextBtns();

            });
        }catch (Exception e) {
            Log.e("NotasFragment", "onCreateView: ", e);
            Toast.makeText(requireContext(), "No hay acceso a internet", Toast.LENGTH_SHORT).show();
            MainActivity.nav_req(R.id.navigation_home);
        }


        return root;
    }

    public void changePrevNextBtns() {
        idArrays = NotasDB.getNoteID();
        PrevID = idArrays.get(2);
        if (PrevID != 0) {
            PrevNota = NotasDB.getNote(PrevID).get(0);
            PrevBtn.setText(PrevNota);
            PrevLbl.setVisibility(View.VISIBLE);
            PrevBtn.setVisibility(View.VISIBLE);
        }else {
            PrevLbl.setVisibility(View.GONE);
            PrevBtn.setVisibility(View.GONE);
        }

        NextID = idArrays.get(1);
        if (NextID != 0) {
            NextNota = NotasDB.getNote(NextID).get(0);
            NextBtn.setText(NextNota);
            NextLbl.setVisibility(View.VISIBLE);
            NextBtn.setVisibility(View.VISIBLE);
        }else {
            NextLbl.setVisibility(View.GONE);
            NextBtn.setVisibility(View.GONE);
        }

    }

    public void setNotesAds(){
        new Thread(() -> {
            String jsonURL = "https://apis.fpfch.gob.mx/api/v1/config/mobile";
            URL url;
            try {
                url = new URL(jsonURL);
                URLConnection request = url.openConnection();
                request.connect();
                JsonObject jsonObj = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();

                String mbInterioresURL = (String) jsonObj.get("mbInterioresURL").getAsString();

                requireActivity().runOnUiThread(() -> {
                    final WebView webView;
                    webView = (WebView) binding.adCarouselNotas;
                    webView.setWebChromeClient(new WebChromeClient());
                    webView.setWebViewClient(new WebViewClient());
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    String iFrame = "<html><body style='margin: 0; padding: 0;'><img src='" + mbInterioresURL + "' alt=''></body></html>";


                    webView.loadDataWithBaseURL(null, iFrame, "text/html", "UTF-8", null);
                });


            } catch (IOException e) {
                Log.e("Youtube Embed", "setYoutubeEmbeded: ", e);
            }
        }).start();
    }


    @Override
    public void onDestroyView() {

        super.onDestroyView();
        binding = null;
    }
}