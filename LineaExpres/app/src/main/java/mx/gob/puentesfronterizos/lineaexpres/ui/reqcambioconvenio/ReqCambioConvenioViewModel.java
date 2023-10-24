package mx.gob.puentesfronterizos.lineaexpres.ui.reqcambioconvenio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqCambioConvenioViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReqCambioConvenioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}