package mx.gob.puentesfronterizos.lineaexpres.ui.accountverified;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentAccountVerifiedBinding;
import mx.gob.puentesfronterizos.lineaexpres.ui.addadp.AccountVerifiedViewModel;

public class AccountVerifiedFragment extends Fragment{


    private FragmentAccountVerifiedBinding binding;
    private String TAG = "TelepeajeADD";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountVerifiedViewModel AddADPFragment =
                new ViewModelProvider(this).get(AccountVerifiedViewModel.class);

        binding = FragmentAccountVerifiedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Button goToLogin = binding.confirmBtn;

        goToLogin.setOnClickListener(view -> {
            requireActivity().runOnUiThread(() -> {
                MainActivity.nav_req(R.id.navigation_login);
            });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}