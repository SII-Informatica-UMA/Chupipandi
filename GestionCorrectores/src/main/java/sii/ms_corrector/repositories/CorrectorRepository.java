package sii.ms_corrector.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import sii.ms_corrector.entities.Corrector;

public interface CorrectorRepository extends CrudRepository<Corrector, Long> {

    boolean existsByIdUsuario(Long idUsuario);

    Optional<List<Corrector>> findAllByIdConvocatoria(Long idConvocatoria);
    
}
