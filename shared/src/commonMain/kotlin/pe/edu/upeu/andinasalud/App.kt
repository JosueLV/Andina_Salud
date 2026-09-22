package pe.edu.upeu.andinasalud

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import pe.edu.upeu.andinasalud.di.appModule
import pe.edu.upeu.andinasalud.presentation.navigation.AppNavHost
import pe.edu.upeu.andinasalud.presentation.theme.AndinaSaludTheme

@Composable
fun App() {
    // Inicialización de la inyección de dependencias exigida por el examen
    KoinApplication(application = {
        modules(appModule)
    }) {
        // Aplicación de la paleta propia Material 3 (Requerimiento técnico)
        AndinaSaludTheme {
            // Punto de entrada de la navegación con el Scaffold inferior (RF-07)
            AppNavHost()
        }
    }
}