package mx.gob.puentesfronterizos.lineaexpres.ui.tramitespend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TramitesPendViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TramitesPendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}