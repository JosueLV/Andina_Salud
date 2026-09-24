package pe.edu.upeu.andinasalud.domain.rules

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.ErroresSolicitud
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.FechaHora
import pe.edu.upeu.andinasalud.domain.model.Medico
import pe.edu.upeu.andinasalud.domain.model.SolicitudCita

/**
 * Las cinco reglas de negocio del caso, en un solo lugar del dominio.
 * La interfaz (ViewModel/Compose) solo las consulta; nunca las repite.
 */
object ReglasCita {
    const val MAX_PROGRAMADAS = 3        // RN-02
    const val MOTIVO_MIN = 10            // RN-04
    const val MOTIVO_MAX = 200           // RN-04
    const val HORAS_MIN_CANCELAR = 24    // RN-03

    // ---------- RN-02 ----------
    fun cantidadProgramadas(citas: List<Cita>): Int =
        citas.count { it.estado is EstadoCita.Programada }

    fun limiteAlcanzado(citas: List<Cita>): Boolean =
        cantidadProgramadas(citas) >= MAX_PROGRAMADAS

    // ---------- RN-04 ----------
    fun validarMotivo(motivo: String): String? {
        val largo = motivo.trim().length
        return if (largo !in MOTIVO_MIN..MOTIVO_MAX) {
            "El motivo debe tener entre $MOTIVO_MIN y $MOTIVO_MAX caracteres (tiene $largo)"
        } else null
    }

    // ---------- RN-01 ----------
    /** Devuelve (errorFecha, errorHora). Una fecha u hora anterior al momento actual no es valida. */
    fun validarFechaHora(fecha: String, hora: String, ahora: FechaHora): Pair<String?, String?> {
        val f = FechaHora.parsearFecha(fecha)
        val h = FechaHora.parsearHora(hora)

        val errorFecha = when {
            fecha.isBlank() -> "Ingresa la fecha"
            f == null -> "Usa el formato AAAA-MM-DD con una fecha real"
            else -> null
        }
        val errorHora = when {
            hora.isBlank() -> "Ingresa la hora"
            h == null -> "Usa el formato HH:MM con una hora real"
            else -> null
        }
        if (f == null || h == null) return errorFecha to errorHora

        val elegida = FechaHora(f.first, f.second, f.third, h.first, h.second)
        if (elegida.enMinutos() < ahora.enMinutos()) {
            return if (elegida.inicioDelDiaEnMinutos() < ahora.inicioDelDiaEnMinutos()) {
                "La fecha no puede ser anterior a hoy" to null
            } else {
                null to "La hora ya paso, elige una hora posterior a la actual"
            }
        }
        return null to null
    }

    // ---------- RN-05 ----------
    /** Hay conflicto si ya existe otra cita Programada en la misma fecha y hora. */
    fun hayConflictoHorario(
        citas: List<Cita>,
        fecha: String,
        hora: String,
        excluirId: Int? = null
    ): Boolean {
        val nueva = FechaHora.desde(fecha, hora) ?: return false
        return citas.any { c ->
            c.id != excluirId &&
                c.estado is EstadoCita.Programada &&
                FechaHora.desde(c.fecha, c.hora) == nueva
        }
    }

    // ---------- RN-03 ----------
    /** Devuelve el motivo por el que NO se puede cancelar, o null si si se puede. */
    fun motivoNoCancelable(cita: Cita, ahora: FechaHora): String? {
        if (cita.estado !is EstadoCita.Programada) {
            return "Solo se pueden cancelar citas en estado Programada"
        }
        val fechaCita = FechaHora.desde(cita.fecha, cita.hora)
            ?: return "La cita tiene una fecha u hora invalida"
        val faltanMinutos = fechaCita.enMinutos() - ahora.enMinutos()
        return if (faltanMinutos <= HORAS_MIN_CANCELAR * 60L) {
            "Solo puedes cancelar con mas de $HORAS_MIN_CANCELAR horas de anticipacion"
        } else null
    }

    fun puedeCancelar(cita: Cita, ahora: FechaHora): Boolean = motivoNoCancelable(cita, ahora) == null

    // ---------- Validacion completa de la solicitud (RF-04) ----------
    fun validarSolicitud(
        solicitud: SolicitudCita,
        citas: List<Cita>,
        medicos: List<Medico>,
        ahora: FechaHora
    ): ErroresSolicitud {
        val errorEspecialidad = if (solicitud.especialidad.isBlank()) "Selecciona una especialidad" else null
        var errorSede = if (solicitud.sede.isBlank()) "Selecciona una sede" else null

        if (errorEspecialidad == null && errorSede == null) {
            val hayMedico = medicos.any {
                it.especialidad == solicitud.especialidad && solicitud.sede in it.sedes
            }
            if (!hayMedico) {
                errorSede = "No hay medicos de ${solicitud.especialidad} en la sede ${solicitud.sede}"
            }
        }

        val (errorFecha, errorHoraFormato) = validarFechaHora(solicitud.fecha, solicitud.hora, ahora)
        var errorHora = errorHoraFormato
        if (errorFecha == null && errorHora == null &&
            hayConflictoHorario(citas, solicitud.fecha, solicitud.hora) // RN-05
        ) {
            errorHora = "Ya tienes una cita programada en esa fecha y hora"
        }

        val errorGeneral = if (limiteAlcanzado(citas)) { // RN-02
            "Ya tienes $MAX_PROGRAMADAS citas programadas; cancela o atiende una para solicitar otra"
        } else null

        return ErroresSolicitud(
            especialidad = errorEspecialidad,
            sede = errorSede,
            fecha = errorFecha,
            hora = errorHora,
            motivo = validarMotivo(solicitud.motivo),
            general = errorGeneral
        )
    }

    // ---------- Validacion de reprogramacion (SC-D): reutiliza RN-01, RN-04 y RN-05 ----------
    fun validarReprogramacion(
        cita: Cita,
        nuevaFecha: String,
        nuevaHora: String,
        motivo: String,
        citas: List<Cita>,
        ahora: FechaHora
    ): ErroresSolicitud {
        if (cita.estado !is EstadoCita.Programada) {
            return ErroresSolicitud(general = "Solo se pueden reprogramar citas en estado Programada")
        }
        val (errorFecha, errorHoraFormato) = validarFechaHora(nuevaFecha, nuevaHora, ahora)
        var errorHora = errorHoraFormato
        if (errorFecha == null && errorHora == null &&
            hayConflictoHorario(citas, nuevaFecha, nuevaHora, excluirId = cita.id)
        ) {
            errorHora = "Ya tienes otra cita programada en esa fecha y hora"
        }
        return ErroresSolicitud(fecha = errorFecha, hora = errorHora, motivo = validarMotivo(motivo))
    }
}
