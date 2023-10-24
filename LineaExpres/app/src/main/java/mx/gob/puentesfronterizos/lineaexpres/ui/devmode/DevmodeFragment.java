package mx.gob.puentesfronterizos.lineaexpres.ui.devmode;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentDevmodeBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.localDB.updateData;


public class DevmodeFragment extends Fragment {
    private FragmentDevmodeBinding binding;
    UserLog userLog;
    updateData UpdateData;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DevmodeViewModel FormalitiesViewModel = new ViewModelProvider(this).get(DevmodeViewModel.class);
        binding = FragmentDevmodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userLog = new UserLog(requireContext());
        UpdateData = new updateData(requireContext());

        String userFireToken = userLog.UserFireToken("get", null);

        EditText showTokenEditText = binding.showFireToken;
        EditText errores = binding.errores;

        String x = UpdateData.getErrores();

        errores.setText(x);
        showTokenEditText.setText(userFireToken);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}