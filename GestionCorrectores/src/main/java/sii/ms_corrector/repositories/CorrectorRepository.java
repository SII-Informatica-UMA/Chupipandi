package sii.ms_corrector.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import sii.ms_corrector.entities.Corrector;

public interface CorrectorRepository extends JpaRepository<Corrector, Long> {

    boolean existsByIdUsuario(Long idUsuario);

}
