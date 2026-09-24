package pe.edu.upeu.andinasalud.data.local

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.Paciente
import pe.edu.upeu.andinasalud.domain.model.EstadoCita

object CitasSimuladas {
    val paciente = Paciente(
        id = "P-0417",
        nombre = "Lucia Quispe Mamani",
        documento = "70154823",
        correo = "lucia.quispe@correo.pe",
        telefono = "987654321"
    )

    val sedes = listOf("Ñaña", "Chosica", "Chaclacayo", "Santa Anita")

    val especialidades = listOf(
        "Medicina General", "Odontología", "Pediatría", "Nutrición", "Psicología"
    )

    val medicos = listOf(
        "Dr. Iván Rojas", "Dra. Ana López",
        "Dra. Rosa Flores", "Dr. Carlos Ruiz",
        "Dra. Carla Núñez", "Dr. Luis Gómez",
        "Lic. Ana Bermúdez", "Lic. Pedro Lara",
        "Ps. Luis Tapia", "Ps. Carmen Soto"
    )

    val citas = listOf(
        // --- 3 PROGRAMADAS ---
        Cita(
            id = 1,
            especialidad = "Medicina General",
            medico = "Dr. Iván Rojas",
            fecha = "2026-10-18",
            hora = "09:00",
            sede = "Ñaña",
            modalidad = "Presencial",
            estado = EstadoCita.Programada(recordatorioActivo = true) // Corregido con parámetro
        ),
        Cita(
            id = 2,
            especialidad = "Odontología",
            medico = "Dra. Rosa Flores",
            fecha = "2026-10-21",
            hora = "16:30",
            sede = "Chosica",
            modalidad = "Teleconsulta",
            estado = EstadoCita.Programada(recordatorioActivo = true)
        ),
        Cita(
            id = 3,
            especialidad = "Nutrición",
            medico = "Lic. Ana Bermúdez",
            fecha = "2026-10-25",
            hora = "11:15",
            sede = "Santa Anita",
            modalidad = "Presencial",
            estado = EstadoCita.Programada(recordatorioActivo = true)
        ),

        // --- 2 ATENDIDAS ---
        Cita(
            id = 4,
            especialidad = "Pediatría",
            medico = "Dra. Carla Núñez",
            fecha = "2026-08-30",
            hora = "08:45",
            sede = "Chaclacayo",
            modalidad = "Teleconsulta",
            estado = EstadoCita.Atendida(indicaciones = "Paciente estable, continuar con la medicación.")
        ),
        Cita(
            id = 5,
            especialidad = "Psicología",
            medico = "Ps. Luis Tapia",
            fecha = "2026-09-02",
            hora = "15:00",
            sede = "Ñaña",
            modalidad = "Presencial",
            estado = EstadoCita.Atendida(indicaciones = "Asistir a la próxima sesión presencial.")
        ),

        // --- 1 CANCELADA ---
        Cita(
            id = 6,
            especialidad = "Medicina General",
            medico = "Dr. Iván Rojas",
            fecha = "2026-09-05",
            hora = "10:30",
            sede = "Chosica",
            modalidad = "Teleconsulta",
            estado = EstadoCita.Cancelada(
                motivo = "Cruce de horarios en el trabajo",
                canceladaPorPaciente = true
            )
        )
    )
}