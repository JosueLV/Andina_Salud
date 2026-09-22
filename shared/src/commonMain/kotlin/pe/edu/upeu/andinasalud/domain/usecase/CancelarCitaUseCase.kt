package pe.edu.upeu.andinasalud.domain.usecase

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.EstadoCita

class CancelarCitaUseCase {
    operator fun invoke(cita: Cita): Result<Boolean> {
        // RN-03: Solo puede cancelarse si está Programada
        if (cita.estado !is EstadoCita.Programada) {
            return Result.failure(Exception("Solo se pueden cancelar citas programadas"))
        }
        return Result.success(true)
    }
}