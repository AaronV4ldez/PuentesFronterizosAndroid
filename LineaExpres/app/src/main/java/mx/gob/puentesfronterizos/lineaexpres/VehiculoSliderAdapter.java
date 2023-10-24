package mx.gob.puentesfronterizos.lineaexpres;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class VehiculoSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    updateData openDb;
    ArrayList<String> vehiculos;

    public VehiculoSliderAdapter(Context context) {
        this.context = context;
        openDb = new updateData(context);
        vehiculos = openDb.getVehicles();
    }

    @Override
    public int getCount() {
        System.out.println("Entonces también debe moistrar aca: " + vehiculos.size());
        return vehiculos.size();

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.vehiculos_plantilla, container, false);
        ImageView ad_slideImageView = (ImageView) view.findViewById(R.id.vehicleImg);
        TextView modeloText = (TextView) view.findViewById(R.id.modeloText);
        TextView placasText = (TextView) view.findViewById(R.id.placasText);
        TextView tagText = (TextView) view.findViewById(R.id.tagText);
        TextView saldoText = (TextView) view.findViewById(R.id.saldoText);
        LinearLayout btnContainer = (LinearLayout) view.findViewById(R.id.btnContainer);
        Button btnCambioVeh = (Button) view.findViewById(R.id.btnCambioVeh);
        Button btnCambioTag = (Button) view.findViewById(R.id.btnCambioTag);
        Button btnActualizarPoliza = (Button) view.findViewById(R.id.btnActualizarPoliza);
        Button btnActualizarPlaca = (Button) view.findViewById(R.id.btnActualizarPlaca);
        Button btnBajaVehiculo = (Button) view.findViewById(R.id.btnBajaVehiculo);
        //Button btnDesactivarTAG = (Button) view.findViewById(R.id.btnDesactivarTAG);
        Button btnCambioPuente = (Button) view.findViewById(R.id.btnCambioPuente);
        Button btnCambioConvenio = (Button) view.findViewById(R.id.btnCambioConvenio);
        Button btnTransferenciaSaldo = (Button) view.findViewById(R.id.btnTransferenciaSaldo);

        String[] splitArray = vehiculos.get(position).split("∑");
        String Marca = splitArray[1];
        String Linea = splitArray[2];
        String Tag = splitArray[3];
        String imgUrl = splitArray[4];
        String ctl_contract_type = splitArray[5];
        String clt_expiration_date = splitArray[6];
        String Saldo = splitArray[7];
        String Placa = splitArray[8];



        Glide.with(context)
                .load(imgUrl)
                .into(ad_slideImageView);

        modeloText.setText(Marca + " " + Linea);
        placasText.setText(Placa);
        tagText.setText("Tag: " + Tag);

        if (ctl_contract_type.equals("C")) {
            saldoText.setText("Saldo: " + Saldo);

        }
        if (ctl_contract_type.equals("V")) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate hoy = null;
                hoy = LocalDate.now();

                String[] partesFecha = clt_expiration_date.split("-");

                int year = Integer.parseInt(partesFecha[0]);
                int month = Integer.parseInt(partesFecha[1]);
                int dayOfMonth = Integer.parseInt(partesFecha[2]);


                LocalDate fechaObjetivo = LocalDate.of(year, month, dayOfMonth);
                long diferencia = hoy.until(fechaObjetivo, ChronoUnit.DAYS);
                if (diferencia > 30) {

                    btnCambioPuente.setVisibility(View.GONE);
                    btnCambioConvenio.setVisibility(View.GONE);
                }
            }

            saldoText.setText("Contrato vence: " + clt_expiration_date);
            btnTransferenciaSaldo.setVisibility(View.GONE);
        }

        btnCambioVeh.setTag(Placa);
        btnCambioTag.setTag(Placa);
        btnActualizarPoliza.setTag(Placa);
        btnActualizarPlaca.setTag(Placa);
        btnBajaVehiculo.setTag(Placa);
        //btnDesactivarTAG.setTag(Placa);
        btnCambioPuente.setTag(Placa);
        btnCambioConvenio.setTag(Placa);
        btnTransferenciaSaldo.setTag(Placa);

        btnCambioVeh.setOnClickListener(view1 -> {
            openDb.updateCarSelected(btnCambioVeh.getTag().toString());
            MainActivity.nav_req(R.id.navigation_cambio_veh);
        });
        btnCambioTag.setOnClickListener(view1 -> {
            openDb.updateCarSelected(btnCambioTag.getTag().toString());
            MainActivity.nav_req(R.id.navigation_req_cambio_tag);
        });
        btnActualizarPoliza.setOnClickListener(view1 -> {
            openDb.updateCarSelected(btnActualizarPoliza.getTag().toString());
            MainActivity.nav_req(R.id.navigation_req_actualizacion_poliza);
        });
        btnActualizarPlaca.setOnClickListener(view1 -> {
            openDb.updateCarSelected(btnActualizarPlaca.getTag().toString());
            MainActivity.nav_req(R.id.navigation_req_actualizacion_placas);
        });
        btnBajaVehiculo.setOnClickListener(view1 -> {
            openDb.updateCarSelected(btnBajaVehiculo.getTag().toString());
            MainActivity.nav_req(R.id.navigation_req_baja_vehiculo);
        });
        //btnDesactivarTAG.setOnClickListener(view1 -> {
        //    openDb.updateCarSelected(btnDesactivarTAG.getTag().toString());
        //    MainActivity.nav_req(R.id.navigation_req_baja_tag);
        //});
        btnCambioPuente.setOnClickListener(view1 -> {
            openDb.updateCarSelected(btnCambioPuente.getTag().toString());
            MainActivity.nav_req(R.id.navigation_req_cambio_puente);
        });
        btnCambioConvenio.setOnClickListener(view1 -> {
           openDb.updateCarSelected(btnCambioConvenio.getTag().toString());
            MainActivity.nav_req(R.id.navigation_req_cambio_convenio);
        });
        btnTransferenciaSaldo.setOnClickListener(view1 -> {
            openDb.updateCarSelected(btnTransferenciaSaldo.getTag().toString());
            MainActivity.nav_req(R.id.navigation_req_transferencia_saldo);
        });



        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }


}