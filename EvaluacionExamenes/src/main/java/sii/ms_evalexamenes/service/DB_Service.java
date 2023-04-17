package sii.ms_evalexamenes.service;



import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.entities.Materia;
import sii.ms_evalexamenes.repositories.ExamenRepository;
import sii.ms_evalexamenes.repositories.MateriaRepository;
import sii.ms_evalexamenes.service.exceptions.AlreadyExistsException;
import sii.ms_evalexamenes.service.exceptions.NotFoundException;



@Service
@Transactional
public class DB_Service {

	
	
	private ExamenRepository examenRepository;
	private MateriaRepository materiaRepository;


	@Autowired
	public DB_Service(ExamenRepository examenRepository,MateriaRepository materiaRepository) {
		this.examenRepository = examenRepository;
		this.materiaRepository = materiaRepository;
	}
	
	
	
	
	//Examen
	public Optional<List<Examen>> getAllExamenes(){
        return Optional.of(examenRepository.findAll());
    }

    public Optional<Examen> getExamenById(Long id){
        return examenRepository.findById(id);
    }
	
	public Long addExamen(Examen examen) {
        if (examenRepository.existsById(examen.getId())) throw new AlreadyExistsException();
		examen.setId(null);
		examenRepository.save(examen);
        return examen.getId();
    }

	public void updateExamen(Examen examen) {
        Optional<Examen> exam = examenRepository.findById(examen.getId());
        exam.ifPresent(ex -> ex.setCalificacion(examen.getCalificacion()));
    }
	

	//Materias
	public Optional<List<Materia>> getAllMaterias(){
        return Optional.of(materiaRepository.findAll());
    }

    public Optional<Materia> getMateriaById(Long id) {
        return materiaRepository.findById(id);
    }
	
    //Esto es Inventada o bien se hace asi?
    public Optional<List<Materia>> getTodasMateriasPorConvocatoria(Long idConvocatoria) {
        return materiaRepository.findAllByIdConvocatoria(idConvocatoria);
    }
    
	
	public Long addMateria(Materia materia) {
        if (materiaRepository.existsById(materia.getId())) throw new AlreadyExistsException();

		materia.setId(null);
		materiaRepository.save(materia);
        return materia.getId();
    }

    public void deleteMateria(Long id) {

        /* 
         * if (materiaRepository.existsById(id)) {
			materiaRepository.deleteById(id);
		} else {
			throw new NotFoundException();
		}
        */
		materiaRepository.deleteById(id);
	}

   
	
}
