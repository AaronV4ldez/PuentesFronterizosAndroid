package mx.gob.puentesfronterizos.lineaexpres.ui.callus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CallUsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CallUsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}