package pe.edu.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.CriteriosCitas
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.FiltroEstado

/** Conecta el ViewModel con la interfaz. No contiene logica de negocio. */
@Composable
fun CitasScreen(
    viewModel: CitasViewModel,
    onCitaClick: (Cita) -> Unit = {},
    onNuevaCita: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val criterios by viewModel.criterios.collectAsState()
    val limiteAlcanzado by viewModel.limiteAlcanzado.collectAsState()

    CitasContent(
        uiState = uiState,
        criterios = criterios,
        limiteAlcanzado = limiteAlcanzado,
        onBusquedaChange = viewModel::setTextoBusqueda,
        onEstadoChange = viewModel::setEstadoFiltro,
        onToggleHoy = viewModel::toggleFiltroHoy,
        onReintentar = viewModel::cargarCitas,
        onCitaClick = onCitaClick,
        onNuevaCita = onNuevaCita
    )
}

/** Version sin estado propio (state hoisting): recibe datos y devuelve eventos. */
@Composable
fun CitasContent(
    uiState: CitasUiState,
    criterios: CriteriosCitas,
    limiteAlcanzado: Boolean,
    onBusquedaChange: (String) -> Unit,
    onEstadoChange: (FiltroEstado) -> Unit,
    onToggleHoy: () -> Unit,
    onReintentar: () -> Unit,
    onCitaClick: (Cita) -> Unit,
    onNuevaCita: () -> Unit
) {
    val estados = FiltroEstado.entries

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                // SC-B: el limite viene de la regla RN-02 del dominio
                onClick = { if (!limiteAlcanzado) onNuevaCita() },
                containerColor = if (limiteAlcanzado) Color.Gray else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva cita")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Mis Citas", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = criterios.busqueda,
                    onValueChange = onBusquedaChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar especialidad o médico...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // SC-A: chip "Hoy", se combina con el filtro de estado
                FilterChip(
                    selected = criterios.soloHoy,
                    onClick = onToggleHoy,
                    label = { Text("Hoy") }
                )
            }

            ScrollableTabRow(
                selectedTabIndex = estados.indexOf(criterios.estado),
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                estados.forEach { estado ->
                    Tab(
                        selected = criterios.estado == estado,
                        onClick = { onEstadoChange(estado) },
                        text = { Text(estado.etiqueta) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // RF-08: carga, contenido, lista vacia y error
            when (uiState) {
                is CitasUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CitasUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No se pudieron cargar las citas",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(uiState.message, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onReintentar) { Text("Reintentar") }
                    }
                }
                is CitasUiState.Empty -> {
                    Text(
                        "No hay citas que coincidan con los filtros",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is CitasUiState.Success -> {
                    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                        items(uiState.items, key = { it.cita.id }) { item ->
                            CitaCard(
                                cita = item.cita,
                                esHoy = item.esHoy,
                                onClick = { onCitaClick(item.cita) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CitaCard(cita: Cita, esHoy: Boolean = false, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(cita.especialidad, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                if (esHoy) {
                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                        Text("Hoy", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }
            }

            Text(cita.medico)
            Text("${cita.fecha} - ${cita.hora}")
            Text("Sede: ${cita.sede}")
            Text(
                text = "Estado: " + when (cita.estado) {
                    is EstadoCita.Programada -> "Programada"
                    is EstadoCita.Atendida -> "Atendida"
                    is EstadoCita.Cancelada -> "Cancelada"
                },
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // SC-C: modalidad con icono
            Text(
                text = if (cita.modalidad == "Teleconsulta") "💻 Teleconsulta" else "📍 Presencial",
                color = Color(0xFF008000),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
