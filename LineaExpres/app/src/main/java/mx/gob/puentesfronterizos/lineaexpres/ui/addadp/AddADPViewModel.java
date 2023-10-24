package mx.gob.puentesfronterizos.lineaexpres.ui.addadp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddADPViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddADPViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}