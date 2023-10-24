package mx.gob.puentesfronterizos.lineaexpres.ui.reqinscription;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqInscriptionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReqInscriptionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}