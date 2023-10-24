package mx.gob.puentesfronterizos.lineaexpres.ui.congratmsg;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Timer;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentCongratmsgBinding;


public class CongratmsgFragment extends Fragment {
    private FragmentCongratmsgBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CongratmsgViewModel CongratmsgViewModel = new ViewModelProvider(this).get(CongratmsgViewModel.class);
        binding = FragmentCongratmsgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.nav_req(R.id.navigation_home);
            }
        }, 1000);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}