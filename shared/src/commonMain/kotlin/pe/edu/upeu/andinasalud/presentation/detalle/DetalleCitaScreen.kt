package pe.edu.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.EstadoCita

@Composable
fun DetalleCitaScreen(viewModel: DetalleCitaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            DetalleCitaContent(
                uiState = uiState,
                onCancelar = { mostrarDialogo = true },
                onReintentar = viewModel::cargar
            )
        }
    }

    // RF-03: dialogo de confirmacion antes de cancelar
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cancelar cita") },
            text = { Text("¿Seguro que deseas cancelar esta cita? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    viewModel.cancelarCita()
                }) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("No") }
            }
        )
    }
}

@Composable
fun DetalleCitaContent(
    uiState: DetalleCitaUiState,
    onCancelar: () -> Unit,
    onReintentar: () -> Unit
) {
    when (uiState) {
        is DetalleCitaUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is DetalleCitaUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No se pudo mostrar la cita",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(uiState.message)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onReintentar) { Text("Reintentar") }
            }
        }
        is DetalleCitaUiState.Success -> {
            DetalleCitaDatos(uiState, onCancelar)
        }
    }
}

@Composable
private fun DetalleCitaDatos(estado: DetalleCitaUiState.Success, onCancelar: () -> Unit) {
    val cita = estado.cita
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Detalle de cita", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DatoDetalle("Especialidad", cita.especialidad)
                DatoDetalle("Médico", cita.medico)
                DatoDetalle("Sede", cita.sede)
                DatoDetalle("Fecha y hora", "${cita.fecha} - ${cita.hora}")
                DatoDetalle("Modalidad", if (cita.modalidad == "Teleconsulta") "💻 Teleconsulta" else "📍 Presencial")
                DatoDetalle("Estado", textoEstado(cita))
                DatoDetalle("Indicaciones", textoIndicaciones(cita))
            }
        }

        if (cita.historial.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cambios registrados", style = MaterialTheme.typography.titleMedium)
            cita.historial.forEach { registro ->
                Text("• $registro", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Solo tiene sentido cancelar una cita Programada; RN-03 decide si esta habilitado
        if (cita.estado is EstadoCita.Programada) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = onCancelar,
                enabled = estado.puedeCancelar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancelar cita")
            }
            if (!estado.puedeCancelar && estado.motivoNoCancelable != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    estado.motivoNoCancelable,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/** Composable reutilizable: recibe una etiqueta y un valor, no sabe nada de citas. */
@Composable
private fun DatoDetalle(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun textoEstado(cita: Cita): String = when (val e = cita.estado) {
    is EstadoCita.Programada ->
        if (e.recordatorioActivo) "Programada (recordatorio activo)" else "Programada (sin recordatorio)"
    is EstadoCita.Atendida -> "Atendida"
    is EstadoCita.Cancelada ->
        "Cancelada: ${e.motivo}" + if (e.canceladaPorPaciente) " (por el paciente)" else ""
}

private fun textoIndicaciones(cita: Cita): String = when (val e = cita.estado) {
    is EstadoCita.Programada -> "Llega 10 minutos antes y trae tu documento de identidad."
    is EstadoCita.Atendida -> e.indicaciones
    is EstadoCita.Cancelada -> "Sin indicaciones"
}
