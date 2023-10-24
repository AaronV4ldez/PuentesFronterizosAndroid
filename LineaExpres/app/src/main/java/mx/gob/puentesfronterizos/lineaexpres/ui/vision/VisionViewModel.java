package mx.gob.puentesfronterizos.lineaexpres.ui.vision;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VisionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public VisionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is vision fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}