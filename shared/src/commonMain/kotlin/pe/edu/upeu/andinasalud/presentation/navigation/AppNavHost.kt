package pe.edu.upeu.andinasalud.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject

// Asegúrate de que estos imports coincidan con tus paquetes reales
import pe.edu.upeu.andinasalud.presentation.citas.CitasScreen
import pe.edu.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.edu.upeu.andinasalud.presentation.perfil.PerfilScreen
import pe.edu.upeu.andinasalud.presentation.solicitud.SolicitudScreen
import pe.edu.upeu.andinasalud.presentation.detalle.DetalleCitaScreen
import pe.edu.upeu.andinasalud.data.local.CitasSimuladas

@Composable
fun AppNavHost(
    isDarkTheme: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Requerimiento RF-07: Barra de navegación con 3 destinos
            val destinosPrincipales = listOf(Destinos.Inicio.ruta, Destinos.Citas.ruta, Destinos.Perfil.ruta)

            if (currentRoute in destinosPrincipales) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") },
                        selected = currentRoute == Destinos.Inicio.ruta,
                        onClick = {
                            navController.navigate(Destinos.Inicio.ruta) {
                                popUpTo(navController.graph.findStartDestination().route!!) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Citas") },
                        label = { Text("Citas") },
                        selected = currentRoute == Destinos.Citas.ruta,
                        onClick = {
                            navController.navigate(Destinos.Citas.ruta) {
                                popUpTo(navController.graph.findStartDestination().route!!) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = currentRoute == Destinos.Perfil.ruta,
                        onClick = {
                            navController.navigate(Destinos.Perfil.ruta) {
                                popUpTo(navController.graph.findStartDestination().route!!) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinos.Citas.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinos.Inicio.ruta) {
                Text("Pantalla de Inicio (En construcción)")
            }
            composable(Destinos.Citas.ruta) {
                val viewModel: CitasViewModel = koinInject()
                CitasScreen(viewModel = viewModel)
            }
            composable(Destinos.Perfil.ruta) { // Usamos Destinos.Perfil.ruta en lugar de "perfil" estático
                PerfilScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange
                )
            }
            composable("solicitud") {
                SolicitudScreen(
                    onGuardar = { navController.popBackStack() }
                )
            }
            composable("detalle/{citaId}") {
                // Usamos la primera cita simulada temporalmente para poder ver el diseño
                val citaEjemplo = CitasSimuladas.citas.first()
                DetalleCitaScreen(
                    cita = citaEjemplo,
                    onCancelar = { navController.popBackStack() }
                )
            }
        }
    }
}