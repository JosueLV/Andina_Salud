package pe.edu.upeu.andinasalud.di

import org.koin.dsl.module
import pe.edu.upeu.andinasalud.data.local.RelojSistema
import pe.edu.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.edu.upeu.andinasalud.domain.repository.CitaRepository
import pe.edu.upeu.andinasalud.domain.repository.Reloj
import pe.edu.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.edu.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase
import pe.edu.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.edu.upeu.andinasalud.presentation.citas.CitasViewModel

val appModule = module {
    // Unica linea a cambiar cuando exista la API: CitaRepositoryFake() -> CitaRepositoryApi()
    single<CitaRepository> { CitaRepositoryFake() }
    single<Reloj> { RelojSistema() }

    // Casos de uso
    factory { ObtenerCitasUseCase(get()) }
    factory { SolicitarCitaUseCase(get(), get()) }
    factory { CancelarCitaUseCase(get(), get()) }
    factory { ReprogramarCitaUseCase(get(), get()) }

    // ViewModels
    factory { CitasViewModel(get()) }
}
