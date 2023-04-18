package sii.ms_corrector.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sii.ms_corrector.dtos.CorrectorNuevoDTO;
import sii.ms_corrector.entities.Corrector;
import sii.ms_corrector.entities.Materia;
import sii.ms_corrector.entities.MateriaEnConvocatoria;
import sii.ms_corrector.repositories.CorrectorRepository;
import sii.ms_corrector.repositories.MateriaEnConvocatoriaRepository;
import sii.ms_corrector.repositories.MateriaRepository;
import sii.ms_corrector.services.exceptions.CorrectorNoEncontrado;
import sii.ms_corrector.services.exceptions.CorrectorYaExiste;

@Service
@Transactional
public class CorrectorService {

    private CorrectorRepository corRepo;
    private MateriaEnConvocatoriaRepository matConvRepo;
    private MateriaRepository matRepo;

    @Autowired
    public CorrectorService(CorrectorRepository corRepo,
                            MateriaEnConvocatoriaRepository matConvRepo,
                            MateriaRepository matRepo) {
        this.corRepo = corRepo;
        this.matConvRepo = matConvRepo;
        this.matRepo = matRepo;
    }

    public List<Corrector> getTodosCorrectores() {
        return corRepo.findAll();
    }

    // TODO
    public List<Corrector> getTodosCorrectoresByConvocatoria(Long idConvocatoria) {
        // return corRepo.findAllByIdConvocatoria(idConvocatoria);
        return null;
    }

    public Corrector getCorrectorById(Long id) {
        if (!corRepo.existsById(id)) {
            throw new CorrectorNoEncontrado();
        }
        return corRepo.findById(id).get();
    }

    public Long añadirCorrector(CorrectorNuevoDTO nuevoCorrectorDTO) {
        Corrector nuevoCorrector = nuevoCorrectorDTO.corrector();
        if (corRepo.existsByIdUsuario(nuevoCorrector.getIdUsuario())) {
            throw new CorrectorYaExiste();
        }
        // Guardamos la nueva materia en su correspondiente repositorio
        // Capaz habria que comprobar que exista o que pertenezca a un conjunto de posibilidades
        Materia mat = nuevoCorrectorDTO.getMateria().materia();
        mat.setId(null);
        matRepo.save(mat);
        
        // Guardamos la nueva materia en convocatoria en su correspondiente repositorio
        Long idConv = nuevoCorrectorDTO.getIdentificadorConvocatoria();
        MateriaEnConvocatoria matConv = new MateriaEnConvocatoria();
        matConv.setId(null);
        matConv.setCorrector(nuevoCorrector);
        matConv.setIdConvocatoria(idConv);
        matConv.setMateria(mat);
        matConvRepo.save(matConv);

        // Finalmente guardamos el nuevo corrector
        nuevoCorrector.setId(null);
		corRepo.save(nuevoCorrector);
		return nuevoCorrector.getId();
    }

	public void modificarCorrector(Corrector corrector) {
		if (!corRepo.existsById(corrector.getId())) {
			throw new CorrectorNoEncontrado();
        }
        Optional<Corrector> correctorRepo = corRepo.findById(corrector.getId());
        correctorRepo.ifPresent(c->c.setMateriaEspecialista(corrector.getMateriaEspecialista()));
        
        // (Campos sacados del esquema de la API)
        // se permite cambiar el id de la base de datos?
        // o acaso 'id' en corrector es independiente
        // y que es 'identificadorUsuario'
        correctorRepo.ifPresent(c -> c.setId(corrector.getId()));
        correctorRepo.ifPresent(c -> c.setIdUsuario(corrector.getIdUsuario()));
        correctorRepo.ifPresent(c -> c.setTelefono(corrector.getTelefono()));
        correctorRepo.ifPresent(c -> c.setMaximasCorrecciones(corrector.getMaximasCorrecciones()));
        // que es exactamente la lista de materias en convocatoria?
	}

	public void eliminarCorrector(Long id) {
		if (corRepo.existsById(id)) {
			corRepo.deleteById(id);
		} else {
            // seria AccesoNoAutorizado? cuando salta esta excepcion?
            // gestion de usuarios se encarga de los roles, nos importa en algo a nosotros?
            // como diferencio quien puede acceder y quien no?
			throw new CorrectorNoEncontrado();
		}
	}

}