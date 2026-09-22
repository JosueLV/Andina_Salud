package pe.edu.upeu.andinasalud.domain.model

data class Paciente(
    val id: String,
    val nombre: String,
    val documento: String,
    val correo: String
)