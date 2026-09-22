package pe.edu.upeu.andinasalud.domain.model

data class Cita(
    val id: Int,
    val especialidad: String,
    val medico: String,
    val sede: String,
    val fecha: String,
    val hora: String,
    val estado: EstadoCita
)