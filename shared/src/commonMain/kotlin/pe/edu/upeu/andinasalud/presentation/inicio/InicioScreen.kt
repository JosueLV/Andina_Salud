package pe.edu.upeu.andinasalud.presentation.inicio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.rules.ReglasCita

@Composable
fun InicioScreen(
    uiState: InicioUiState,
    onIrACitas: () -> Unit,
    onSolicitar: () -> Unit,
    onCitaClick: (Int) -> Unit,
    onReintentar: () -> Unit
) {
    when (uiState) {
        is InicioUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is InicioUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No se pudo cargar el inicio",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(uiState.message)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onReintentar) { Text("Reintentar") }
            }
        }
        is InicioUiState.Success -> {
            InicioContenido(uiState, onIrACitas, onSolicitar, onCitaClick)
        }
    }
}

@Composable
private fun InicioContenido(
    estado: InicioUiState.Success,
    onIrACitas: () -> Unit,
    onSolicitar: () -> Unit,
    onCitaClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Hola, ${estado.primerNombre}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text("¿Cómo te sientes hoy?", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Próxima cita programada", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ProximaCitaCard(cita = estado.proximaCita, onClick = onCitaClick)

        Spacer(modifier = Modifier.height(32.dp))

        Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onIrACitas) { Text("Mis citas") }
            // SC-B: el boton se deshabilita segun la regla RN-02 que viene del dominio
            FilledTonalButton(onClick = onSolicitar, enabled = !estado.limiteAlcanzado) {
                Text("Solicitar cita")
            }
        }
        if (estado.limiteAlcanzado) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Llegaste al límite de ${ReglasCita.MAX_PROGRAMADAS} citas programadas.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ProximaCitaCard(cita: Cita?, onClick: (Int) -> Unit) {
    if (cita == null) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text("No tienes citas programadas.", modifier = Modifier.padding(16.dp))
        }
        return
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(cita.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(cita.especialidad, fontWeight = FontWeight.Bold)
                Text("${cita.fecha} - ${cita.hora}")
                Text("📍 Sede ${cita.sede}")
            }
        }
    }
}
