package pe.edu.upeu.andinasalud.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Perfil del paciente", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when (val estado = uiState) {
            is PerfilUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PerfilUiState.Error -> {
                Text(estado.message, color = MaterialTheme.colorScheme.error)
                Button(onClick = viewModel::cargar) { Text("Reintentar") }
            }
            is PerfilUiState.Success -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Nombre: ${estado.paciente.nombre}", style = MaterialTheme.typography.bodyLarge)
                        Text("Documento: ${estado.paciente.documento}", style = MaterialTheme.typography.bodyLarge)
                        Text("Correo: ${estado.paciente.correo}", style = MaterialTheme.typography.bodyLarge)
                        Text("Teléfono: ${estado.paciente.telefono}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // RF-06: el tema se aplica en App.kt, en la raiz del arbol, y por eso cambia toda la aplicacion
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Modo oscuro", style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isDarkTheme, onCheckedChange = onThemeChange)
        }
    }
}
