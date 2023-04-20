package sii.ms_evalexamenes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sii.ms_evalexamenes.entities.Examen;

public interface ExamenRepository extends JpaRepository<Examen, Long> {
    public Optional<List<Examen>> findByAlumnoId(Long alumnoId);
    public Optional<List<Examen>> findByCorrectorId(Long correctorId);
}
