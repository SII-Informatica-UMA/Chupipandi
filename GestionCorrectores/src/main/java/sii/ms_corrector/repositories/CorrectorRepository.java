package sii.ms_corrector.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sii.ms_corrector.entities.Corrector;

public interface CorrectorRepository extends JpaRepository<Corrector, Long> {

    boolean existsByIdUsuario(Long idUsuario);

    @Query("select c from Corrector c, MateriaEnConvocatoria m where m.idConvocatoria = :convoc")
    List<Corrector> findAllByIdConvocatoria(@Param("convoc") Long idConvocatoria);

}
