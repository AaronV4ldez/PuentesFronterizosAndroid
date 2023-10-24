package mx.gob.puentesfronterizos.lineaexpres.ui.formalities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentFormalitiesBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.util.ArrayList;
import java.util.List;


public class FormalitiesFragment extends Fragment {
    private FragmentFormalitiesBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.M)
    List list = new ArrayList<>();
    private Button BtnReqRegister;
    private Button BtnReqBridges;
    private Button BtnReqVehicleChange;
    private Button BtnReqBalanceTransfer;
    private Button BtnReqBajaDeVehiculoOTagLineaExpress;
    ArrayList<String> userData;
    String User;
    String Token;

    UserLog userLog;
    updateData UpdateData;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FormalitiesViewModel FormalitiesViewModel = new ViewModelProvider(this).get(FormalitiesViewModel.class);
        binding = FragmentFormalitiesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        userLog = new UserLog(requireActivity()); //Open local db connection
        UpdateData = new updateData(requireActivity()); //Open local db connection

        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);

        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
        }

        BtnReqRegister = binding.BtnReqRegister;
        BtnReqBridges = binding.BtnReqChangeBridge;
        BtnReqVehicleChange = binding.BtnReqChangeVehicle;
        BtnReqBalanceTransfer = binding.BtnReqBalanceTransfer;
        BtnReqBajaDeVehiculoOTagLineaExpress = binding.BtnReqBajaDeVehiculoOTagLineaExpress;



        BtnReqRegister.setOnClickListener(v -> {
            MainActivity.Formalities_Requests(1);
        });

        BtnReqBridges.setOnClickListener(v -> {
            MainActivity.Formalities_Requests(2);
        });

        BtnReqVehicleChange.setOnClickListener(v -> {

            MainActivity.Formalities_Requests(3);
        });

        BtnReqBalanceTransfer.setOnClickListener(v -> {
            MainActivity.Formalities_Requests(4);
        });

        BtnReqBajaDeVehiculoOTagLineaExpress.setOnClickListener(v -> {
            MainActivity.Formalities_Requests(5);
        });




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}