package mx.gob.puentesfronterizos.lineaexpres.ui.banortest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BanortestViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public BanortestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}