package mx.gob.puentesfronterizos.lineaexpres.ui.citas;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class CitasViewModel extends ViewModel {
    final SavedStateHandle state;

    public CitasViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}