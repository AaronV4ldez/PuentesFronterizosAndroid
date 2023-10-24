package mx.gob.puentesfronterizos.lineaexpres.ui.whoweare;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WhoWeAreViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public WhoWeAreViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is wwa fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}