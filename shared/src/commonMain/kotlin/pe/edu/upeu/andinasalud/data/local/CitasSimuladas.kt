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
            especialidad = "Medicina General",
            medico = "Dr. Iván Rojas",
            fecha = "2026-09-18",
            hora = "09:00",
            sede = "Ñaña",
            modalidad = "Presencial"
        ),
        Cita(
            especialidad = "Odontología",
            medico = "Dra. Rosa Flores",
            fecha = "2026-09-21",
            hora = "16:30",
            sede = "Chosica",
            modalidad = "Teleconsulta"
        ),
        Cita(
            especialidad = "Nutrición",
            medico = "Lic. Ana Bermúdez",
            fecha = "2026-09-25",
            hora = "11:15",
            sede = "Santa Anita",
            modalidad = "Presencial"
        ),
        Cita(
            especialidad = "Pediatría",
            medico = "Dra. Carla Núñez",
            fecha = "2026-08-30",
            hora = "08:45",
            sede = "Chaclacayo",
            modalidad = "Teleconsulta"
        ),
        Cita(
            especialidad = "Psicología",
            medico = "Ps. Luis Tapia",
            fecha = "2026-09-02",
            hora = "15:00",
            sede = "Ñaña",
            modalidad = "Presencial"
        ),
        Cita(
            especialidad = "Medicina General",
            medico = "Dr. Iván Rojas",
            fecha = "2026-09-05",
            hora = "10:30",
            sede = "Chosica",
            modalidad = "Teleconsulta"
        )
    )
}