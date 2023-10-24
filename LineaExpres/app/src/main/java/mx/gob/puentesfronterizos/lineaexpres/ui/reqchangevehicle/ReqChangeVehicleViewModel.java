package mx.gob.puentesfronterizos.lineaexpres.ui.reqchangevehicle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqChangeVehicleViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReqChangeVehicleViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}