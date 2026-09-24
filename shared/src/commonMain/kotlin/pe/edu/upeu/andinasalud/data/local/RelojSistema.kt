package pe.edu.upeu.andinasalud.data.local

import pe.edu.upeu.andinasalud.domain.model.FechaHora
import pe.edu.upeu.andinasalud.domain.repository.Reloj
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Hora actual de Lima (UTC-5, sin horario de verano) calculada en commonMain. */
class RelojSistema : Reloj {
    @OptIn(ExperimentalTime::class)
    override fun ahora(): FechaHora {
        val segundosLima = Clock.System.now().epochSeconds - 5 * 3600L
        return FechaHora.desdeMinutos(segundosLima.floorDiv(60L))
    }
}
