package pe.edu.upeu.andinasalud.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerCitasUseCase(private val repository: CitaRepository) {
    operator fun invoke(): Flow<List<Cita>> {
        return repository.obtenerCitas()
    }
}