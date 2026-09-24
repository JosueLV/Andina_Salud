package pe.edu.upeu.andinasalud.domain.usecase

import pe.edu.upeu.andinasalud.domain.model.Cita
import pe.edu.upeu.andinasalud.domain.model.EstadoCita
import pe.edu.upeu.andinasalud.domain.model.SolicitudCita
import pe.edu.upeu.andinasalud.domain.model.SolicitudInvalidaException
import pe.edu.upeu.andinasalud.domain.repository.CitaRepository
import pe.edu.upeu.andinasalud.domain.repository.Reloj
import pe.edu.upeu.andinasalud.domain.rules.ReglasCita

class SolicitarCitaUseCase(
    private val repository: CitaRepository,
    private val reloj: Reloj
) {
    suspend operator fun invoke(solicitud: SolicitudCita): Result<Cita> {
        val citas = repository.obtenerCitasActuales()
        val medicos = repository.obtenerMedicos()

        val errores = ReglasCita.validarSolicitud(solicitud, citas, medicos, reloj.ahora())
        if (errores.hayErrores) return Result.failure(SolicitudInvalidaException(errores))

        val medico = medicos.first {
            it.especialidad == solicitud.especialidad && solicitud.sede in it.sedes
        }
        val nueva = Cita(
            especialidad = solicitud.especialidad,
            medico = medico.nombre,
            fecha = solicitud.fecha.trim(),
            hora = solicitud.hora.trim(),
            sede = solicitud.sede,
            modalidad = solicitud.modalidad,
            estado = EstadoCita.Programada(recordatorioActivo = true)
        )
        return Result.success(repository.agregar(nueva))
    }
}
