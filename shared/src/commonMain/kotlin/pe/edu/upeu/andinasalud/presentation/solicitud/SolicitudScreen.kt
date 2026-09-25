package pe.edu.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.andinasalud.domain.rules.ReglasCita
import pe.edu.upeu.andinasalud.presentation.components.EncabezadoConVolver

@Composable
fun SolicitudScreen(
    viewModel: SolicitudViewModel,
    onRegistrada: () -> Unit,
    onVolver: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()

    // Al registrarse la cita se vuelve a la pantalla anterior; la lista se actualiza sola
    LaunchedEffect(estado.registrada) {
        if (estado.registrada) {
            viewModel.registroMostrado()
            onRegistrada()
        }
    }

    val errores = estado.errores

    Column(modifier = Modifier.fillMaxSize()) {
    EncabezadoConVolver(titulo = "Solicitar nueva cita", onVolver = onVolver)
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        // Errores que no pertenecen a un solo campo (RN-02)
        if (errores.general != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(
                    errores.general,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        CampoSeleccion(
            etiqueta = "Especialidad",
            valor = estado.especialidad,
            opciones = estado.opcionesEspecialidad,
            error = errores.especialidad,
            onSeleccion = viewModel::onEspecialidadChange
        )
        CampoSeleccion(
            etiqueta = "Sede",
            valor = estado.sede,
            opciones = estado.opcionesSede,
            error = errores.sede,
            onSeleccion = viewModel::onSedeChange
        )
        CampoFormulario(
            etiqueta = "Fecha (AAAA-MM-DD)",
            valor = estado.fecha,
            error = errores.fecha,
            onCambio = viewModel::onFechaChange
        )
        CampoFormulario(
            etiqueta = "Hora (HH:MM)",
            valor = estado.hora,
            error = errores.hora,
            onCambio = viewModel::onHoraChange
        )

        Text("Modalidad de atención", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SolicitudUiState.MODALIDADES.forEach { modalidad ->
                FilterChip(
                    selected = estado.modalidad == modalidad,
                    onClick = { viewModel.onModalidadChange(modalidad) },
                    label = { Text(if (modalidad == "Teleconsulta") "💻 $modalidad" else "📍 $modalidad") }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        CampoFormulario(
            etiqueta = "Motivo de consulta",
            valor = estado.motivo,
            error = errores.motivo,
            onCambio = viewModel::onMotivoChange,
            ayuda = "${estado.motivo.trim().length}/${ReglasCita.MOTIVO_MAX}",
            lineasMinimas = 3
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = viewModel::registrar,
            enabled = !estado.enviando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.enviando) "Registrando..." else "Registrar solicitud")
        }
    }
    }
}

/** Campo de texto reutilizable: muestra el error debajo del campo (RF-04). No conoce el ViewModel. */
@Composable
fun CampoFormulario(
    etiqueta: String,
    valor: String,
    error: String?,
    onCambio: (String) -> Unit,
    ayuda: String? = null,
    lineasMinimas: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = {
            val texto = error ?: ayuda
            if (texto != null) Text(texto)
        },
        minLines = lineasMinimas,
        modifier = Modifier.fillMaxWidth()
    )
}

/** Lista desplegable reutilizable con el mismo manejo de error debajo del campo. */
@Composable
fun CampoSeleccion(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    error: String?,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
            isError = error != null,
            supportingText = { if (error != null) Text(error) },
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente: el campo es de solo lectura y esta capa recibe el toque
        Box(modifier = Modifier.matchParentSize().clickable { expandido = true })

        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
