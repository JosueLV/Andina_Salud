package pe.edu.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.andinasalud.domain.model.Cita

@Composable
fun DetalleCitaScreen(cita: Cita, onCancelar: () -> Unit = {}) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Detalle de Cita", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Especialidad: ${cita.especialidad}")
                Text("Médico: ${cita.medico}")
                Text("Sede: ${cita.sede}") // Si sede no existe en Cita.kt, borra esta línea
                Text("Fecha: ${cita.fecha} | Hora: ${cita.hora}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cancelar Cita")
        }
    }
}