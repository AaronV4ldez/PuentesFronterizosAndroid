package mx.gob.puentesfronterizos.lineaexpres.ui.objectives;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ObjectivesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ObjectivesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}