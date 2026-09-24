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

    private val _estadoFiltro = MutableStateFlow("Todas")
    val estadoFiltro: StateFlow<String> = _estadoFiltro.asStateFlow()

    // RF-05: Estado para el texto de búsqueda
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private var todasLasCitas: List<Cita> = emptyList()

    val limiteAlcanzado: StateFlow<Boolean> = _uiState.map { estado ->
        if (estado is CitasUiState.Success) estado.citas.size >= 3 else false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val cantidadProgramadas: StateFlow<Int> = _uiState.map { estado ->
        if (estado is CitasUiState.Success) estado.citas.size else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init { cargarCitas() }

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

    fun setEstadoFiltro(nuevoEstado: String) {
        _estadoFiltro.value = nuevoEstado
        aplicarFiltro()
    }

    // RF-05: Actualiza el texto y aplica el filtro
    fun setTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
        aplicarFiltro()
    }

    // Función auxiliar para quitar tildes según RF-05
    private fun String.quitarTildes(): String {
        return this.lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
    }

    private fun aplicarFiltro() {
        val activadoHoy = _mostrarSoloHoy.value
        val estadoActual = _estadoFiltro.value
        val busqueda = _textoBusqueda.value.quitarTildes()

        var listaTemporal = todasLasCitas

        // 1. Filtro por Búsqueda (RF-05)
        if (busqueda.isNotBlank()) {
            listaTemporal = listaTemporal.filter {
                it.especialidad.quitarTildes().contains(busqueda) ||
                        it.medico.quitarTildes().contains(busqueda)
            }
        }

        // 2. Filtro por Estado (RF-02)
        if (estadoActual != "Todas") {
            listaTemporal = listaTemporal.filter {
                it.estado.toString().equals(estadoActual, ignoreCase = true)
            }
        }

        // 3. Filtro por Hoy (SC-A)
        if (activadoHoy) {
            listaTemporal = listaTemporal.filter { it.fecha.contains("23") || it.fecha.contains("Hoy") }
        }

        // 4. Ordenamiento (RF-02)
        listaTemporal = listaTemporal.sortedWith(compareBy({ it.fecha }, { it.hora }))

        if (listaTemporal.isEmpty()) {
            _uiState.value = CitasUiState.Empty
        } else {
            _uiState.value = CitasUiState.Success(listaTemporal)
        }
    }
}