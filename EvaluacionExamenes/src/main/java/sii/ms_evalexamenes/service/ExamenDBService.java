package sii.ms_evalexamenes.service;



import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.entities.Materia;
import sii.ms_evalexamenes.repositories.ExamenRepository;
import sii.ms_evalexamenes.repositories.MateriaRepository;


@Service
@Transactional
public class ExamenDBService {

	
	
	private ExamenRepository examenRepository;
	private MateriaRepository materiaRepository;


	@Autowired
	public ExamenDBService(ExamenRepository examenRepository) {
		this.examenRepository = examenRepository;
	}
	
	
	
	
	//Service
	
	
	public Optional<Examen> get_Examen_By_Id(Long id) {
		return examenRepository.findById(id);
	}
	
	public List<Examen> get_All_Examen() {
		return examenRepository.findAll();
	}
	
	public Long add_Examen(Examen examen) {
		examen.setId(null);
		examenRepository.save(examen);
		return examen.getId();
	}
		
	public void delete_Examen(Long idExamen) throws Exception {
		if (examenRepository.existsById(idExamen)) {
			examenRepository.deleteById(idExamen);
		} else {
			throw new Exception("Examen no Encontrado, no se ha eliminado ningún Examen");
		}
	}
	
	
	public void modify_Examen(Long idExamen, Examen examen) throws Exception {
		if (examenRepository.existsById(idExamen)) {
			examenRepository.save(examen);
		} else {
			throw new Exception("Examen no Encontrado, no se ha modificado ningún Examen");
		}
	}
	
	
	//Materia
	
	public Optional<Materia> get_Materia_By_Id(Long id) {
		return materiaRepository.findById(id);
	}
	
	
	/*
	public List<Materia> get_All_Materias() {
		return materiaRepository.findAll();
	}
	*/
	
	public Optional<List<Materia>> get_All_Materias() {
        return Optional.of((List<Materia>) materiaRepository.findAll());
    }
	
	public Long add_Materia(Materia examen) {
		examen.setId(null);
		materiaRepository.save(examen);
		return examen.getId();
	}
		
	
	
	public void delete_Materia(Long idMateria) throws Exception {
		if (materiaRepository.existsById(idMateria)) {
			materiaRepository.deleteById(idMateria);
		} else {
			throw new Exception("Materia no Encontrada, no se ha eliminado ningúna Materia");
		}
	}
	
	
	public void modify_Materia(Long idMateria, Materia materia) throws Exception {
		if (materiaRepository.existsById(idMateria)) {
			materiaRepository.save(materia);
		} else {
			throw new Exception("Materia no Encontrada, no se ha modificado ningúna Materia");
		}
	}
	
	
	
	
	
}
