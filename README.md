# AndinaSalud

Aplicación móvil multiplataforma (Kotlin Multiplatform + Compose Multiplatform) para que un
paciente de la red de centros médicos AndinaSalud gestione sus citas desde su propio teléfono.

Proyecto desarrollado para el **Examen Parcial de la Unidad 1** del curso **Desarrollo de
Aplicaciones Móviles** — VI Ciclo, EP Ingeniería de Sistemas, UPeU.

> Esta versión trabaja con **datos simulados en memoria**. No consume red ni base de datos:
> ese es el punto central del caso (validar el flujo con pacientes reales antes de que el
> área de sistemas entregue la API REST de citas).

## Autor

**Llontop Vivas, Walther Josue** — examen desarrollado de forma individual.

## Estructura de paquetes

```
shared/src/commonMain/kotlin/pe/edu/upeu/andinasalud/
├── domain/
│   ├── model/          Cita, EstadoCita (sealed class), Medico, Paciente, FechaHora,
│   │                   SolicitudCita, ErroresSolicitud, CriteriosCitas
│   ├── repository/     CitaRepository (interfaz) y Reloj (interfaz)
│   ├── rules/          ReglasCita: las 5 reglas de negocio (RN-01 a RN-05) en un solo lugar
│   └── usecase/        ObtenerCitasUseCase, FiltrarCitasUseCase, SolicitarCitaUseCase,
│                       CancelarCitaUseCase, ReprogramarCitaUseCase, ObtenerProximaCitaUseCase,
│                       ObtenerPacienteUseCase, ObtenerOpcionesSolicitudUseCase
├── data/
│   ├── local/          CitasSimuladas (datos semilla), RelojSistema (implementa Reloj)
│   └── repository/      CitaRepositoryFake (implementa CitaRepository, con delay de 800 ms)
├── presentation/
│   ├── citas/           CitasScreen, CitasViewModel, CitasUiState
│   ├── inicio/          InicioScreen, InicioViewModel, InicioUiState
│   ├── detalle/         DetalleCitaScreen, DetalleCitaViewModel, DetalleCitaUiState
│   ├── solicitud/       SolicitudScreen, SolicitudViewModel, SolicitudUiState
│   ├── perfil/          PerfilScreen, PerfilViewModel, PerfilUiState
│   ├── navigation/      AppNavHost, Destinos
│   ├── components/      EncabezadoConVolver (composable reutilizable)
│   └── theme/           Color, Type, AndinaSaludTheme (Material 3, modo claro/oscuro)
└── di/                  AppModule (Koin)
```

## Decisiones de arquitectura

**Clean Architecture + MVVM.** El dominio (`domain`) no depende de Android, iOS ni de
Compose: solo define modelos, la interfaz `CitaRepository`, la interfaz `Reloj` y las
reglas de negocio. La capa `data` implementa esas interfaces con una fuente en memoria.
La capa `presentation` solo conoce al dominio a través de los casos de uso.

**Por qué el cambio a la API real es una sustitución localizada.** Cuando el área de
sistemas entregue la API REST, basta con:
1. Crear `CitaRepositoryApi` (o similar) que implemente `CitaRepository` usando Ktor.
2. Cambiar una sola línea en `di/AppModule.kt`:
   `single<CitaRepository> { CitaRepositoryFake() }` → `single<CitaRepository> { CitaRepositoryApi() }`

Ningún ViewModel, pantalla ni caso de uso cambia, porque todos dependen de la interfaz
`CitaRepository`, no de su implementación.

**Por qué el estado de la cita es una `sealed class` y no un enum o un texto.** Cada
estado lleva información distinta y solo válida para ese estado: una cita `Programada`
tiene `recordatorioActivo`, una `Atendida` tiene `indicaciones`, y una `Cancelada` tiene
`motivo` y `canceladaPorPaciente`. Con un enum o un `String` esos datos quedarían sueltos
o anulables (`String?`) en toda la clase `Cita`, aunque solo tengan sentido en un estado.
Con la `sealed class`, el compilador obliga a manejar los tres casos (el `when` es
exhaustivo) y cada rama solo ve los datos que le corresponden.

**Dónde vive cada regla de negocio (RN-01 a RN-05).** Todas están en
`domain/rules/ReglasCita.kt`, no en las pantallas ni en los ViewModels:
- **RN-01** (no reservar en el pasado): `validarFechaHora`, comparando con `Reloj.ahora()`.
- **RN-02** (máximo 3 Programadas): `limiteAlcanzado` / `cantidadProgramadas`.
- **RN-03** (cancelar solo con 24 h de anticipación): `motivoNoCancelable` / `puedeCancelar`.
- **RN-04** (motivo entre 10 y 200 caracteres): `validarMotivo`.
- **RN-05** (no duplicar fecha y hora): `hayConflictoHorario`.

La interfaz solo lee el resultado (un mensaje de error o un booleano); nunca vuelve a
evaluar la condición.

**Asincronía.** Cada ViewModel lanza sus corrutinas en `viewModelScope`, así que se
cancelan solas si la pantalla se destruye. El retardo de carga simulado (RF-08, 800 ms)
vive en `CitaRepositoryFake.obtenerCitas()`, usando `delay()` dentro de un `Flow`.

**Estado de la interfaz (RF-08).** Cada pantalla que carga datos expone un
`sealed interface UiState` con los cuatro casos: `Loading`, `Success`, `Empty` y `Error`,
y el composable solo dibuja según el caso actual.

## Solicitudes de cambio (Parte II)

| Código | Dónde vive la solución |
|---|---|
| SC-A (chip "Hoy") | `FiltrarCitasUseCase`, combinado con el filtro de estado existente |
| SC-B (indicador de citas Programadas y límite) | `ReglasCita.limiteAlcanzado`, leído en `InicioViewModel` y `CitasViewModel`; el contador se pinta en la barra inferior en `AppNavHost` |
| SC-C (modalidad Presencial/Teleconsulta) | Campo `modalidad` en `Cita`, presente en el dominio, el formulario, el detalle y la lista, con su propio ícono |
| SC-D (reprogramar cita) | `ReprogramarCitaUseCase`, que reutiliza `validarFechaHora` y `hayConflictoHorario` de `ReglasCita` en vez de duplicar la validación |

## Cómo ejecutar el proyecto

1. Abrir el proyecto en Android Studio (versión reciente, con soporte KMP).
2. Sincronizar Gradle (`Sync Project with Gradle Files`).
3. Elegir el módulo `androidApp` y ejecutar en un emulador o dispositivo Android.
4. Para correr las pruebas unitarias del dominio:
   ```
   ./gradlew :shared:testAndroidHostTest
   ```

## Datos simulados

- 1 paciente fijo (Lucía Quispe Mamani).
- 4 sedes: Ñaña, Chosica, Chaclacayo, Santa Anita.
- 5 especialidades, con 2 médicos cada una (10 médicos en total).
- 6 citas de ejemplo: 3 Programadas (fechas futuras), 2 Atendidas, 1 Cancelada.

## Trabajo colaborativo con Git

El examen se desarrolló de forma individual. El flujo de ramas siguió el esquema pedido
en el enunciado (`main` / `develop` / `feature/<funcionalidad>-llontop` /
`sc-<letra>-llontop`), con una rama por funcionalidad, fusionada a `develop` mediante
pull requests en GitHub:

1. `feature/reglas-dominio-llontop` — reglas de negocio y casos de uso.
2. `feature/viewmodels-llontop` — ViewModels, UiState y filtrado.
3. `feature/pantallas-llontop` — pantallas conectadas a los ViewModels y navegación.

El commit evaluado en `main` está etiquetado como `v1.0-unidad1`.
