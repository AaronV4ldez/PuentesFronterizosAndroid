package mx.gob.puentesfronterizos.lineaexpres.ui.recharge;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentRechargeBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class RechargeFragment extends Fragment {

    private FragmentRechargeBinding binding;
    ArrayList<String> userData;
    String User;
    String Token;
    updateData UpdateData;
    UserLog userLog;

    String tipoContrato;
    String num_tag;
    String ctl_contract_type;
    String ctl_stall_id;

    String Nacionalidad = "Mexico";

    //Tarifas
    String anual_zaragoza_mx = "";
    String anual_lerdo_mx = "";
    String anual_zaragoza_us = "";
    String anual_lerdo_us = "";
    String anual_mixto_mx = "";
    String anual_mixto_us = "";
    String saldo_zaragoza1_mx = "";
    String saldo_zaragoza2_mx = "";
    String saldo_zaragoza1_us = "";
    String saldo_zaragoza2_us = "";
    String pago_minimotp_mx = "";

    String CantidadARecargar = "";
    EditText TelepeajeRecarga;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RechargeViewModel dashboardViewModel =
                new ViewModelProvider(this).get(RechargeViewModel.class);

        binding = FragmentRechargeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Database Declarations
        UpdateData = new updateData(requireActivity()); //Open local db connection
        userLog = new UserLog(requireActivity()); //Open local db connection


        //Variable Declarations & Database gets
        userData = userLog.GetUserData();
        User = userData.get(0);
        Token = userData.get(1);
        if (Token == null || Token.isEmpty()) {
            MainActivity.nav_req(R.id.navigation_login);
        }

        getPrices();

        String CarSelected = UpdateData.getCarSelected();
        ArrayList<String> CarList = UpdateData.getProfileVehicles();
        for (int i = 0; i < CarList.size(); i++) {
            String[] splitArray = CarList.get(i).split("∑");
            if (splitArray[8].equals(CarSelected)) {
                tipoContrato = splitArray[0];
                num_tag = splitArray[3];
                ctl_contract_type = splitArray[5];
                ctl_stall_id = splitArray[11];
            }

        }



        //Linea Expres
        LinearLayout LineaExpresLayout = binding.LineaExpresLayout;
        Button btnMX = binding.btnMX;
        Button btnUS = binding.btnUSA;
        Button btnSaldo1 = binding.btnSaldo1;
        Button btnSaldo2 = binding.btnSaldo2;
        EditText tagSelected = binding.tagSelected;
        Button btnRecargar = binding.btnRecargar;

        //Telepeaje
        LinearLayout TelepeajeLayout = binding.TelepeajeLayout;
        TelepeajeRecarga = binding.TelepeajeRecarga;
        EditText TelepeajetagSelected = binding.TelepeajetagSelected;
        Button btnRecargarTelepeaje = binding.btnRecargarTelepeaje;



        if (tipoContrato.equals("0") || tipoContrato.equals("2")) {
            TelepeajeLayout.setVisibility(View.VISIBLE);

        }else {
            LineaExpresLayout.setVisibility(View.VISIBLE);
        }

        tagSelected.setText(num_tag);
        TelepeajetagSelected.setText(num_tag);
        btnMX.setOnClickListener(v -> {
            System.out.println(ctl_stall_id);
            System.out.println(ctl_contract_type);
            Nacionalidad = "Mexico";
            btnMX.setBackgroundResource(R.drawable.buttons);
            btnUS.setBackgroundResource(R.drawable.buttons2);
            btnSaldo1.setBackgroundResource(R.drawable.buttons2);
            btnSaldo2.setBackgroundResource(R.drawable.buttons2);

            if (ctl_contract_type != null || ctl_contract_type != "") {
                if (ctl_contract_type.equals("C")) {
                    if (ctl_stall_id != null || ctl_stall_id != "") {
                        if (ctl_stall_id.equals("104")) {
                            btnSaldo1.setText(saldo_zaragoza1_mx);
                            btnSaldo2.setText(saldo_zaragoza2_mx);
                        }
                        if (ctl_stall_id.equals("105")) {
                            btnSaldo1.setText(saldo_zaragoza1_mx);
                            btnSaldo2.setText(saldo_zaragoza2_mx);
                        }
                    }


                }
                if (ctl_contract_type.equals("M")) {
                    btnSaldo1.setText(anual_mixto_mx);
                    CantidadARecargar = anual_mixto_mx;
                    btnSaldo1.setBackgroundResource(R.drawable.buttons);
                }
                if (ctl_contract_type.equals("V")) {

                    if (ctl_stall_id != null || ctl_stall_id != "") {
                        if (ctl_stall_id.equals("104")) {
                            btnSaldo1.setText(anual_lerdo_mx);
                            btnSaldo2.setVisibility(View.GONE);
                        }
                        if (ctl_stall_id.equals("105")) {
                            btnSaldo1.setText(anual_zaragoza_mx);
                            btnSaldo2.setVisibility(View.GONE);
                        }
                    }
                }
            }



        });

        btnUS.setOnClickListener(v -> {
            System.out.println(ctl_stall_id);
            System.out.println(ctl_contract_type);
            Nacionalidad = "USA";
            btnUS.setBackgroundResource(R.drawable.buttons);
            btnMX.setBackgroundResource(R.drawable.buttons2);
            btnSaldo1.setBackgroundResource(R.drawable.buttons2);
            btnSaldo2.setBackgroundResource(R.drawable.buttons2);

            if (ctl_contract_type != null || ctl_contract_type != "") {
                if (ctl_contract_type.equals("C")) {
                    if (ctl_stall_id != null || ctl_stall_id != "") {
                        if (ctl_stall_id.equals("104")) {
                            btnSaldo1.setText(saldo_zaragoza1_us);
                            btnSaldo2.setText(saldo_zaragoza2_us);
                        }
                        if (ctl_stall_id.equals("105")) {
                            btnSaldo1.setText(saldo_zaragoza1_us);
                            btnSaldo2.setText(saldo_zaragoza2_us);
                        }
                    }


                }
                if (ctl_contract_type.equals("M")) {
                    btnSaldo1.setText(anual_mixto_us);
                    btnSaldo1.setBackgroundResource(R.drawable.buttons);
                    CantidadARecargar = anual_mixto_us;
                }
                if (ctl_contract_type.equals("V")) {

                    if (ctl_stall_id != null || ctl_stall_id != "") {
                        if (ctl_stall_id.equals("104")) {
                            btnSaldo1.setText(anual_lerdo_us);
                            btnSaldo2.setVisibility(View.GONE);
                        }
                        if (ctl_stall_id.equals("105")) {
                            btnSaldo1.setText(anual_zaragoza_us);
                            btnSaldo2.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        btnSaldo1.setOnClickListener(v -> {
            if (Nacionalidad.equals("")) {
                return;
            }
            btnSaldo1.setBackgroundResource(R.drawable.buttons);
            btnSaldo2.setBackgroundResource(R.drawable.buttons2);

            CantidadARecargar = btnSaldo1.getText().toString();

        });
        btnSaldo2.setOnClickListener(v -> {
            if (Nacionalidad.equals("")) {
                return;
            }
            btnSaldo1.setBackgroundResource(R.drawable.buttons2);
            btnSaldo2.setBackgroundResource(R.drawable.buttons);
            CantidadARecargar = btnSaldo2.getText().toString();

        });

        btnRecargar.setOnClickListener(view -> {
            System.out.println("ESte recargar");
            if (CantidadARecargar.equals("")) {
                return;
            }
            if (CantidadARecargar.equals("---")) {
                return;
            }
            UpdateData.updateCantidadRecargar(CantidadARecargar);
            MainActivity.nav_req(R.id.navigation_banortest);
        });
        btnRecargarTelepeaje.setOnClickListener(view -> {

            if (CantidadARecargar.equals("---")) {
                return;
            }
            UpdateData.updateCantidadRecargar(TelepeajeRecarga.getText().toString());
            MainActivity.nav_req(R.id.navigation_banortest);
        });



        return root;
    }

    public void getPrices(){
        new Thread(() -> {
            String jsonURL = "https://apis.fpfch.gob.mx/api/v1/config/mobile";
            URL url;
            try {
                url = new URL(jsonURL);
                URLConnection request = url.openConnection();
                request.connect();
                JsonObject jsonObj = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();

                anual_zaragoza_mx = (String) jsonObj.get("anual_zaragoza_mx").getAsString();
                anual_lerdo_mx = (String) jsonObj.get("anual_lerdo_mx").getAsString();
                anual_zaragoza_us = (String) jsonObj.get("anual_zaragoza_us").getAsString();
                anual_lerdo_us = (String) jsonObj.get("anual_lerdo_us").getAsString();
                anual_mixto_mx = jsonObj.get("anual_mixto_mx").getAsString();
                anual_mixto_us = jsonObj.get("anual_mixto_us").getAsString();
                saldo_zaragoza1_mx = (String) jsonObj.get("saldo_zaragoza1_mx").getAsString();
                saldo_zaragoza2_mx = (String) jsonObj.get("saldo_zaragoza2_mx").getAsString();
                saldo_zaragoza1_us = (String) jsonObj.get("saldo_zaragoza1_us").getAsString();
                saldo_zaragoza2_us = (String) jsonObj.get("saldo_zaragoza2_us").getAsString();
                pago_minimotp_mx = jsonObj.get("pago_minimotp_mx").getAsString();
                TelepeajeRecarga.setText(pago_minimotp_mx);


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