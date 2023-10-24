package mx.gob.puentesfronterizos.lineaexpres.ui.callus;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import mx.gob.puentesfronterizos.lineaexpres.R;
import mx.gob.puentesfronterizos.lineaexpres.databinding.FragmentCallusBinding;

public class CallUsFragment extends Fragment {

    private FragmentCallusBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CallUsViewModel callUsFragment =
                new ViewModelProvider(this).get(CallUsViewModel.class);

        binding = FragmentCallusBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Dialer
        String numberString = getText(R.string.numberToCall).toString();
        Uri number = Uri.parse("tel:" + numberString);
        Intent dial = new Intent(Intent.ACTION_DIAL, number);
        startActivity(dial);

        getActivity().onBackPressed();

        //Dialer endline
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}