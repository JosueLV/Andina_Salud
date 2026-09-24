package pe.edu.upeu.andinasalud.presentation.inicio

import pe.edu.upeu.andinasalud.domain.model.Cita

sealed interface InicioUiState {
    data object Loading : InicioUiState
    data class Success(
        val primerNombre: String,
        val proximaCita: Cita?,
        val cantidadProgramadas: Int,
        val limiteAlcanzado: Boolean   // RN-02, leida del dominio (SC-B)
    ) : InicioUiState
    data class Error(val message: String) : InicioUiState
}
