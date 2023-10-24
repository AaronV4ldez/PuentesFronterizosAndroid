package mx.gob.puentesfronterizos.lineaexpres.ui.devmode;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class DevmodeViewModel extends ViewModel {
    final SavedStateHandle state;

    public DevmodeViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}