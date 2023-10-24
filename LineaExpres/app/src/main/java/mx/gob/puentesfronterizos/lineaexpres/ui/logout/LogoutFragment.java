package mx.gob.puentesfronterizos.lineaexpres.ui.logout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import mx.gob.puentesfronterizos.lineaexpres.MainActivity;
import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentLogoutBinding;
import mx.gob.puentesfronterizos.lineaexpres.localDB.SQLOnInit;
import mx.gob.puentesfronterizos.lineaexpres.localDB.UserLog;
import mx.gob.puentesfronterizos.lineaexpres.ui.logout.LogoutViewModel;

public class LogoutFragment extends Fragment {
    private FragmentLogoutBinding binding;
    UserLog userlog;
    SQLOnInit sqlOnInit;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogoutViewModel LogoutViewModel = new ViewModelProvider(this).get(LogoutViewModel.class);
        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //User database
        userlog = new UserLog(requireContext());
        sqlOnInit = new SQLOnInit(requireContext());
        userlog.Logout();
        sqlOnInit.UserSetIdOnInit();

        MainActivity.nav_req(R.id.navigation_profile);

        

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
