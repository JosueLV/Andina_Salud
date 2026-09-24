package pe.edu.upeu.andinasalud.domain.usecase

import pe.edu.upeu.andinasalud.domain.model.Paciente
import pe.edu.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerPacienteUseCase(private val repository: CitaRepository) {
    suspend operator fun invoke(): Paciente = repository.obtenerPaciente()
}
