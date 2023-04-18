package sii.ms_corrector.services;

import java.util.ArrayList;
import java.util.List;

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
        // [ ] Gestionar las nuevas materias por separado
        // (falta comprobar que la materia no exista ya)
        
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

	public void modificarCorrector(Long id, CorrectorNuevoDTO correctorMod) {
        Corrector entidadCorrector = correctorMod.corrector();
        entidadCorrector.setId(id);
		if (!corRepo.existsById(entidadCorrector.getId())) {
			throw new CorrectorNoEncontrado();
        }
        Corrector corrector = corRepo.findById(entidadCorrector.getId()).get();
        
        corrector.setIdUsuario(entidadCorrector.getIdUsuario());
        corrector.setTelefono(entidadCorrector.getTelefono());
        corrector.setMaximasCorrecciones(entidadCorrector.getMaximasCorrecciones());

        // [ ] Gestionar las nuevas materias por separado
        // (falta comprobar que la materia no exista ya)

        // Guardamos la nueva materia en su correspondiente repositorio
        // Capaz habria que comprobar que exista o que pertenezca a un conjunto de posibilidades
        Materia mat = correctorMod.getMateria().materia();
        mat.setId(null);
        matRepo.save(mat);
        
        // Guardamos la nueva materia en convocatoria en su correspondiente repositorio
        Long idConv = correctorMod.getIdentificadorConvocatoria();
        MateriaEnConvocatoria matConv = new MateriaEnConvocatoria();
        matConv.setId(null);
        matConv.setCorrector(entidadCorrector);
        matConv.setIdConvocatoria(idConv);
        matConv.setMateria(mat);
        matConvRepo.save(matConv);

        List<MateriaEnConvocatoria> lista = entidadCorrector.getMatEnConv();
        if (lista == null) {
            lista = new ArrayList<>();
        }
        lista.add(matConv);
        corrector.setMatEnConv(lista);;
        
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