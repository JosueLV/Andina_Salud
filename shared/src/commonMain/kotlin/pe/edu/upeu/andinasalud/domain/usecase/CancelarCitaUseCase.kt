package pe.edu.upeu.andinasalud.domain.usecase

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.ReglaNegocioException
import pe.edu.upeu.andinasalud.domain.repository.CitaRepository
import pe.edu.upeu.andinasalud.domain.repository.Reloj
import pe.edu.upeu.andinasalud.domain.rules.ReglasCita

class CancelarCitaUseCase(
    private val repository: CitaRepository,
    private val reloj: Reloj
) {
    suspend operator fun invoke(citaId: Int): Result<Cita> {
        val cita = repository.obtenerCita(citaId)
            ?: return Result.failure(ReglaNegocioException("La cita no existe"))

        // RN-03
        val motivo = ReglasCita.motivoNoCancelable(cita, reloj.ahora())
        if (motivo != null) return Result.failure(ReglaNegocioException(motivo))

        val cancelada = cita.copy(
            estado = EstadoCita.Cancelada(
                motivo = "Cancelada por el paciente",
                canceladaPorPaciente = true
            )
        )
        repository.actualizar(cancelada)
        return Result.success(cancelada)
    }
}
