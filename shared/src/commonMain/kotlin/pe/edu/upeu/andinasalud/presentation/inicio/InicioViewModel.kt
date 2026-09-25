package pe.edu.upeu.andinasalud.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.andinasalud.domain.repository.Reloj
import pe.edu.upeu.andinasalud.domain.rules.ReglasCita
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerProximaCitaUseCase
import kotlin.coroutines.cancellation.CancellationException

/** Alimenta la pantalla de inicio (RF-01) y el indicador de la barra inferior (SC-B). */
class InicioViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase,
    private val obtenerPacienteUseCase: ObtenerPacienteUseCase,
    private val obtenerProximaCitaUseCase: ObtenerProximaCitaUseCase,
    private val reloj: Reloj
) : ViewModel() {

    private val _uiState = MutableStateFlow<InicioUiState>(InicioUiState.Loading)
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    private var cargaJob: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            _uiState.value = InicioUiState.Loading
            try {
                val paciente = obtenerPacienteUseCase()
                obtenerCitasUseCase().collect { citas ->
                    _uiState.value = InicioUiState.Success(
                        primerNombre = paciente.nombre.trim().substringBefore(" "),
                        proximaCita = obtenerProximaCitaUseCase(citas, reloj.ahora()),
                        cantidadProgramadas = ReglasCita.cantidadProgramadas(citas),
                        limiteAlcanzado = ReglasCita.limiteAlcanzado(citas)
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = InicioUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
