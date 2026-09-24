package pe.edu.upeu.andinasalud.presentation.citas

import pe.edu.upeu.andinasalud.domain.model.Cita

/** Lo que la pantalla necesita dibujar por cada cita (el dominio no sabe de "badges"). */
data class CitaItemUi(
    val cita: Cita,
    val esHoy: Boolean
)

sealed interface CitasUiState {
    data object Loading : CitasUiState                       // carga (800 ms)
    data class Success(val items: List<CitaItemUi>) : CitasUiState // contenido
    data object Empty : CitasUiState                         // lista vacia
    data class Error(val message: String) : CitasUiState     // error
}
