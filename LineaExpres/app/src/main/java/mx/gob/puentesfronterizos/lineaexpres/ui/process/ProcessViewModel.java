package mx.gob.puentesfronterizos.lineaexpres.ui.process;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProcessViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProcessViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Process fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}