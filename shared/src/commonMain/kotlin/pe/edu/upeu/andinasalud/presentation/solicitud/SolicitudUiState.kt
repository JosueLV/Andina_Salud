package pe.edu.upeu.andinasalud.presentation.solicitud

import pe.edu.upeu.andinasalud.domain.model.ErroresSolicitud

data class SolicitudUiState(
    val especialidad: String = "",
    val sede: String = "",
    val fecha: String = "",
    val hora: String = "",
    val motivo: String = "",
    val modalidad: String = MODALIDADES.first(),
    val opcionesEspecialidad: List<String> = emptyList(),
    val opcionesSede: List<String> = emptyList(),
    val errores: ErroresSolicitud = ErroresSolicitud(), // un mensaje por campo (RF-04)
    val enviando: Boolean = false,
    val registrada: Boolean = false
) {
    companion object {
        val MODALIDADES = listOf("Presencial", "Teleconsulta")
    }
}
