package pe.edu.upeu.andinasalud.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
import pe.edu.upeu.andinasalud.presentation.citas.CitasScreen
import pe.edu.upeu.andinasalud.presentation.citas.CitasViewModel

@Composable
fun AppNavHost() {
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
                                // Cambiamos .id por .route!! para que sea de tipo String
                                popUpTo(navController.graph.findStartDestination().route!!) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        // Usamos AutoMirrored para quitar la línea tachada
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
            startDestination = Destinos.Citas.ruta, // Iniciamos en Citas temporalmente para probar
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinos.Inicio.ruta) {
                Text("Pantalla de Inicio (En construcción)")
            }
            composable(Destinos.Citas.ruta) {
                // Inyectamos el ViewModel usando Koin
                val viewModel: CitasViewModel = koinInject()
                CitasScreen(viewModel = viewModel)
            }
            composable(Destinos.Perfil.ruta) {
                Text("Pantalla de Perfil (En construcción)")
            }
        }
    }
}