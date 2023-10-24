package mx.gob.puentesfronterizos.lineaexpres.ui.crearcita;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class CrearCitaViewModel extends ViewModel {
    final SavedStateHandle state;

    public CrearCitaViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}