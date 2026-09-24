package pe.edu.upeu.andinasalud.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.Medico

/**
 * Contrato de datos. Hoy lo implementa CitaRepositoryFake (memoria);
 * cuando exista la API basta con crear otra implementacion y cambiar una linea en AppModule.
 */
interface CitaRepository {
    /** Flujo con retardo de carga simulado; vuelve a emitir cuando la lista cambia. */
    fun obtenerCitas(): Flow<List<Cita>>

    suspend fun obtenerCitasActuales(): List<Cita>
    suspend fun obtenerCita(id: Int): Cita?
    suspend fun obtenerMedicos(): List<Medico>
    suspend fun agregar(cita: Cita): Cita
    suspend fun actualizar(cita: Cita)
}
