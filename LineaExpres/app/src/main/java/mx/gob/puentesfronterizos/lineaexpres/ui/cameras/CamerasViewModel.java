package mx.gob.puentesfronterizos.lineaexpres.ui.cameras;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class CamerasViewModel extends ViewModel {
    final SavedStateHandle state;

    public CamerasViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}