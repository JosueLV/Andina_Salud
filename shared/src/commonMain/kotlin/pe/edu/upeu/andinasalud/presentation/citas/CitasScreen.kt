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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
@Composable
fun CitasScreen(viewModel: CitasViewModel) {
    // Elevación de estado (State Hoisting) exigida en el examen
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mis Citas", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Manejo de los 4 estados obligatorios del RF-08
        when (val state = uiState) {
            is CitasUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator() // Muestra la carga de los 800ms
                }
            }
            is CitasUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is CitasUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay citas registradas.")
                }
            }
            is CitasUiState.Success -> {
                // RF-02: Lista con LazyColumn
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

// Composable reutilizable (Criterio de evaluación)
@Composable
fun CitaItem(cita: Cita) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = cita.especialidad, style = MaterialTheme.typography.titleMedium)
            Text(text = "Dr/Dra: ${cita.medico}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "${cita.fecha} - ${cita.hora}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Sede: ${cita.sede}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            // SC-C: Fila con ícono dinámico según la modalidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (cita.modalidad == "Teleconsulta") Icons.Default.VideoCall else Icons.Default.LocationOn,
                    contentDescription = "Icono Modalidad",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = cita.modalidad,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}