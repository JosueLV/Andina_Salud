package pe.edu.upeu.andinasalud.domain.model

enum class FiltroEstado(val etiqueta: String) {
    TODAS("Todas"),
    PROGRAMADA("Programada"),
    ATENDIDA("Atendida"),
    CANCELADA("Cancelada")
}

/** Todo lo que el usuario puede combinar en la lista: estado (RF-02), busqueda (RF-05) y Hoy (SC-A). */
data class CriteriosCitas(
    val estado: FiltroEstado = FiltroEstado.TODAS,
    val soloHoy: Boolean = false,
    val busqueda: String = ""
)
