package pe.edu.upeu.andinasalud.data.local

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.Medico
import pe.edu.upeu.andinasalud.domain.model.Paciente

object CitasSimuladas {
    val paciente = Paciente(
        id = "P-0417",
        nombre = "Lucía Quispe Mamani",
        documento = "70154823",
        correo = "lucia.quispe@correo.pe",
        telefono = "987654321"
    )

    val sedes = listOf("Ñaña", "Chosica", "Chaclacayo", "Santa Anita")

    val especialidades = listOf(
        "Medicina General", "Odontología", "Pediatría", "Nutrición", "Psicología"
    )

    // Al menos dos médicos por especialidad, cada uno en una o más sedes
    val medicos = listOf(
        Medico("M-01", "Dr. Iván Rojas", "Medicina General", listOf("Ñaña", "Chosica")),
        Medico("M-02", "Dra. Ana López", "Medicina General", listOf("Chaclacayo", "Santa Anita")),
        Medico("M-03", "Dra. Rosa Flores", "Odontología", listOf("Chosica", "Ñaña")),
        Medico("M-04", "Dr. Carlos Ruiz", "Odontología", listOf("Santa Anita")),
        Medico("M-05", "Dra. Carla Núñez", "Pediatría", listOf("Chaclacayo", "Ñaña")),
        Medico("M-06", "Dr. Luis Gómez", "Pediatría", listOf("Chosica", "Santa Anita")),
        Medico("M-07", "Lic. Ana Bermúdez", "Nutrición", listOf("Santa Anita", "Chaclacayo")),
        Medico("M-08", "Lic. Pedro Lara", "Nutrición", listOf("Ñaña", "Chosica")),
        Medico("M-09", "Ps. Luis Tapia", "Psicología", listOf("Ñaña", "Santa Anita")),
        Medico("M-10", "Ps. Carmen Soto", "Psicología", listOf("Chosica", "Chaclacayo"))
    )

    val citas = listOf(
        // --- 3 PROGRAMADAS (fechas futuras respecto al dia del examen) ---
        Cita(
            id = 1, especialidad = "Medicina General", medico = "Dr. Iván Rojas",
            fecha = "2026-10-18", hora = "09:00", sede = "Ñaña", modalidad = "Presencial",
            estado = EstadoCita.Programada(recordatorioActivo = true)
        ),
        Cita(
            id = 2, especialidad = "Odontología", medico = "Dra. Rosa Flores",
            fecha = "2026-10-21", hora = "16:30", sede = "Chosica", modalidad = "Teleconsulta",
            estado = EstadoCita.Programada(recordatorioActivo = false)
        ),
        Cita(
            id = 3, especialidad = "Nutrición", medico = "Lic. Ana Bermúdez",
            fecha = "2026-10-25", hora = "11:15", sede = "Santa Anita", modalidad = "Presencial",
            estado = EstadoCita.Programada(recordatorioActivo = true)
        ),
        // --- 2 ATENDIDAS ---
        Cita(
            id = 4, especialidad = "Pediatría", medico = "Dra. Carla Núñez",
            fecha = "2026-08-30", hora = "08:45", sede = "Chaclacayo", modalidad = "Teleconsulta",
            estado = EstadoCita.Atendida(indicaciones = "Control en tres meses")
        ),
        Cita(
            id = 5, especialidad = "Psicología", medico = "Ps. Luis Tapia",
            fecha = "2026-09-02", hora = "15:00", sede = "Ñaña", modalidad = "Presencial",
            estado = EstadoCita.Atendida(indicaciones = "Continuar sesiones quincenales")
        ),
        // --- 1 CANCELADA ---
        Cita(
            id = 6, especialidad = "Medicina General", medico = "Dr. Iván Rojas",
            fecha = "2026-09-05", hora = "10:30", sede = "Chosica", modalidad = "Teleconsulta",
            estado = EstadoCita.Cancelada(motivo = "Viaje del paciente", canceladaPorPaciente = true)
        )
    )
}
