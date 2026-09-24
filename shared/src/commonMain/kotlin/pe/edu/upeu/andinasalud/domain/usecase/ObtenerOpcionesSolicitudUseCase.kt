package pe.edu.upeu.andinasalud.domain.usecase

import pe.edu.upeu.andinasalud.domain.repository.CitaRepository

data class OpcionesSolicitud(
    val especialidades: List<String>,
    val sedes: List<String>
)

/** Opciones del formulario de solicitud (RF-04), tomadas de los medicos disponibles. */
class ObtenerOpcionesSolicitudUseCase(private val repository: CitaRepository) {
    suspend operator fun invoke(): OpcionesSolicitud {
        val medicos = repository.obtenerMedicos()
        return OpcionesSolicitud(
            especialidades = medicos.map { it.especialidad }.distinct(),
            sedes = medicos.flatMap { it.sedes }.distinct()
        )
    }
}
