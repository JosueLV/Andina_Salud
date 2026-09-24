package pe.edu.upeu.andinasalud.domain.usecase

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.CriteriosCitas
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.FechaHora
import pe.edu.upeu.andinasalud.domain.model.FiltroEstado
import pe.edu.upeu.andinasalud.domain.repository.Reloj

/**
 * Filtra y ordena la lista. Es el lugar donde se resuelve el chip "Hoy" (SC-A),
 * combinado con el filtro de estado y la busqueda. El composable no filtra nada.
 */
class FiltrarCitasUseCase(private val reloj: Reloj) {

    operator fun invoke(citas: List<Cita>, criterios: CriteriosCitas): List<Cita> {
        val ahoraMinutos = reloj.ahora().enMinutos()
        val termino = normalizar(criterios.busqueda)

        return citas
            .filter { coincideEstado(it, criterios.estado) }
            .filter { !criterios.soloHoy || esHoy(it) }
            .filter {
                termino.isBlank() ||
                    normalizar(it.especialidad).contains(termino) ||
                    normalizar(it.medico).contains(termino)
            }
            .sortedWith(compareBy<Cita>({ orden(it, ahoraMinutos).first }, { orden(it, ahoraMinutos).second }))
    }

    fun esHoy(cita: Cita): Boolean =
        FechaHora.desde(cita.fecha, "00:00")?.fechaTexto() == reloj.ahora().fechaTexto()

    private fun coincideEstado(cita: Cita, filtro: FiltroEstado): Boolean = when (filtro) {
        FiltroEstado.TODAS -> true
        FiltroEstado.PROGRAMADA -> cita.estado is EstadoCita.Programada
        FiltroEstado.ATENDIDA -> cita.estado is EstadoCita.Atendida
        FiltroEstado.CANCELADA -> cita.estado is EstadoCita.Cancelada
    }

    // De la mas proxima a la mas lejana: primero las futuras (ascendente), luego las pasadas (mas recientes primero).
    private fun orden(cita: Cita, ahoraMinutos: Long): Pair<Int, Long> {
        val minutos = FechaHora.desde(cita.fecha, cita.hora)?.enMinutos() ?: return Pair(2, 0L)
        return if (minutos >= ahoraMinutos) Pair(0, minutos) else Pair(1, -minutos)
    }

    // Sin distinguir mayusculas ni tildes (RF-05)
    private fun normalizar(texto: String): String =
        texto.trim().lowercase().map { c ->
            when (c) {
                'á' -> 'a'
                'é' -> 'e'
                'í' -> 'i'
                'ó' -> 'o'
                'ú', 'ü' -> 'u'
                else -> c
            }
        }.joinToString("")
}
