package mx.gob.puentesfronterizos.lineaexpres.ui.reqtransferenciasaldo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqTransferenciaSaldoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReqTransferenciaSaldoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}