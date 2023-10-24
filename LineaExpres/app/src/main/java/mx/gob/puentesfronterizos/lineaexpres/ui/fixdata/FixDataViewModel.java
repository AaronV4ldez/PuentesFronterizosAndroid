package mx.gob.puentesfronterizos.lineaexpres.ui.fixdata;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class FixDataViewModel extends ViewModel {
    final SavedStateHandle state;

    public FixDataViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }
}
