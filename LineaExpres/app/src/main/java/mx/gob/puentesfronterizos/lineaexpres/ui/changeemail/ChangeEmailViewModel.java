package mx.gob.puentesfronterizos.lineaexpres.ui.changeemail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChangeEmailViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ChangeEmailViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}