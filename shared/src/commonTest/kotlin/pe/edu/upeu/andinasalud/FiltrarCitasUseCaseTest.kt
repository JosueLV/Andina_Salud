package pe.edu.upeu.andinasalud

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.CriteriosCitas
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.FechaHora
import pe.edu.upeu.andinasalud.domain.model.FiltroEstado
import pe.edu.upeu.andinasalud.domain.repository.Reloj
import pe.edu.upeu.andinasalud.domain.usecase.FiltrarCitasUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class FiltrarCitasUseCaseTest {

    private val reloj = object : Reloj {
        override fun ahora() = FechaHora(2026, 9, 24, 10, 0)
    }
    private val useCase = FiltrarCitasUseCase(reloj)

    private fun cita(id: Int, esp: String, medico: String, fecha: String, hora: String, estado: EstadoCita) =
        Cita(id = id, especialidad = esp, medico = medico, fecha = fecha, hora = hora, sede = "Ñaña", estado = estado)

    private val hoyProgramada = cita(1, "Odontología", "Dra. Rosa Flores", "2026-09-24", "16:00", EstadoCita.Programada(true))
    private val hoyAtendida = cita(2, "Nutrición", "Lic. Ana Bermúdez", "2026-09-24", "08:00", EstadoCita.Atendida("ok"))
    private val futura = cita(3, "Medicina General", "Dr. Iván Rojas", "2026-10-18", "09:00", EstadoCita.Programada(false))
    private val pasada = cita(4, "Psicología", "Ps. Luis Tapia", "2026-09-02", "15:00", EstadoCita.Atendida("ok"))
    private val todas = listOf(pasada, futura, hoyAtendida, hoyProgramada)

    @Test
    fun busquedaSinTildesNiMayusculas() {
        val r1 = useCase(todas, CriteriosCitas(busqueda = "ODONTOLOGIA"))
        assertEquals(listOf(1), r1.map { it.id })
        val r2 = useCase(todas, CriteriosCitas(busqueda = "ivan"))
        assertEquals(listOf(3), r2.map { it.id })
    }

    @Test
    fun chipHoySeCombinaConEstado() {
        val soloHoy = useCase(todas, CriteriosCitas(soloHoy = true))
        assertEquals(setOf(1, 2), soloHoy.map { it.id }.toSet())

        val hoyProgramadas = useCase(todas, CriteriosCitas(estado = FiltroEstado.PROGRAMADA, soloHoy = true))
        assertEquals(listOf(1), hoyProgramadas.map { it.id })
    }

    @Test
    fun ordenaDeLaMasProximaALaMasLejana() {
        // ahora = 24/09 10:00 -> futuras ascendentes, luego pasadas de la mas reciente a la mas antigua
        val orden = useCase(todas, CriteriosCitas()).map { it.id }
        assertEquals(listOf(1, 3, 2, 4), orden)
    }
}
