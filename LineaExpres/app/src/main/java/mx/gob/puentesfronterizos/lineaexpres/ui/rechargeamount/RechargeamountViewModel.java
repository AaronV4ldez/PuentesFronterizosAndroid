package mx.gob.puentesfronterizos.lineaexpres.ui.rechargeamount;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RechargeamountViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RechargeamountViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is recharge fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}