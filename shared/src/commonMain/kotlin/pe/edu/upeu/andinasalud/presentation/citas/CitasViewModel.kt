package pe.edu.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class CitasViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase
) : ViewModel() {

    // El estado mutable es privado (Regla del examen)
    private val _uiState = MutableStateFlow<CitasUiState>(CitasUiState.Loading)
    // Exponemos solo la versión inmutable
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()

    init {
        cargarCitas()
    }

    private fun cargarCitas() {
        viewModelScope.launch {
            _uiState.value = CitasUiState.Loading

            obtenerCitasUseCase()
                .catch { e ->
                    // Si ocurre un fallo, mostramos el estado de Error
                    _uiState.value = CitasUiState.Error(e.message ?: "Ocurrió un error inesperado")
                }
                .collect { lista ->
                    // Verificamos si la lista está vacía
                    if (lista.isEmpty()) {
                        _uiState.value = CitasUiState.Empty
                    } else {
                        _uiState.value = CitasUiState.Success(lista)
                    }
                }
        }
    }
}