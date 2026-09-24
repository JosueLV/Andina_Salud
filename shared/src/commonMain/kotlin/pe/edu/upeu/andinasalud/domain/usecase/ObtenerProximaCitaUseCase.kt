package pe.edu.upeu.andinasalud.domain.usecase

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.FechaHora

/** RF-01: la cita Programada mas cercana que todavia no ocurre. */
class ObtenerProximaCitaUseCase {
    operator fun invoke(citas: List<Cita>, ahora: FechaHora): Cita? =
        citas
            .filter { it.estado is EstadoCita.Programada }
            .mapNotNull { cita -> FechaHora.desde(cita.fecha, cita.hora)?.let { cita to it.enMinutos() } }
            .filter { it.second >= ahora.enMinutos() }
            .minByOrNull { it.second }
            ?.first
}
