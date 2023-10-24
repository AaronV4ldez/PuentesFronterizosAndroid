package mx.gob.puentesfronterizos.lineaexpres.ui.subservices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SubservicesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SubservicesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}