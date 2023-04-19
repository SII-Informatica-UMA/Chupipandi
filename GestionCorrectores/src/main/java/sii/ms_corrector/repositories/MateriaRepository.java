package sii.ms_corrector.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import sii.ms_corrector.entities.Materia;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    
    boolean existsByIdMateria(Long id);
    Materia findByIdMateria(Long id);

    boolean existsByNombre(String nombre);
    Materia findByNombre(String nombre);

}
