package sii.ms_evalexamenes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import sii.ms_evalexamenes.entities.Materia;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    
}
