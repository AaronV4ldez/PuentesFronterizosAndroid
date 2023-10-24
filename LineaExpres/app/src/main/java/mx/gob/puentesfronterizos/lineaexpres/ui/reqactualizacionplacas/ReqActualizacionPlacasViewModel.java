package mx.gob.puentesfronterizos.lineaexpres.ui.reqactualizacionplacas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqActualizacionPlacasViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReqActualizacionPlacasViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}