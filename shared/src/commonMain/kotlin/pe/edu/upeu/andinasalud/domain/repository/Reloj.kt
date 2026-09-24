package pe.edu.upeu.andinasalud.domain.repository

import pe.edu.upeu.andinasalud.domain.model.FechaHora

/** El dominio no sabe de zonas horarias ni de plataformas: pide la hora actual a esta interfaz. */
interface Reloj {
    fun ahora(): FechaHora
}
