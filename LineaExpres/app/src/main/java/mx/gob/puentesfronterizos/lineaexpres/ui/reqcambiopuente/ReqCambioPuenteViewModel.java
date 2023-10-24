package mx.gob.puentesfronterizos.lineaexpres.ui.reqcambiopuente;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqCambioPuenteViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReqCambioPuenteViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}