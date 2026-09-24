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

    private val _mostrarSoloHoy = MutableStateFlow(false)
    val mostrarSoloHoy: StateFlow<Boolean> = _mostrarSoloHoy.asStateFlow()

    // RF-02: Nuevo estado para controlar la pestaña seleccionada
    private val _estadoFiltro = MutableStateFlow("Todas")
    val estadoFiltro: StateFlow<String> = _estadoFiltro.asStateFlow()

    private var todasLasCitas: List<Cita> = emptyList()

    val limiteAlcanzado: StateFlow<Boolean> = _uiState.map { estado ->
        if (estado is CitasUiState.Success) {
            estado.citas.size >= 3
        } else {
            false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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

    // RF-02: Función para cambiar la pestaña de estado
    fun setEstadoFiltro(nuevoEstado: String) {
        _estadoFiltro.value = nuevoEstado
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val activadoHoy = _mostrarSoloHoy.value
        val estadoActual = _estadoFiltro.value

        var listaTemporal = todasLasCitas

        // 1er Filtro (RF-02): Por Estado (Programada, Atendida, Cancelada)
        if (estadoActual != "Todas") {
            listaTemporal = listaTemporal.filter {
                it.estado.toString().equals(estadoActual, ignoreCase = true)
            }
        }

        // 2do Filtro (SC-A): Por Hoy
        if (activadoHoy) {
            listaTemporal = listaTemporal.filter { it.fecha.contains("23") || it.fecha.contains("Hoy") }
        }

        // RF-02: Ordenamiento (De la más próxima a la más lejana)
        // Se asume que el formato de fecha (YYYY-MM-DD) y hora (HH:MM) permite orden alfabético
        listaTemporal = listaTemporal.sortedWith(compareBy({ it.fecha }, { it.hora }))

        if (listaTemporal.isEmpty()) {
            _uiState.value = CitasUiState.Empty
        } else {
            _uiState.value = CitasUiState.Success(listaTemporal)
        }
    }
}