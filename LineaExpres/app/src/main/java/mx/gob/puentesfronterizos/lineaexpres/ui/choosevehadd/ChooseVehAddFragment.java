package mx.gob.puentesfronterizos.lineaexpres.ui.choosevehadd;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentChooseVehAddBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class ChooseVehAddFragment extends Fragment {
    UserLog userLog;
    ArrayList<String> userData;
    String User;
    String Token;
    updateData UpdateData;
    String FName;
    private FragmentChooseVehAddBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChooseVehAddViewModel ChooseVehAddFragment =
                new ViewModelProvider(this).get(ChooseVehAddViewModel.class);

        binding = FragmentChooseVehAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        userLog = new UserLog(requireActivity()); //Open local db connection
        updateData UpdateDato = new updateData(requireActivity()); //Open local db connection
        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);
        FName = userData.get(2);
        UpdateData = new updateData(requireContext()); //Open Users db

        TextView welcomeMsg = binding.welcomeMsg;
        Button btnLineaExpres = binding.btnLineaExpres;
        Button btnTelepeaje = binding.btnTelepeaje;
        Button goToSolInsc = binding.goToSolInsc;
        TextView textView2 = binding.textView2;


        goToSolInsc.setOnClickListener(view -> {
            UpdateDato.updateCarSelected("Inscripcion");
            MainActivity.nav_req(R.id.navigation_req_inscription);

             });

        welcomeMsg.setText(FName + ", gracias por registrarte con nosotros");

        ArrayList<String> vehiculos = UpdateData.getVehicles();

        if (vehiculos.size() != 0) {
            btnLineaExpres.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            goToSolInsc.setVisibility(View.GONE);
        }

        btnLineaExpres.setOnClickListener(view -> {
            MainActivity.nav_req(R.id.navigation_linea_expres_add_vehicle);
        });
        btnTelepeaje.setOnClickListener(view -> {
            MainActivity.nav_req(R.id.navigation_telepeaje_add_vehicle);
        });


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}