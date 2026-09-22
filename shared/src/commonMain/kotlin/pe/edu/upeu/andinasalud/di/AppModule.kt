package pe.edu.upeu.andinasalud.di



import org.koin.dsl.module
import pe.edu.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.edu.upeu.andinasalud.domain.repository.CitaRepository
import pe.edu.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.edu.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.edu.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.edu.upeu.andinasalud.presentation.citas.CitasViewModel

val appModule = module {
    // Repositorio (Inyectamos la interfaz y le pasamos la implementación falsa)
    single<CitaRepository> { CitaRepositoryFake() }

    // Casos de Uso
    factory { ObtenerCitasUseCase(get()) }
    factory { SolicitarCitaUseCase() }
    factory { CancelarCitaUseCase() }
    // ViewModels
    factory { CitasViewModel(get()) }
}