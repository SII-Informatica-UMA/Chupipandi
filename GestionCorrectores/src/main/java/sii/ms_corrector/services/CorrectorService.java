package sii.ms_corrector.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sii.ms_corrector.entities.Corrector;
import sii.ms_corrector.repositories.CorrectorRepository;
import sii.ms_corrector.services.exceptions.CorrectorNoEncontrado;
import sii.ms_corrector.services.exceptions.CorrectorYaExiste;

@Service
@Transactional
public class CorrectorService {

    private CorrectorRepository repository;

    @Autowired
    public CorrectorService(CorrectorRepository repository) {
        this.repository = repository;
    }

    public List<Corrector> getTodosCorrectores() {
        return (List<Corrector>) repository.findAll();
    }

    public Optional<List<Corrector>> getTodosCorrectoresByConvocatoria(Long idConvocatoria) {
        return repository.findByIdConvocatoria(idConvocatoria);
    }

    public Optional<Corrector> getCorrectorById(Long id) {
        return repository.findById(id);
    }

    public Long añadirCorrector(Corrector nuevoCorrector) {
        if (repository.existsById(nuevoCorrector.getId())) {
            throw new CorrectorYaExiste();
        }
        nuevoCorrector.setId(null);
		repository.save(nuevoCorrector);
		return nuevoCorrector.getId();
    }

	public void modificarCorrector(Corrector corrector) {
		if (!repository.existsById(corrector.getId())) {
			throw new CorrectorNoEncontrado();
        }
        Optional<Corrector> correctorRepo = repository.findById(corrector.getId());
        correctorRepo.ifPresent(c->c.setExamenes(corrector.getExamenes()));
        correctorRepo.ifPresent(c->c.setMateriaEspecialista(corrector.getMateriaEspecialista()));
        correctorRepo.ifPresent(c->c.setNumeroMaximoExamenes(corrector.getNumeroMaximoExamenes()));
	}

	public void eliminarCorrector(Long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		} else {
            // seria AccesoNoAutorizado? cuando salta esta excepcion?
            // gestion de usuarios se encarga de los roles, nos importa en algo a nosotros?
            // como diferencio quien puede acceder y quien no?
			throw new CorrectorNoEncontrado();
		}
	}

}