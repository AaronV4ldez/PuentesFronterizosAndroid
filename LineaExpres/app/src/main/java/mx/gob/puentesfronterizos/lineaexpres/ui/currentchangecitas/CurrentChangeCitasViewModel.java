package mx.gob.puentesfronterizos.lineaexpres.ui.currentchangecitas;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class CurrentChangeCitasViewModel extends ViewModel {
    final SavedStateHandle state;

    public CurrentChangeCitasViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}