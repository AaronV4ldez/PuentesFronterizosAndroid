package mx.gob.puentesfronterizos.lineaexpres.ui.services;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentServicesBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.ServicesDB;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.util.ArrayList;

public class ServicesFragment extends Fragment {
    private FragmentServicesBinding binding;
    updateData openDb;
    ServicesDB ServicesDB;
    ArrayList<String> Serv_titles;
    ArrayList<String> Serv_Images;
    ArrayList<String> Serv_id;
    LinearLayout relativeLayout;
    ImageView imgContainer;
    TextView plecaServicio;
    TextView titleServicio;
    int i;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ServicesViewModel ServiceViewModel =
                new ViewModelProvider(this).get(ServicesViewModel.class);

        binding = FragmentServicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LinearLayout linearLayout = (LinearLayout) binding.ServContentContainer;
        openDb = new updateData(requireContext());
        ServicesDB = new ServicesDB(requireContext());
        Serv_id = openDb.getServiceID();
        Serv_titles = openDb.getServiceTitle();
        Serv_Images = openDb.getServiceImageURL();

        for (i = 0; i < Serv_id.size(); i++) {
            //Generating news items
            relativeLayout = new LinearLayout(requireContext());
            imgContainer = new ImageView(requireContext());
            plecaServicio = new TextView(requireContext());
            titleServicio = new TextView(requireContext());

            //Set image to ImageView
            Glide.with(requireContext())
                    .load(Serv_Images.get(i))
                    .centerCrop()
                    .into(imgContainer);


            //Set Text to Pleca
            plecaServicio.setText("SERVICIOS");

            //set Text to Title
            titleServicio.setText(Serv_titles.get(i));

            //Add img, pleca, and title to Relative Layout
            relativeLayout.addView(imgContainer);
            relativeLayout.addView(plecaServicio);
            relativeLayout.addView(titleServicio);

            //Add Relative Layout to Linear Layout
            linearLayout.addView(relativeLayout);

            //TODO Editing after set into Linear Layout

            //Relative Layout
            relativeLayout.setId(Integer.parseInt(Serv_id.get(i)));
            relativeLayout.setFocusable(true);
            relativeLayout.setClickable(true);
            relativeLayout.setOrientation(LinearLayout.VERTICAL);

            //Editing ImageView
            imgContainer.setId(View.generateViewId());
            imgContainer.getLayoutParams().height = 680;
            imgContainer.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            imgContainer.requestLayout();

            //Editing Pleca
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,0);
            plecaServicio.setLayoutParams(params);
            plecaServicio.setTextSize(18);
            titleServicio.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            plecaServicio.setBackgroundResource(R.color.black);
            plecaServicio.setTextColor(Color.parseColor("#ffffff"));
            plecaServicio.setPadding(50,10,50,10);

            //Editing Title
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,-250,0,70);
            titleServicio.setLayoutParams(titleParams);
            titleServicio.setPadding(10,40,10,40);
            titleServicio.setTextSize(25);
            titleServicio.setTypeface(Typeface.DEFAULT_BOLD);
            titleServicio.setGravity(View.TEXT_ALIGNMENT_CENTER);
            titleServicio.setBackgroundColor(Color.parseColor("#66000000"));
            titleServicio.setTextColor(Color.parseColor("#FFFFFF"));

            String ServID = Serv_id.get(i);

            relativeLayout.setOnClickListener(v -> {
                ServicesDB.SubServOnClick(ServID);
                ServicesDB.PrevServOnClick(ServID);
                ServicesDB.NextServOnClick(ServID);
                MainActivity.nav_req(R.id.navigation_subservices);
            });

        }




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}