package pe.edu.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SolicitudScreen(onGuardar: () -> Unit = {}) {
    var especialidad by remember { mutableStateOf("") }
    var sede by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Solicitar Nueva Cita", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = especialidad, onValueChange = { especialidad = it }, label = { Text("Especialidad") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = sede, onValueChange = { sede = it }, label = { Text("Sede") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = hora, onValueChange = { hora = it }, label = { Text("Hora (HH:MM)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = motivo, onValueChange = { motivo = it }, label = { Text("Motivo de consulta") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onGuardar, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar Solicitud")
        }
    }
}