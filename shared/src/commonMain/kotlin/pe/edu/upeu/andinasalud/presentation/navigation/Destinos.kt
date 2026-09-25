package pe.edu.upeu.andinasalud.presentation.navigation

/** Rutas constantes de la aplicacion. */
sealed class Destinos(val ruta: String) {
    data object Inicio : Destinos("inicio")
    data object Citas : Destinos("citas")
    data object Perfil : Destinos("perfil")
    data object Solicitud : Destinos("solicitud")
    data object Detalle : Destinos("detalle")
}
