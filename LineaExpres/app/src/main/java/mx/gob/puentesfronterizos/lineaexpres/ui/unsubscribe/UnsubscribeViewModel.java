package mx.gob.puentesfronterizos.lineaexpres.ui.unsubscribe;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class UnsubscribeViewModel extends ViewModel {
    final SavedStateHandle state;

    public UnsubscribeViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}