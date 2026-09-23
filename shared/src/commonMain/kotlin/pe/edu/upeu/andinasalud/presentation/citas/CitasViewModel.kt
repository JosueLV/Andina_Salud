package pe.edu.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class CitasViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CitasUiState>(CitasUiState.Loading)
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()

    // Estado para saber si el filtro "Hoy" está activo
    private val _filtroHoyActivo = MutableStateFlow(false)
    val filtroHoyActivo: StateFlow<Boolean> = _filtroHoyActivo.asStateFlow()

    private var todasLasCitas: List<Cita> = emptyList()

    init {
        cargarCitas()
    }

    private fun cargarCitas() {
        viewModelScope.launch {
            _uiState.value = CitasUiState.Loading
            try {
                // Como es un Flow, debemos usar "collect" para recibir la lista
                obtenerCitasUseCase().collect { lista ->
                    todasLasCitas = lista
                    aplicarFiltro() // Aplicamos el filtro cada vez que llegan datos
                }
            } catch (e: Exception) {
                _uiState.value = CitasUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun toggleFiltroHoy() {
        _filtroHoyActivo.value = !_filtroHoyActivo.value
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val activado = _filtroHoyActivo.value

        val listaFiltrada = if (activado) {
            // Filtra buscando "22" (fecha de hoy) o la palabra "Hoy"
            todasLasCitas.filter { it.fecha.contains("22") || it.fecha.contains("Hoy") }
        } else {
            todasLasCitas
        }

        if (listaFiltrada.isEmpty()) {
            _uiState.value = CitasUiState.Empty
        } else {
            _uiState.value = CitasUiState.Success(listaFiltrada)
        }
    }
    // RN-02: Validar si el paciente llegó al límite de 3 citas
    fun alcanzoLimiteCitas(estado: CitasUiState): Boolean {
        return if (estado is CitasUiState.Success) {
            estado.citas.size >= 3
        } else {
            false
        }
    }
}