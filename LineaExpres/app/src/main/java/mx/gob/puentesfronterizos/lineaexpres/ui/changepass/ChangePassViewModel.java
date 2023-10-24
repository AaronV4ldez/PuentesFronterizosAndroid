package mx.gob.puentesfronterizos.lineaexpres.ui.changepass;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChangePassViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ChangePassViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}