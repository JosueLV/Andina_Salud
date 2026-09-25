package pe.edu.upeu.andinasalud.domain.model

data class Cita(
    val id: Int = 0,
    val especialidad: String,
    val medico: String,
    val fecha: String, // AAAA-MM-DD
    val hora: String,  // HH:MM
    val sede: String,
    val modalidad: String = "Presencial",
    val estado: EstadoCita = EstadoCita.Programada(recordatorioActivo = true),
    // SC-D: registro de cambios (reprogramaciones) que se muestra en el detalle
    val historial: List<String> = emptyList()
)
