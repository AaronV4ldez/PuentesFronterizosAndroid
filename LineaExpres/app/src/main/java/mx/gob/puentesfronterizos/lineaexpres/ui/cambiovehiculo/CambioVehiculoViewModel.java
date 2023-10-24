package mx.gob.puentesfronterizos.lineaexpres.ui.cambiovehiculo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CambioVehiculoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CambioVehiculoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}