package mx.gob.puentesfronterizos.lineaexpres.ui.rechargeamount;

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
import mx.gob.puentesfronterizos.lineaexpres.databinding.RechargeAmountBinding;

public class RechargeamountFragment extends Fragment {

    Button ConfirmAmount;

    private RechargeAmountBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RechargeamountViewModel dashboardViewModel =
                new ViewModelProvider(this).get(RechargeamountViewModel.class);

        binding = RechargeAmountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ConfirmAmount = binding.ConfirmAmountBtn;

        ConfirmAmount.setOnClickListener(v -> {
            MainActivity.nav_req(R.id.navigation_banortest);
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}