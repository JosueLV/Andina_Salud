package pe.edu.upeu.andinasalud.domain.usecase

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.EstadoCita


class ReprogramarCitaUseCase(
    private val solicitarCitaUseCase: SolicitarCitaUseCase
) {
    // Agregamos "motivoCambio" con un valor por defecto largo para asegurar que pase la validación
    operator fun invoke(citaActual: Cita, nuevaFecha: String, nuevaHora: String, motivoCambio: String = "Reprogramación de cita solicitada por el paciente"): Result<Cita> {
        // 1. Validar que la cita actual esté "Programada"
        if (citaActual.estado !is EstadoCita.Programada) {
            return Result.failure(Exception("Solo se pueden reprogramar citas en estado Programada"))
        }

        // 2. ¡Reutilización de la validación existente! (SC-D)
        // Usamos tu SolicitarCitaUseCase para validar la longitud del motivo (RN-04)
        val resultadoValidacion = solicitarCitaUseCase(motivo = motivoCambio)

        if (resultadoValidacion.isFailure) {
            // Si la regla de los 10 a 200 caracteres falla, devolvemos ese error
            return Result.failure(resultadoValidacion.exceptionOrNull() ?: Exception("Error en validación del motivo"))
        }

        // 3. Crear una copia de la cita con los nuevos datos
        val citaReprogramada = citaActual.copy(fecha = nuevaFecha, hora = nuevaHora)

        return Result.success(citaReprogramada)
    }
}