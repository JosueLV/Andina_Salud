package pe.edu.upeu.andinasalud.presentation.navigation

sealed class Destinos(val ruta: String) {
    data object Inicio : Destinos("inicio")
    data object Citas : Destinos("citas")
    data object Perfil : Destinos("perfil")
    data object Solicitud : Destinos("solicitud")
    data object Detalle : Destinos("detalle/{citaId}") {
        fun crearRuta(citaId: Int) = "detalle/$citaId"
    }
}