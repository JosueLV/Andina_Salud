package pe.edu.upeu.andinasalud

import pe.edu.upeu.andinasalud.presentation.theme.AndinaSaludTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.compose.KoinApplication

// Importa tu módulo Koin y la navegación
import pe.edu.upeu.andinasalud.di.appModule
import pe.edu.upeu.andinasalud.presentation.navigation.AppNavHost



@Composable
fun App() {
    // 1. Iniciamos Koin y cargamos tu módulo de dependencias (ViewModel, Repositorios)
    KoinApplication(application = {
        modules(appModule)
    }) {
        // 2. Estado global del modo oscuro
        var isDarkTheme by remember { mutableStateOf(false) }

        // 3. Aplicamos el tema pasando el estado
        AndinaSaludTheme(darkTheme = isDarkTheme) {
            Surface(modifier = Modifier.fillMaxSize()) {
                // 4. Llamamos a la navegación y le pasamos el estado y la función para cambiarlo
                AppNavHost(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { nuevoEstado -> isDarkTheme = nuevoEstado }
                )
            }
        }
    }
}