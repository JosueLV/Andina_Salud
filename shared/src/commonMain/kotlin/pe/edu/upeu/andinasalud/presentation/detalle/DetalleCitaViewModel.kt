package pe.edu.upeu.andinasalud.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.andinasalud.domain.repository.Reloj
import pe.edu.upeu.andinasalud.domain.rules.ReglasCita
import pe.edu.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import kotlin.coroutines.cancellation.CancellationException

class DetalleCitaViewModel(
    private val citaId: Int,
    private val obtenerCitasUseCase: ObtenerCitasUseCase,
    private val cancelarCitaUseCase: CancelarCitaUseCase,
    private val reloj: Reloj
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleCitaUiState>(DetalleCitaUiState.Loading)
    val uiState: StateFlow<DetalleCitaUiState> = _uiState.asStateFlow()

    /** Mensaje de una sola vez (por ejemplo "Cita cancelada"); la pantalla lo muestra y lo limpia. */
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private var cargaJob: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            _uiState.value = DetalleCitaUiState.Loading
            try {
                // Se observa la lista: al cancelar, el repositorio re-emite y el detalle se actualiza solo
                obtenerCitasUseCase().collect { citas ->
                    val cita = citas.firstOrNull { it.id == citaId }
                    _uiState.value = if (cita == null) {
                        DetalleCitaUiState.Error("La cita no existe")
                    } else {
                        val ahora = reloj.ahora()
                        DetalleCitaUiState.Success(
                            cita = cita,
                            puedeCancelar = ReglasCita.puedeCancelar(cita, ahora),
                            motivoNoCancelable = ReglasCita.motivoNoCancelable(cita, ahora)
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = DetalleCitaUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun cancelarCita() {
        viewModelScope.launch {
            cancelarCitaUseCase(citaId)
                .onSuccess { _mensaje.value = "La cita fue cancelada" }
                .onFailure { _mensaje.value = it.message ?: "No se pudo cancelar la cita" }
        }
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }
}
