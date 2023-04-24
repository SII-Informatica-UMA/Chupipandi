package sii.ms_corrector.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sii.ms_corrector.entities.MateriaEnConvocatoria;

public interface MateriaEnConvocatoriaRepository extends JpaRepository<MateriaEnConvocatoria, Long> {
    
    // Devuelve una LISTA de materias en convocatoria (no una solo):
    // dada una convocatoria (para posteriormente hacer un filtrado)
    List<MateriaEnConvocatoria> findByIdConvocatoria(Long idConvocatoria);
    
}
