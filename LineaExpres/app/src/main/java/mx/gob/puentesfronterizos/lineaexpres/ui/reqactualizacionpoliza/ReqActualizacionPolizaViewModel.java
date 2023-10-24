package mx.gob.puentesfronterizos.lineaexpres.ui.reqactualizacionpoliza;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqActualizacionPolizaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReqActualizacionPolizaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}