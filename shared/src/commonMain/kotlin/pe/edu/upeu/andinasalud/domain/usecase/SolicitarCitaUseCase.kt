package pe.edu.upeu.andinasalud.domain.usecase

class SolicitarCitaUseCase {
    operator fun invoke(motivo: String): Result<Boolean> {
        // RN-04: El motivo de la consulta debe tener entre diez y doscientos caracteres.
        if (motivo.length !in 10..200) {
            return Result.failure(Exception("El motivo debe tener entre 10 y 200 caracteres"))
        }

        // El retorno simulado de éxito
        return Result.success(true)
    }
}