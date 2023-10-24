package mx.gob.puentesfronterizos.lineaexpres.ui.citaschangedate;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class ChangeCitaDateViewModel extends ViewModel {
    final SavedStateHandle state;

    public ChangeCitaDateViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}