package pe.edu.upeu.andinasalud.presentation.detalle

import pe.edu.upeu.andinasalud.domain.model.Cita

sealed interface DetalleCitaUiState {
    data object Loading : DetalleCitaUiState
    data class Success(
        val cita: Cita,
        val puedeCancelar: Boolean,            // RN-03, decidido en el dominio
        val motivoNoCancelable: String?        // texto de la regla cuando no se puede
    ) : DetalleCitaUiState
    data class Error(val message: String) : DetalleCitaUiState
}
