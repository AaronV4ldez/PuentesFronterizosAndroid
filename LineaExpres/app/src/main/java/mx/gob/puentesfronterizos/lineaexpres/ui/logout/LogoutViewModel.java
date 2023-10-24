package mx.gob.puentesfronterizos.lineaexpres.ui.logout;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class LogoutViewModel extends ViewModel {
    final SavedStateHandle state;

    public LogoutViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}