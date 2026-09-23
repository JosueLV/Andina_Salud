package pe.edu.upeu.andinasalud.domain.model

data class Cita(
    val id: Int = 0,
    val especialidad: String,
    val medico: String,
    val fecha: String,
    val hora: String,
    val sede: String,
    val modalidad: String,
    // Restauramos el estado con un valor por defecto para que el caso de uso compile
    val estado: EstadoCita = EstadoCita.Programada(recordatorioActivo = true)
)