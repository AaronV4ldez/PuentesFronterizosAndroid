package mx.gob.puentesfronterizos.lineaexpres.ui.formalities;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class FormalitiesViewModel extends ViewModel {
    final SavedStateHandle state;

    public FormalitiesViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}