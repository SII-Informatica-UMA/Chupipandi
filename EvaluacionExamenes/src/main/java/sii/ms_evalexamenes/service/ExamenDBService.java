package sii.ms_evalexamenes.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sii.ms_evalexamenes.repositories.ExamenRepository;

@Service
@Transactional
public class ExamenDBService {

	
	
	private ExamenRepository examenRepository;

	@Autowired
	public ExamenDBService(ExamenRepository examenRepository) {
		this.examenRepository = examenRepository;
		
	}
}
