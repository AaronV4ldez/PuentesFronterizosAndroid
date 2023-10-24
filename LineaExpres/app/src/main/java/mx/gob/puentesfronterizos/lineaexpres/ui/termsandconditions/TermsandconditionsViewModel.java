package mx.gob.puentesfronterizos.lineaexpres.ui.termsandconditions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TermsandconditionsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TermsandconditionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}