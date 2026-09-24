package pe.edu.upeu.andinasalud.presentation.perfil

import pe.edu.upeu.andinasalud.domain.model.Paciente

sealed interface PerfilUiState {
    data object Loading : PerfilUiState
    data class Success(val paciente: Paciente) : PerfilUiState
    data class Error(val message: String) : PerfilUiState
}
