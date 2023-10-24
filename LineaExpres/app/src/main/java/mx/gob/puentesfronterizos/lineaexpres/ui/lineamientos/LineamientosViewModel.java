package mx.gob.puentesfronterizos.lineaexpres.ui.lineamientos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LineamientosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LineamientosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is wwa fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}