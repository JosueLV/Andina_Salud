package pe.edu.upeu.andinasalud.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pe.edu.upeu.andinasalud.data.local.CitasSimuladas
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.repository.CitaRepository

class CitaRepositoryFake : CitaRepository {
    override fun obtenerCitas(): Flow<List<Cita>> = flow {
        delay(1500) // Retardo simulado pedido en el examen
        emit(CitasSimuladas.citas)
    }
}