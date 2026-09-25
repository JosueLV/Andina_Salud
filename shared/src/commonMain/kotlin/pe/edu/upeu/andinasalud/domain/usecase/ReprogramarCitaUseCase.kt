package pe.edu.upeu.andinasalud.domain.usecase

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.ReglaNegocioException
import pe.edu.upeu.andinasalud.domain.model.SolicitudInvalidaException
import pe.edu.upeu.andinasalud.domain.repository.CitaRepository
import pe.edu.upeu.andinasalud.domain.repository.Reloj
import pe.edu.upeu.andinasalud.domain.rules.ReglasCita

/** SC-D: reutiliza las mismas reglas (RN-01, RN-04, RN-05) que la solicitud, sin duplicarlas. */
class ReprogramarCitaUseCase(
    private val repository: CitaRepository,
    private val reloj: Reloj
) {
    suspend operator fun invoke(
        citaId: Int,
        nuevaFecha: String,
        nuevaHora: String,
        motivo: String
    ): Result<Cita> {
        val cita = repository.obtenerCita(citaId)
            ?: return Result.failure(ReglaNegocioException("La cita no existe"))

        val errores = ReglasCita.validarReprogramacion(
            cita = cita,
            nuevaFecha = nuevaFecha,
            nuevaHora = nuevaHora,
            motivo = motivo,
            citas = repository.obtenerCitasActuales(),
            ahora = reloj.ahora()
        )
        if (errores.hayErrores) return Result.failure(SolicitudInvalidaException(errores))

        val registro = "Reprogramada de ${cita.fecha} ${cita.hora} a ${nuevaFecha.trim()} ${nuevaHora.trim()}: ${motivo.trim()}"
        val reprogramada = cita.copy(
            fecha = nuevaFecha.trim(),
            hora = nuevaHora.trim(),
            historial = cita.historial + registro
        )
        repository.actualizar(reprogramada)
        return Result.success(reprogramada)
    }
}
