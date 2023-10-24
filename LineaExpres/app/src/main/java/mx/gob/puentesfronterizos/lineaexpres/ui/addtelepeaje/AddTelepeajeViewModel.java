package mx.gob.puentesfronterizos.lineaexpres.ui.addtelepeaje;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddTelepeajeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddTelepeajeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}