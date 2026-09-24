package pe.edu.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.CriteriosCitas
import pe.edu.upeu.andinasalud.domain.model.FiltroEstado
import pe.edu.upeu.andinasalud.domain.rules.ReglasCita
import pe.edu.upeu.andinasalud.domain.usecase.FiltrarCitasUseCase
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import kotlin.coroutines.cancellation.CancellationException

class CitasViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase,
    private val filtrarCitasUseCase: FiltrarCitasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CitasUiState>(CitasUiState.Loading)
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()

    private val _criterios = MutableStateFlow(CriteriosCitas())
    val criterios: StateFlow<CriteriosCitas> = _criterios.asStateFlow()

    // SC-B: ambos valores salen de la regla RN-02 del dominio sobre TODAS las citas (no sobre las filtradas)
    private val _cantidadProgramadas = MutableStateFlow(0)
    val cantidadProgramadas: StateFlow<Int> = _cantidadProgramadas.asStateFlow()

    private val _limiteAlcanzado = MutableStateFlow(false)
    val limiteAlcanzado: StateFlow<Boolean> = _limiteAlcanzado.asStateFlow()

    private var todasLasCitas: List<Cita> = emptyList()
    private var cargado = false
    private var cargaJob: Job? = null

    init {
        cargarCitas()
    }

    /** La corrutina vive en viewModelScope: si la pantalla se destruye, se cancela sola. */
    fun cargarCitas() {
        cargaJob?.cancel()
        cargado = false
        cargaJob = viewModelScope.launch {
            _uiState.value = CitasUiState.Loading
            try {
                obtenerCitasUseCase().collect { lista ->
                    todasLasCitas = lista
                    _cantidadProgramadas.value = ReglasCita.cantidadProgramadas(lista)
                    _limiteAlcanzado.value = ReglasCita.limiteAlcanzado(lista)
                    cargado = true
                    aplicarFiltros()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = CitasUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun setEstadoFiltro(estado: FiltroEstado) {
        _criterios.update { it.copy(estado = estado) }
        aplicarFiltros()
    }

    fun toggleFiltroHoy() {
        _criterios.update { it.copy(soloHoy = !it.soloHoy) }
        aplicarFiltros()
    }

    fun setTextoBusqueda(texto: String) {
        _criterios.update { it.copy(busqueda = texto) }
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        if (!cargado) return
        val filtradas = filtrarCitasUseCase(todasLasCitas, _criterios.value)
        _uiState.value = if (filtradas.isEmpty()) {
            CitasUiState.Empty
        } else {
            CitasUiState.Success(filtradas.map { CitaItemUi(it, filtrarCitasUseCase.esHoy(it)) })
        }
    }
}
