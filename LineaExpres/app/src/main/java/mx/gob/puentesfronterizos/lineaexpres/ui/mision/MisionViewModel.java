package mx.gob.puentesfronterizos.lineaexpres.ui.mision;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MisionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MisionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}