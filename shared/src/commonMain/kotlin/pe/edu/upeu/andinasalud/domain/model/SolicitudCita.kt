package pe.edu.upeu.andinasalud.domain.model

/** Datos que llegan del formulario de solicitud (RF-04). */
data class SolicitudCita(
    val especialidad: String,
    val sede: String,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val modalidad: String = "Presencial"
)

/** Un mensaje de error por campo, para mostrarlo debajo del campo correspondiente. */
data class ErroresSolicitud(
    val especialidad: String? = null,
    val sede: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val motivo: String? = null,
    val general: String? = null // reglas que no pertenecen a un solo campo (RN-02)
) {
    val hayErrores: Boolean
        get() = listOf(especialidad, sede, fecha, hora, motivo, general).any { it != null }

    fun resumen(): String =
        listOfNotNull(general, especialidad, sede, fecha, hora, motivo).joinToString(". ")
}

/** Falla de una regla de negocio de una sola causa (por ejemplo RN-03). */
class ReglaNegocioException(mensaje: String) : Exception(mensaje)

/** Falla de validacion de formulario; lleva el detalle por campo. */
class SolicitudInvalidaException(val errores: ErroresSolicitud) : Exception(errores.resumen())
