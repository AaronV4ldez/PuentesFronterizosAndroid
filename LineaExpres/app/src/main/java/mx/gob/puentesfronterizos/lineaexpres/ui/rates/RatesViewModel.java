package mx.gob.puentesfronterizos.lineaexpres.ui.rates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RatesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RatesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is vision fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}