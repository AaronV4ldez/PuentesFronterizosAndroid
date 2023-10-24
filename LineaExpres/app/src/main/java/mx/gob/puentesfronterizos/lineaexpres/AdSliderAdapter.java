package mx.gob.puentesfronterizos.lineaexpres;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;

public class AdSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    updateData openDb;


    public AdSliderAdapter(Context context) {
        this.context = context;
    }

    public int[] ad_slide_images = {
            R.drawable.anuncio1,
            R.drawable.anuncio2
    };
    public String[] ad_slide_headings = {
            "Anuncio1", "Anuncio2"
    };


    @Override
    public int getCount() {
        return ad_slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.ad_slide_layout, container, false);
        ImageView ad_slideImageView = (ImageView) view.findViewById(R.id.ad_image);
        TextView ad_slideHeading = (TextView) view.findViewById(R.id.ad_title);


        ad_slideImageView.setImageResource(ad_slide_images[position]);
        ad_slideHeading.setText(ad_slide_headings[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
