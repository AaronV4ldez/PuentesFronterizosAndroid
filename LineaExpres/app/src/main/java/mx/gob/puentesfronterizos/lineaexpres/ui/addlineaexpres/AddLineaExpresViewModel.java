package mx.gob.puentesfronterizos.lineaexpres.ui.addlineaexpres;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddLineaExpresViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddLineaExpresViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}