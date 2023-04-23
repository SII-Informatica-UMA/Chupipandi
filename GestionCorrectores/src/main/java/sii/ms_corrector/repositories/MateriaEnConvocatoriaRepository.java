package sii.ms_corrector.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sii.ms_corrector.entities.MateriaEnConvocatoria;

public interface MateriaEnConvocatoriaRepository extends JpaRepository<MateriaEnConvocatoria, Long> {
    
    // @Query("select case when (count(m) > 0) then true else false end from MateriaEnConvocatoria m, Corrector c where m.idConvocatoria = :convoc AND c.id = :corr")
    // boolean existsByIdConvocatoriaAndCorrector(@Param("convoc") Long id, @Param("corr") Long idCorrector);

    @Query("select m from MateriaEnConvocatoria m, Corrector c where m.idConvocatoria = :convoc AND c.id = :corr")
    MateriaEnConvocatoria findByIdConvocatoriaAndCorrector(@Param("convoc") Long id, @Param("corr") Long idCorrector);
    
    List<MateriaEnConvocatoria> findByIdConvocatoria(Long idConvocatoria);
    
}
