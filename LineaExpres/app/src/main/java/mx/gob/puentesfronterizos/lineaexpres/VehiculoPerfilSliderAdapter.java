package mx.gob.puentesfronterizos.lineaexpres;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class VehiculoPerfilSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    updateData openDb;
    ArrayList<String> vehiculos;

    public VehiculoPerfilSliderAdapter(Context context) {
        this.context = context;
        openDb = new updateData(context);
        vehiculos = openDb.getVehicles();
    }

    @Override
    public int getCount() {
        return vehiculos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recarga_vehiculos_plantilla, container, false);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(view);

        // Establecer la rotación en el LinearLayout
        layout.setRotation(90);

        // Agregar una rotación al ViewPager
        container.setRotation(-90);

        ImageView ad_slideImageView = (ImageView) view.findViewById(R.id.vehicleImg);
        TextView modeloText = (TextView) view.findViewById(R.id.modeloText);
        TextView placasText = (TextView) view.findViewById(R.id.placasText);
        TextView tagText = (TextView) view.findViewById(R.id.tagText);
        TextView saldoText = (TextView) view.findViewById(R.id.saldoText);

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

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);

        container.addView(layout);

        return layout;
    }





    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }


}