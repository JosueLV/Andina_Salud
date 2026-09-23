package pe.edu.upeu.andinasalud.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen(
    nombrePaciente: String = "Lucia Quispe Mamani",
    documento: String = "70154823",
    correo: String = "lucia.quispe@correo.pe",
    isDarkTheme: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Perfil del Paciente", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nombre: $nombrePaciente", style = MaterialTheme.typography.bodyLarge)
                Text("Documento: $documento", style = MaterialTheme.typography.bodyLarge)
                Text("Correo: $correo", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Modo Oscuro", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onThemeChange
            )
        }
    }
}