package mx.gob.puentesfronterizos.lineaexpres.ui.subservices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentSubservicesBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.ServicesDB;

import java.util.ArrayList;

public class SubservicesFragment extends Fragment {
    private FragmentSubservicesBinding binding;

    ServicesDB servicesDB;

    ArrayList<Integer> idArrays;
    int NextID;
    int PrevID;

    ArrayList<String> Service;
    String Service_Title;
    String Service_Body;
    String Service_Image;

    String getPrevTitle;
    String getNextTitle;
    ArrayList<String> getNewService;
    String Title_Serv;
    String Img_Serv;
    String Body_Serv;
    Button PrevBtn;
    Button NextBtn;
    TextView PrevLbl;
    TextView NextLbl;
    ScrollView service_Fragment;
    int PrevServID;
    int CurrentServID;
    int NextServID;
    TextView layout_title_serv;
    WebView layout_body_serv;
    ImageView layout_image_serv;
    String iFrame;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       SubservicesViewModel subservicesViewModel = new ViewModelProvider(this).get(SubservicesViewModel.class);
        binding = FragmentSubservicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        servicesDB = new ServicesDB(requireActivity()); //Open local db connection


        //Setting layouts
        //Prev
        PrevLbl = binding.prevLabel;
        PrevBtn = binding.prevBtn;
        //Next
        NextLbl = binding.nextLabel;
        NextBtn = binding.nextBtn;
        //Scroll
        service_Fragment = binding.mainScroll;

        //TODO Service Layout to change
        layout_title_serv = binding.titleServ; //Title Layout
        layout_body_serv = binding.textInformative; //Body Layout
        layout_image_serv = binding.imageView2; // Image Layout


        //Setting id's to variables
        idArrays = servicesDB.getSubServIDs(); //Get ID's
        CurrentServID = idArrays.get(0); //set Current id

        // Getting Title, Body and image form db
        Service = servicesDB.getService(CurrentServID); //Get Title, Body and image from db and set as Array
        Service_Title = Service.get(0); //Getting Title
        Service_Body = Service.get(1); //Getting Body
        Service_Image = Service.get(2); //Getting Image


        //TODO Editing Layout
        //Set Title
        layout_title_serv.setText(Service_Title);

        //Set Body
        iFrame = "<html><body style='margin: 0; padding: 0; width:100vw'> "+Service_Body+" </body></html>";
        layout_body_serv.loadDataWithBaseURL(null, iFrame, "text/html", "UTF-8", null);

        //Set Image
        Glide.with(requireContext())
                .load(Service_Image)
                .into(layout_image_serv);

        //TODO On click listeners
            //TODO PrevBTN on click
        PrevBtn.setOnClickListener(v -> {
            Service = servicesDB.getService(PrevServID); //Get Title, Body and image from db and set as Array
            Service_Title = Service.get(0); //Getting Title
            Service_Body = Service.get(1); //Getting Body
            Service_Image = Service.get(2); //Getting Image

            //TODO Editing Layout
            //Set Title
            layout_title_serv.setText(Service_Title);

            //Set Body
            iFrame = "<html><body style='margin: 0; padding: 0; width:100vw'> "+Service_Body+" </body></html>";
            layout_body_serv.loadDataWithBaseURL(null, iFrame, "text/html", "UTF-8", null);

            //Set Image
            Glide.with(requireContext())
                    .load(Service_Image)
                    .into(layout_image_serv);

            service_Fragment.fullScroll(ScrollView.FOCUS_UP);

            String NewPrevServID = String.valueOf(PrevServID);

            servicesDB.SubServOnClick(NewPrevServID); //Setting news id's
            servicesDB.PrevServOnClick(NewPrevServID); //Setting news id's
            servicesDB.NextServOnClick(NewPrevServID); //Setting news id's

            changePrevNextBtns();
        });
        NextBtn.setOnClickListener(v -> {
            Service = servicesDB.getService(NextServID); //Get Title, Body and image from db and set as Array
            Service_Title = Service.get(0); //Getting Title
            Service_Body = Service.get(1); //Getting Body
            Service_Image = Service.get(2); //Getting Image

            //TODO Editing Layout
            //Set Title
            layout_title_serv.setText(Service_Title);

            //Set Body
            iFrame = "<html><body style='margin: 0; padding: 0; width:100vw'> "+Service_Body+" </body></html>";
            layout_body_serv.loadDataWithBaseURL(null, iFrame, "text/html", "UTF-8", null);

            //Set Image
            Glide.with(requireContext())
                    .load(Service_Image)
                    .into(layout_image_serv);

            service_Fragment.fullScroll(ScrollView.FOCUS_UP);

            String NewNextServID = String.valueOf(NextServID);

            servicesDB.SubServOnClick(NewNextServID); //Setting news id's
            servicesDB.PrevServOnClick(NewNextServID); //Setting news id's
            servicesDB.NextServOnClick(NewNextServID); //Setting news id's

            changePrevNextBtns();
        });

        changePrevNextBtns();
        return root;
    }

    public void changePrevNextBtns() {
        idArrays = servicesDB.getSubServIDs(); //Get ID's Again
        NextServID = idArrays.get(1);
        PrevServID = idArrays.get(2);

        //Set PrevService
        if (PrevServID != 0) {
            getPrevTitle = servicesDB.getTitleServ(PrevServID);
            PrevBtn.setText(getPrevTitle);
            PrevLbl.setVisibility(View.VISIBLE);
            PrevBtn.setVisibility(View.VISIBLE);
        }else {
            PrevLbl.setVisibility(View.GONE);
            PrevBtn.setVisibility(View.GONE);
        }

        //Set NextService
        if (NextServID != 0) {
            getNextTitle = servicesDB.getTitleServ(NextServID);
            NextBtn.setText(getNextTitle);
            NextLbl.setVisibility(View.VISIBLE);
            NextBtn.setVisibility(View.VISIBLE);
        }else {
            NextLbl.setVisibility(View.GONE);
            NextBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}