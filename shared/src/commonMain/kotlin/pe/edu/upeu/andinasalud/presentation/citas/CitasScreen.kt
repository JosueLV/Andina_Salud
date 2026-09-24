package pe.edu.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
    // Observamos los estados que vienen del ViewModel (Reglas SC-A y SC-B en la capa correcta)
    val uiState by viewModel.uiState.collectAsState()
    val mostrarSoloHoy by viewModel.mostrarSoloHoy.collectAsState()
    val limiteAlcanzado by viewModel.limiteAlcanzado.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Si NO se ha alcanzado el límite, permitimos la acción
                    if (!limiteAlcanzado) {
                        // Lógica de navegación para nueva cita
                    }
                },
                // SC-B: Deshabilitado visual (Gris si llegó al límite, color primario si no)
                containerColor = if (limiteAlcanzado) Color.Gray else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Cita")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize()) {

            Text("Mis Citas", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // SC-A: El chip interactúa directamente con el ViewModel
            FilterChip(
                selected = mostrarSoloHoy,
                onClick = { viewModel.toggleFiltroHoy() },
                label = { Text("Filtrar por Hoy") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is CitasUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is CitasUiState.Error -> Text("Error al cargar citas", color = Color.Red)
                is CitasUiState.Empty -> Text("No hay citas programadas") // Manejo exhaustivo
                is CitasUiState.Success -> {
                    // La lista ya viene filtrada desde la capa del ViewModel
                    val citas = (uiState as CitasUiState.Success).citas

                    LazyColumn {
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

                // Etiqueta visual de "Hoy" en la tarjeta
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

            // SC-C: Modalidad de atención con iconos y color
            Text(
                text = if (cita.modalidad == "Teleconsulta") "💻 Teleconsulta" else "📍 Presencial",
                color = Color(0xFF008000),
                fontWeight = FontWeight.Bold
            )
        }
    }
}