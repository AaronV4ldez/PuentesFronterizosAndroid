package mx.gob.puentesfronterizos.lineaexpres.ui.currentcitas;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class CurrentCitasViewModel extends ViewModel {
    final SavedStateHandle state;

    public CurrentCitasViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}