package sii.ms_evalexamenes.service;



import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.repositories.ExamenRepository;


@Service
@Transactional
public class ExamenDBService {

	
	
	private ExamenRepository examenRepository;


	@Autowired
	public ExamenDBService(ExamenRepository examenRepository) {
		this.examenRepository = examenRepository;
	}
	
	
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
	
	
}
