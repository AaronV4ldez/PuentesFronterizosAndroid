package mx.gob.puentesfronterizos.lineaexpres.ui.facturacion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FacturacionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FacturacionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}