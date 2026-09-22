package pe.edu.upeu.andinasalud.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.andinasalud.domain.model.Cita


interface CitaRepository {
    fun obtenerCitas(): Flow<List<Cita>>
}