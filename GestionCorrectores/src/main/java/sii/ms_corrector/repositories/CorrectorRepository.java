package sii.ms_corrector.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sii.ms_corrector.entities.Corrector;

public interface CorrectorRepository extends JpaRepository<Corrector, Long> {

    boolean existsByIdUsuario(Long idUsuario);

    List<Corrector> findAllByIdConvocatoria(Long idConvocatoria);
    
}
