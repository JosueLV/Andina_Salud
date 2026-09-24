package pe.edu.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class CitasViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CitasUiState>(CitasUiState.Loading)
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()

    // 1. Cambiado a mostrarSoloHoy para que coincida exactamente con la UI
    private val _mostrarSoloHoy = MutableStateFlow(false)
    val mostrarSoloHoy: StateFlow<Boolean> = _mostrarSoloHoy.asStateFlow()

    private var todasLasCitas: List<Cita> = emptyList()

    // 2. RN-02: Convertimos tu validación en un StateFlow reactivo.
    // La UI lo leerá automáticamente sin tener que llamar a una función.
    val limiteAlcanzado: StateFlow<Boolean> = _uiState.map { estado ->
        if (estado is CitasUiState.Success) {
            estado.citas.size >= 3
        } else {
            false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 3. Variable extra para pintar el número en el ícono inferior (AppNavHost)
    val cantidadProgramadas: StateFlow<Int> = _uiState.map { estado ->
        if (estado is CitasUiState.Success) estado.citas.size else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        cargarCitas()
    }

    private fun cargarCitas() {
        viewModelScope.launch {
            _uiState.value = CitasUiState.Loading
            try {
                obtenerCitasUseCase().collect { lista ->
                    todasLasCitas = lista
                    aplicarFiltro()
                }
            } catch (e: Exception) {
                _uiState.value = CitasUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun toggleFiltroHoy() {
        _mostrarSoloHoy.value = !_mostrarSoloHoy.value
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val activado = _mostrarSoloHoy.value

        val listaFiltrada = if (activado) {
            // Nota: Actualicé "22" a "23" para que coincida con la fecha de hoy
            todasLasCitas.filter { it.fecha.contains("23") || it.fecha.contains("Hoy") }
        } else {
            todasLasCitas
        }

        if (listaFiltrada.isEmpty()) {
            _uiState.value = CitasUiState.Empty
        } else {
            _uiState.value = CitasUiState.Success(listaFiltrada)
        }
    }
}