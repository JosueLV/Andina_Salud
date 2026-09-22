package pe.edu.upeu.andinasalud.data.local

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.Paciente

object CitasSimuladas {
    val paciente = Paciente(
        id = "P-0417",
        nombre = "Lucia Quispe Mamani",
        documento = "70154823",
        correo = "lucia.quispe@correo.pe"
    )

    val sedes = listOf("Ñaña", "Chosica", "Chaclacayo", "Santa Anita")

    val especialidades = listOf(
        "Medicina General", "Odontología", "Pediatría", "Nutrición", "Psicología"
    )

    val citas = listOf(
        Cita(
            1,
            "Medicina General",
            "Dr. Iván Rojas",
            "Ñaña",
            "2026-09-18",
            "09:00",
            EstadoCita.Programada(true)
        ),
        Cita(2, "Odontología", "Dra. Rosa Flores", "Chosica", "2026-09-21", "16:30", EstadoCita.Programada(false)),
        Cita(3, "Nutrición", "Lic. Ana Bermúdez", "Santa Anita", "2026-09-25", "11:15", EstadoCita.Programada(true)),
        Cita(4, "Pediatría", "Dra. Carla Núñez", "Chaclacayo", "2026-08-30", "08:45", EstadoCita.Atendida("Control en tres meses")),
        Cita(5, "Psicología", "Ps. Luis Tapia", "Ñaña", "2026-09-02", "15:00", EstadoCita.Atendida("Continuar sesiones quincenales")),
        Cita(6, "Medicina General", "Dr. Iván Rojas", "Chosica", "2026-09-05", "10:30", EstadoCita.Cancelada("Viaje del paciente", true))
    )
}