package pe.edu.upeu.andinasalud.presentation.citas

import pe.edu.upeu.andinasalud.domain.model.Cita

sealed interface CitasUiState {
    data object Loading : CitasUiState // Estado de carga (los 800ms)
    data class Success(val citas: List<Cita>) : CitasUiState // Contenido
    data object Empty : CitasUiState // Lista vacía
    data class Error(val message: String) : CitasUiState // Error
}