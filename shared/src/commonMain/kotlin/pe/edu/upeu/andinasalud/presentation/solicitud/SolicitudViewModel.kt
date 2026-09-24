package pe.edu.upeu.andinasalud.presentation.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.andinasalud.domain.model.ErroresSolicitud
import pe.edu.upeu.andinasalud.domain.model.SolicitudCita
import pe.edu.upeu.andinasalud.domain.model.SolicitudInvalidaException
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerOpcionesSolicitudUseCase
import pe.edu.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase

class SolicitudViewModel(
    private val solicitarCitaUseCase: SolicitarCitaUseCase,
    private val obtenerOpcionesUseCase: ObtenerOpcionesSolicitudUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val opciones = obtenerOpcionesUseCase()
            _uiState.update {
                it.copy(
                    opcionesEspecialidad = opciones.especialidades,
                    opcionesSede = opciones.sedes
                )
            }
        }
    }

    // Al editar un campo se borra solo su error; el resto se conserva hasta el siguiente envio.
    fun onEspecialidadChange(valor: String) = _uiState.update {
        it.copy(especialidad = valor, errores = it.errores.copy(especialidad = null, sede = null, general = null))
    }

    fun onSedeChange(valor: String) = _uiState.update {
        it.copy(sede = valor, errores = it.errores.copy(sede = null, general = null))
    }

    fun onFechaChange(valor: String) = _uiState.update {
        it.copy(fecha = valor, errores = it.errores.copy(fecha = null, hora = null, general = null))
    }

    fun onHoraChange(valor: String) = _uiState.update {
        it.copy(hora = valor, errores = it.errores.copy(hora = null, general = null))
    }

    fun onMotivoChange(valor: String) = _uiState.update {
        it.copy(motivo = valor, errores = it.errores.copy(motivo = null, general = null))
    }

    fun onModalidadChange(valor: String) = _uiState.update { it.copy(modalidad = valor) }

    fun registrar() {
        val actual = _uiState.value
        if (actual.enviando) return
        _uiState.update { it.copy(enviando = true) }

        viewModelScope.launch {
            val resultado = solicitarCitaUseCase(
                SolicitudCita(
                    especialidad = actual.especialidad,
                    sede = actual.sede,
                    fecha = actual.fecha,
                    hora = actual.hora,
                    motivo = actual.motivo,
                    modalidad = actual.modalidad
                )
            )
            resultado
                .onSuccess {
                    _uiState.update { s -> s.copy(enviando = false, registrada = true, errores = ErroresSolicitud()) }
                }
                .onFailure { e ->
                    val errores = (e as? SolicitudInvalidaException)?.errores
                        ?: ErroresSolicitud(general = e.message ?: "No se pudo registrar la cita")
                    _uiState.update { s -> s.copy(enviando = false, errores = errores) }
                }
        }
    }

    fun registroMostrado() = _uiState.update { it.copy(registrada = false) }
}
