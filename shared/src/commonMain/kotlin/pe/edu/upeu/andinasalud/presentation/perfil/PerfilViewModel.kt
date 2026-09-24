package pe.edu.upeu.andinasalud.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase
import kotlin.coroutines.cancellation.CancellationException

class PerfilViewModel(
    private val obtenerPacienteUseCase: ObtenerPacienteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PerfilUiState>(PerfilUiState.Loading)
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = PerfilUiState.Loading
            try {
                _uiState.value = PerfilUiState.Success(obtenerPacienteUseCase())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = PerfilUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
