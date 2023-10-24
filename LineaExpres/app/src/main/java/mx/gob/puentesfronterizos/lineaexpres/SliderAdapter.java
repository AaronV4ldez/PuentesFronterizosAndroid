package mx.gob.puentesfronterizos.lineaexpres;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;


public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    updateData openDb;
    String[] slide_heading;
    String[] slide_images;
    String[] slide_ids;
    Button slideHeading;


    public SliderAdapter(Context context) {
       this.context = context;
       openDb = new updateData(context);
       slide_heading = openDb.getTitles().toArray(new String[0]);
       slide_images = openDb.getUrls().toArray(new String[0]);
       slide_ids = openDb.getIds().toArray(new String[0]);
    }

    //Arrays
    @Override
    public int getCount() {

        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
        ImageView slideImageView = (ImageView) view.findViewById(R.id.image_note);
        slideHeading = (Button) view.findViewById(R.id.title_note);

        //Glide.with(context)
        //       .load(slide_images[position])
        //       .into(slideImageView);

        slideHeading.setText(slide_heading[position]);

        slideHeading.setTag(slide_ids[position]);
        int id = 0;
        try {
            id = Integer.parseInt((String) slideHeading.getTag());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        int finalId = id;
        slideHeading.setOnClickListener(v -> {
            openDb.NoteOnClick(finalId);
            openDb.PrevNoteOnClick(finalId);
            openDb.NextNoteOnClick(finalId);

            MainActivity.nav_req(R.id.navigation_notas);
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
