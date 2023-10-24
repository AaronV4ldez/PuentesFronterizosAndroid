package mx.gob.puentesfronterizos.lineaexpres.ui.reqbajavehiculo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqBajaVehiculoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReqBajaVehiculoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}