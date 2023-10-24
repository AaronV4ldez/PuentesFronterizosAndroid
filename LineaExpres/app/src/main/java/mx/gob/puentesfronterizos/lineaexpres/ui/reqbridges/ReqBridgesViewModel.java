package mx.gob.puentesfronterizos.lineaexpres.ui.reqbridges;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ReqBridgesViewModel {
    private final MutableLiveData<String> mText;

    public ReqBridgesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
