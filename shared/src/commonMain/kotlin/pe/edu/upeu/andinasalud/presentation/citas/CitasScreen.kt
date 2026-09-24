package pe.edu.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.andinasalud.domain.model.Cita

@Composable
fun CitasScreen(viewModel: CitasViewModel) {
    // Observamos todos los estados del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val mostrarSoloHoy by viewModel.mostrarSoloHoy.collectAsState()
    val limiteAlcanzado by viewModel.limiteAlcanzado.collectAsState()
    val estadoFiltro by viewModel.estadoFiltro.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()

    val estados = listOf("Todas", "Programada", "Atendida", "Cancelada")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // SC-B: Solo navega si no ha alcanzado el límite de citas
                    if (!limiteAlcanzado) {
                        // Lógica de navegación para nueva cita
                    }
                },
                // SC-B: Deshabilitado visual (gris) si llegó al límite
                containerColor = if (limiteAlcanzado) Color.Gray else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Cita")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Mis Citas", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // RF-05: Barra de Búsqueda
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { viewModel.setTextoBusqueda(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar especialidad o médico...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // SC-A: Chip para filtrar solo las citas de "Hoy"
                FilterChip(
                    selected = mostrarSoloHoy,
                    onClick = { viewModel.toggleFiltroHoy() },
                    label = { Text("Filtrar por Hoy") }
                )
            }

            // RF-02: Pestañas de estado (Todas, Programada, Atendida, Cancelada)
            ScrollableTabRow(
                selectedTabIndex = estados.indexOf(estadoFiltro),
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                estados.forEach { estado ->
                    Tab(
                        selected = estadoFiltro == estado,
                        onClick = { viewModel.setEstadoFiltro(estado) },
                        text = { Text(estado) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // RF-08: Manejo exhaustivo de todos los estados de la interfaz
            when (uiState) {
                is CitasUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is CitasUiState.Error -> {
                    Text("Error al cargar citas", color = Color.Red, modifier = Modifier.padding(16.dp))
                }
                is CitasUiState.Empty -> {
                    Text("No hay citas programadas", modifier = Modifier.padding(16.dp))
                }
                is CitasUiState.Success -> {
                    val citas = (uiState as CitasUiState.Success).citas

                    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                        items(citas) { cita ->
                            CitaCard(cita = cita)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CitaCard(cita: Cita) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(cita.especialidad, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                // SC-A: Etiqueta visual de "Hoy" destacada en rojo
                if (cita.fecha == "2026-09-23") {
                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                        Text("Hoy", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }
            }

            Text(cita.medico)
            Text("${cita.fecha} - ${cita.hora}")
            Text("Sede: ${cita.sede}")

            Spacer(modifier = Modifier.height(8.dp))

            // SC-C: Modalidad de atención con iconos y colores representativos
            Text(
                text = if (cita.modalidad == "Teleconsulta") "💻 Teleconsulta" else "📍 Presencial",
                color = Color(0xFF008000),
                fontWeight = FontWeight.Bold
            )
        }
    }
}