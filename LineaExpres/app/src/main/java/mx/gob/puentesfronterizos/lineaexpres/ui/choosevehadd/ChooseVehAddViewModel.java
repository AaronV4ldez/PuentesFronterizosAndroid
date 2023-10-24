package mx.gob.puentesfronterizos.lineaexpres.ui.choosevehadd;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChooseVehAddViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ChooseVehAddViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CallUS fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}