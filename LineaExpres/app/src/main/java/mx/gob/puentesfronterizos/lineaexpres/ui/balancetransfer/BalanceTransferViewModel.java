package mx.gob.puentesfronterizos.lineaexpres.ui.balancetransfer;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class BalanceTransferViewModel extends ViewModel {
    final SavedStateHandle state;

    public BalanceTransferViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }

}