package pe.edu.upeu.andinasalud.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import pe.edu.upeu.andinasalud.data.local.CitasSimuladas
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.Medico
import pe.edu.upeu.andinasalud.domain.model.Paciente
import pe.edu.upeu.andinasalud.domain.repository.CitaRepository

class CitaRepositoryFake : CitaRepository {

    private val citas = MutableStateFlow(CitasSimuladas.citas)

    /** Ponlo en true solo para capturar el estado de error (RF-08); luego vuelve a false. */
    var simularError: Boolean = false

    override fun obtenerCitas(): Flow<List<Cita>> = citas.onStart {
        delay(RETARDO_CARGA_MS)
        if (simularError) throw IllegalStateException("No se pudieron cargar las citas (error simulado)")
    }

    override suspend fun obtenerCitasActuales(): List<Cita> = citas.value

    override suspend fun obtenerCita(id: Int): Cita? = citas.value.firstOrNull { it.id == id }

    override suspend fun obtenerMedicos(): List<Medico> = CitasSimuladas.medicos

    override suspend fun obtenerPaciente(): Paciente = CitasSimuladas.paciente

    override suspend fun agregar(cita: Cita): Cita {
        val nuevoId = (citas.value.maxOfOrNull { it.id } ?: 0) + 1
        val nueva = cita.copy(id = nuevoId)
        citas.update { it + nueva }
        return nueva
    }

    override suspend fun actualizar(cita: Cita) {
        citas.update { lista -> lista.map { if (it.id == cita.id) cita else it } }
    }

    private companion object {
        const val RETARDO_CARGA_MS = 800L // RF-08
    }
}
