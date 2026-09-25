package pe.edu.upeu.andinasalud

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.FechaHora
import pe.edu.upeu.andinasalud.domain.rules.ReglasCita
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReglasCitaTest {

    private val ahora = FechaHora(2026, 9, 24, 10, 0)

    private fun cita(id: Int, fecha: String, hora: String, estado: EstadoCita = EstadoCita.Programada(true)) =
        Cita(id = id, especialidad = "Nutricion", medico = "Lic. X", fecha = fecha, hora = hora, sede = "Ñaña", estado = estado)

    @Test
    fun rn01_rechazaFechaPasadaYAceptaFutura() {
        val (errorFecha, _) = ReglasCita.validarFechaHora("2026-09-23", "09:00", ahora)
        assertNotNull(errorFecha)
        val (f, h) = ReglasCita.validarFechaHora("2026-10-01", "09:00", ahora)
        assertNull(f)
        assertNull(h)
    }

    @Test
    fun rn01_rechazaHoraPasadaDeHoy() {
        val (_, errorHora) = ReglasCita.validarFechaHora("2026-09-24", "08:00", ahora)
        assertNotNull(errorHora)
    }

    @Test
    fun rn02_limiteDeTresProgramadas() {
        val dos = listOf(cita(1, "2026-10-01", "09:00"), cita(2, "2026-10-02", "09:00"))
        assertFalse(ReglasCita.limiteAlcanzado(dos))
        val tres = dos + cita(3, "2026-10-03", "09:00")
        assertTrue(ReglasCita.limiteAlcanzado(tres))
        // Las Atendidas no cuentan
        val conAtendida = dos + cita(4, "2026-08-01", "09:00", EstadoCita.Atendida("ok"))
        assertFalse(ReglasCita.limiteAlcanzado(conAtendida))
    }

    @Test
    fun rn03_soloCancelaSiFaltanMasDe24Horas() {
        val en25h = cita(1, "2026-09-25", "11:00")   // faltan 25 h
        val en23h = cita(2, "2026-09-25", "09:00")   // faltan 23 h
        assertTrue(ReglasCita.puedeCancelar(en25h, ahora))
        assertFalse(ReglasCita.puedeCancelar(en23h, ahora))
        val atendida = cita(3, "2026-10-30", "09:00", EstadoCita.Atendida("ok"))
        assertFalse(ReglasCita.puedeCancelar(atendida, ahora))
    }

    @Test
    fun rn04_motivoEntre10Y200() {
        assertNotNull(ReglasCita.validarMotivo("corto"))
        assertNull(ReglasCita.validarMotivo("Dolor de cabeza frecuente"))
        assertNotNull(ReglasCita.validarMotivo("x".repeat(201)))
    }

    @Test
    fun rn05_noDuplicaMismoDiaYHora() {
        val citas = listOf(cita(1, "2026-10-01", "09:00"))
        assertTrue(ReglasCita.hayConflictoHorario(citas, "2026-10-01", "09:00"))
        assertFalse(ReglasCita.hayConflictoHorario(citas, "2026-10-01", "10:00"))
        // Al reprogramar la misma cita no choca consigo misma
        assertFalse(ReglasCita.hayConflictoHorario(citas, "2026-10-01", "09:00", excluirId = 1))
    }

    @Test
    fun fechaHora_idaYVueltaDeMinutos() {
        val original = FechaHora(2026, 2, 28, 23, 59)
        assertEquals(original, FechaHora.desdeMinutos(original.enMinutos()))
        assertEquals(1L, FechaHora(2026, 3, 1, 0, 0).enMinutos() - FechaHora(2026, 2, 28, 23, 59).enMinutos())
    }
}
