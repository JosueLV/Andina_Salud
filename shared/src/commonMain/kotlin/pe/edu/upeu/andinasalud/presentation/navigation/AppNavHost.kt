package pe.edu.upeu.andinasalud.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import pe.edu.upeu.andinasalud.presentation.citas.CitasScreen
import pe.edu.upeu.andinasalud.presentation.citas.CitasUiState
import pe.edu.upeu.andinasalud.presentation.citas.CitasViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // SC-B (Paso 1): Inyectamos el ViewModel a nivel global para acceder al contador en la barra
    val citasViewModel: CitasViewModel = koinInject()
    val uiState by citasViewModel.uiState.collectAsState()

    // Calculamos cuántas citas hay en la lista
    val cantidadCitas = if (uiState is CitasUiState.Success) {
        (uiState as CitasUiState.Success).citas.size
    } else 0

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
                        icon = {
                            // SC-B (Paso 1): Envolver el icono en un BadgedBox
                            BadgedBox(
                                badge = {
                                    if (cantidadCitas > 0) {
                                        Badge { Text(cantidadCitas.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Citas")
                            }
                        },
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
                // Ya tenemos el viewModel inyectado arriba, así que solo lo pasamos
                CitasScreen(viewModel = citasViewModel)
            }
            composable(Destinos.Perfil.ruta) {
                Text("Pantalla de Perfil (En construcción)")
            }
        }
    }
}