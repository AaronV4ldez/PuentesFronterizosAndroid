package mx.gob.puentesfronterizos.lineaexpres.ui.congratmsg;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class CongratmsgViewModel extends ViewModel {
    final SavedStateHandle state;

    public CongratmsgViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}