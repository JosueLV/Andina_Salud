package pe.edu.upeu.andinasalud.presentation.inicio

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun InicioScreen(
    // En un proyecto real, estas funciones de navegación vendrían por parámetro
    onNavigateToCitas: () -> Unit = {},
    onNavigateToSolicitar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Saludo con nombre del paciente
        Text(
            text = "Hola, Josue",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text("¿Cómo te sientes hoy?", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Tarjeta destacada con próxima cita
        Text("Próxima cita programada", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Medicina General", fontWeight = FontWeight.Bold)
                    Text("Mañana - 09:00 AM")
                    Text("📍 Sede Ñaña")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Accesos rápidos
        Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onNavigateToCitas) {
                Text("Mis citas")
            }
            FilledTonalButton(onClick = onNavigateToSolicitar) {
                Text("Solicitar cita")
            }
        }
    }
}