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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import pe.edu.upeu.andinasalud.presentation.citas.CitasScreen
import pe.edu.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.edu.upeu.andinasalud.presentation.detalle.DetalleCitaScreen
import pe.edu.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.edu.upeu.andinasalud.presentation.inicio.InicioScreen
import pe.edu.upeu.andinasalud.presentation.inicio.InicioUiState
import pe.edu.upeu.andinasalud.presentation.inicio.InicioViewModel
import pe.edu.upeu.andinasalud.presentation.perfil.PerfilScreen
import pe.edu.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.edu.upeu.andinasalud.presentation.solicitud.SolicitudScreen
import pe.edu.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

private data class ItemBarra(
    val destino: Destinos,
    val etiqueta: String,
    val icono: ImageVector
)

private val itemsBarra = listOf(
    ItemBarra(Destinos.Inicio, "Inicio", Icons.Default.Home),
    ItemBarra(Destinos.Citas, "Citas", Icons.AutoMirrored.Filled.List),
    ItemBarra(Destinos.Perfil, "Perfil", Icons.Default.Person)
)

private fun NavController.irAPestana(destino: Destinos) {
    navigate(destino.ruta) {
        popUpTo(graph.findStartDestination().route!!) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavHost(
    isDarkTheme: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    // Estos ViewModel se comparten entre la barra inferior y su pantalla (no se recargan al cambiar de pestana)
    val inicioViewModel: InicioViewModel = koinInject()
    val citasViewModel: CitasViewModel = koinInject()
    val inicioState by inicioViewModel.uiState.collectAsState()

    // Estado elevado: id de la cita que se esta viendo en el detalle
    var citaSeleccionadaId by rememberSaveable { mutableStateOf(-1) }

    // SC-B: numero de citas Programadas para el indicador de la barra
    val programadas = (inicioState as? InicioUiState.Success)?.cantidadProgramadas ?: 0

    Scaffold(
        bottomBar = {
            if (rutaActual in itemsBarra.map { it.destino.ruta }) {
                NavigationBar {
                    itemsBarra.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                if (item.destino == Destinos.Citas && programadas > 0) {
                                    BadgedBox(badge = { Badge { Text(programadas.toString()) } }) {
                                        Icon(item.icono, contentDescription = item.etiqueta)
                                    }
                                } else {
                                    Icon(item.icono, contentDescription = item.etiqueta)
                                }
                            },
                            label = { Text(item.etiqueta) },
                            selected = rutaActual == item.destino.ruta,
                            onClick = { navController.irAPestana(item.destino) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinos.Inicio.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinos.Inicio.ruta) {
                InicioScreen(
                    uiState = inicioState,
                    onIrACitas = { navController.irAPestana(Destinos.Citas) },
                    onSolicitar = { navController.navigate(Destinos.Solicitud.ruta) },
                    onCitaClick = { id ->
                        citaSeleccionadaId = id
                        navController.navigate(Destinos.Detalle.ruta)
                    },
                    onReintentar = inicioViewModel::cargar
                )
            }
            composable(Destinos.Citas.ruta) {
                CitasScreen(
                    viewModel = citasViewModel,
                    onCitaClick = { cita ->
                        citaSeleccionadaId = cita.id
                        navController.navigate(Destinos.Detalle.ruta)
                    },
                    onNuevaCita = { navController.navigate(Destinos.Solicitud.ruta) }
                )
            }
            composable(Destinos.Perfil.ruta) {
                val viewModel: PerfilViewModel = koinInject()
                PerfilScreen(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange
                )
            }
            composable(Destinos.Solicitud.ruta) {
                val viewModel: SolicitudViewModel = koinInject()
                SolicitudScreen(
                    viewModel = viewModel,
                    onRegistrada = { navController.popBackStack() }
                )
            }
            composable(Destinos.Detalle.ruta) {
                val viewModel: DetalleCitaViewModel = koinInject(
                    parameters = { parametersOf(citaSeleccionadaId) }
                )
                DetalleCitaScreen(viewModel = viewModel)
            }
        }
    }
}
