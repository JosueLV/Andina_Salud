package pe.edu.upeu.andinasalud.domain.model

/**
 * Fecha y hora sin dependencias de plataforma, para que las reglas de negocio
 * (RN-01, RN-03, RN-05) vivan en commonMain y se puedan probar sin emulador.
 */
data class FechaHora(
    val anio: Int,
    val mes: Int,
    val dia: Int,
    val hora: Int,
    val minuto: Int
) {
    /** Minutos transcurridos desde 1970-01-01 00:00 (hora local, sin zona). */
    fun enMinutos(): Long = diasDesdeCivil(anio, mes, dia) * 1440L + hora * 60L + minuto

    fun inicioDelDiaEnMinutos(): Long = diasDesdeCivil(anio, mes, dia) * 1440L

    fun fechaTexto(): String =
        "${anio.toString().padStart(4, '0')}-${mes.toString().padStart(2, '0')}-${dia.toString().padStart(2, '0')}"

    fun horaTexto(): String =
        "${hora.toString().padStart(2, '0')}:${minuto.toString().padStart(2, '0')}"

    companion object {
        /** Devuelve (anio, mes, dia) o null si el texto no es una fecha real AAAA-MM-DD. */
        fun parsearFecha(texto: String): Triple<Int, Int, Int>? {
            val partes = texto.trim().split("-")
            if (partes.size != 3 || partes[0].length != 4) return null
            val anio = partes[0].toIntOrNull() ?: return null
            val mes = partes[1].toIntOrNull() ?: return null
            val dia = partes[2].toIntOrNull() ?: return null
            if (mes !in 1..12) return null
            if (dia !in 1..diasEnMes(anio, mes)) return null
            return Triple(anio, mes, dia)
        }

        /** Devuelve (hora, minuto) o null si el texto no es una hora real HH:MM. */
        fun parsearHora(texto: String): Pair<Int, Int>? {
            val partes = texto.trim().split(":")
            if (partes.size != 2) return null
            val hora = partes[0].toIntOrNull() ?: return null
            val minuto = partes[1].toIntOrNull() ?: return null
            if (hora !in 0..23 || minuto !in 0..59) return null
            return Pair(hora, minuto)
        }

        fun desde(fecha: String, hora: String): FechaHora? {
            val f = parsearFecha(fecha) ?: return null
            val h = parsearHora(hora) ?: return null
            return FechaHora(f.first, f.second, f.third, h.first, h.second)
        }

        fun desdeMinutos(totalMinutos: Long): FechaHora {
            val dias = totalMinutos.floorDiv(1440L)
            val resto = totalMinutos.mod(1440L).toInt()
            val (anio, mes, dia) = civilDesdeDias(dias)
            return FechaHora(anio, mes, dia, resto / 60, resto % 60)
        }
    }
}

private fun esBisiesto(anio: Int): Boolean =
    (anio % 4 == 0 && anio % 100 != 0) || anio % 400 == 0

private fun diasEnMes(anio: Int, mes: Int): Int = when (mes) {
    2 -> if (esBisiesto(anio)) 29 else 28
    4, 6, 9, 11 -> 30
    else -> 31
}

// Algoritmos de calendario civil (Howard Hinnant), dias desde 1970-01-01.
private fun diasDesdeCivil(anioOriginal: Int, mes: Int, dia: Int): Long {
    val anio = if (mes <= 2) anioOriginal - 1 else anioOriginal
    val era = (if (anio >= 0) anio else anio - 399) / 400
    val yoe = anio - era * 400
    val doy = (153 * (if (mes > 2) mes - 3 else mes + 9) + 2) / 5 + dia - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return era.toLong() * 146097L + doe - 719468L
}

private fun civilDesdeDias(dias: Long): Triple<Int, Int, Int> {
    val z = dias + 719468L
    val era = (if (z >= 0) z else z - 146096L) / 146097L
    val doe = z - era * 146097L
    val yoe = (doe - doe / 1460L + doe / 36524L - doe / 146096L) / 365L
    val y = yoe + era * 400L
    val doy = doe - (365L * yoe + yoe / 4L - yoe / 100L)
    val mp = (5L * doy + 2L) / 153L
    val d = doy - (153L * mp + 2L) / 5L + 1L
    val m = if (mp < 10L) mp + 3L else mp - 9L
    val anio = if (m <= 2L) y + 1L else y
    return Triple(anio.toInt(), m.toInt(), d.toInt())
}
