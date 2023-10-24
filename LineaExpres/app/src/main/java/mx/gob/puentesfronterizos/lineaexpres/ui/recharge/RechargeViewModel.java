package mx.gob.puentesfronterizos.lineaexpres.ui.recharge;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RechargeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RechargeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is recharge fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}