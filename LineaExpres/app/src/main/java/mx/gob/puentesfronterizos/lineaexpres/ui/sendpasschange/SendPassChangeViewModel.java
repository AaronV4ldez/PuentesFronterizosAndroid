package mx.gob.puentesfronterizos.lineaexpres.ui.sendpasschange;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendPassChangeViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public SendPassChangeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ProfileViewModel fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
