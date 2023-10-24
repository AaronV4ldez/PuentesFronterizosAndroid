package mx.gob.puentesfronterizos.lineaexpres.ui.process;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentProcessBinding;

public class ProcessFragment extends Fragment {

    private FragmentProcessBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProcessViewModel newsViewModel =
                new ViewModelProvider(this).get(ProcessViewModel.class);

        binding = FragmentProcessBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}