package mx.gob.puentesfronterizos.lineaexpres.ui.miscruces;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MisCrucesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MisCrucesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}