package pe.edu.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.andinasalud.domain.model.Cita

@Composable
fun CitasScreen(viewModel: CitasViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val filtroActivo by viewModel.filtroHoyActivo.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mis Citas", style = MaterialTheme.typography.headlineMedium)

        // SC-A: Chip de filtro "Hoy"
        FilterChip(
            selected = filtroActivo,
            onClick = { viewModel.toggleFiltroHoy() },
            label = { Text("Hoy") },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        when (val state = uiState) {
            is CitasUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CitasUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is CitasUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay citas registradas para hoy.")
                }
            }
            is CitasUiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.citas) { cita ->
                        CitaItem(cita = cita)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CitaItem(cita: Cita) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = cita.especialidad, style = MaterialTheme.typography.titleMedium)
            Text(text = "Dr/Dra: ${cita.medico}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "${cita.fecha} - ${cita.hora}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Sede: ${cita.sede}", style = MaterialTheme.typography.bodySmall)
        }
    }
}