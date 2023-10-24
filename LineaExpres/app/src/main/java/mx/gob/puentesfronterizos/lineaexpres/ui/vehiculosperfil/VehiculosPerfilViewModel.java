package mx.gob.puentesfronterizos.lineaexpres.ui.vehiculosperfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VehiculosPerfilViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public VehiculosPerfilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}