package sii.ms_evalexamenes.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.repositories.ExamenRepository;
import sii.ms_evalexamenes.services.exceptions.AlreadyExistsException;
import sii.ms_evalexamenes.services.exceptions.NotFoundException;

@Service
@Transactional
public class ExamenService {
    
    private ExamenRepository repo;
    
    @Autowired
    public ExamenService(ExamenRepository repo) {
        this.repo = repo;
    }

    public Optional<List<Examen>> getAllExamenes(){
        return Optional.of(repo.findAll());
    }

    public Optional<Examen> getExamenById(Long id) {
        return repo.findById(id);
    }

    public Long addExamen(Examen examen) {
        if (examen.getId() != null && repo.existsById(examen.getId()))
            throw new AlreadyExistsException();
        examen.setId(null);
        repo.save(examen);
        return examen.getId();
    }

    public void updateExamen(Examen examen) {
        if (!repo.existsById(examen.getId()))
            throw new NotFoundException();
        Optional<Examen> exam = repo.findById(examen.getId());
        exam.ifPresent(ex -> ex.setCalificacion(examen.getCalificacion()));
    }

    public Optional<List<Examen>> getExamenByDniAndApellido(Long dni, String apellido) {
        return repo.findByAlumnoId(dni);
    }

    public Optional<List<Examen>> getCorrectoresById(Long correctorId) {
        return repo.findByCorrectorId(correctorId);
    }
}
