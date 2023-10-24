package mx.gob.puentesfronterizos.lineaexpres.ui.derechos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DerechosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DerechosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Poll fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}