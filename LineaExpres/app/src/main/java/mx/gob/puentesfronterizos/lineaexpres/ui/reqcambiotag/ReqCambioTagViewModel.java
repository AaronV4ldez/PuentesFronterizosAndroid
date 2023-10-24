package mx.gob.puentesfronterizos.lineaexpres.ui.reqcambiotag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqCambioTagViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReqCambioTagViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}