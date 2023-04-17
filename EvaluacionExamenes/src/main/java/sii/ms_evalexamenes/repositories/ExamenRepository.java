package sii.ms_evalexamenes.repositories;



import org.springframework.data.jpa.repository.JpaRepository;

import sii.ms_evalexamenes.entities.Examen;

public interface ExamenRepository extends JpaRepository<Examen, Long> {

}
