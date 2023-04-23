package sii.ms_corrector.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

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
import sii.ms_corrector.services.exceptions.PeticionIncorrecta;

@Service
@Transactional
public class CorrectorService {

    private CorrectorRepository corRepo;
    private MateriaEnConvocatoriaRepository matConvRepo;
    private MateriaRepository matRepo;

    final static Logger LOG = Logger.getLogger("test.CorrectorTests");

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

    public List<Corrector> getTodosCorrectoresByConvocatoria(Long idConvocatoria) {
        List<Corrector> lista = corRepo.findAllByIdConvocatoria(idConvocatoria);
        // Para cada corrector, actualizo su lista de materias en convocatoria
        // para mostrar unicamente aquellas que coinciden con la convocatoria pasada por parametro
        lista.forEach(
            corrector -> 
                corrector.setMatEnConv(corrector.getMatEnConv()
                                                .stream()
                                                .filter(materia -> materia.getIdConvocatoria() == idConvocatoria)
                                                .toList()));
        // Elimino los correctores cuya lista ha quedado vacia (porque no contiene la convocatoria que buscamos)
        Iterator<Corrector> it = lista.iterator();
        while (it.hasNext()) {
            Corrector c = it.next();
            if (c.getMatEnConv().isEmpty()) {
                it.remove();
            }
        }
        return lista;
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
        
        // Guardamos la nueva materia en su correspondiente repositorio
        // Capaz habria que asegurarse que exista o que pertenezca a un conjunto de posibilidades
        Materia mat = nuevoCorrectorDTO.getMateria().materia();
        // [x] Comprobar que la materia no exista ya
        // Compruebo si ya existe la materia (por id o por nombre), y en caso contrario la creo
        if (mat.getIdMateria() != null && matRepo.existsByIdMateria(mat.getIdMateria())) {
            mat = matRepo.findByIdMateria(mat.getIdMateria());
        } else if (mat.getNombre() != null && matRepo.existsByNombre(mat.getNombre())) {
            mat = matRepo.findByNombre(mat.getNombre());
        } else if (mat.getIdMateria() != null || mat.getNombre() != null) {
            mat.setId(null);
        } else {
            throw new PeticionIncorrecta();
        }
        matRepo.save(mat);
        
        // Guardamos la nueva materia en convocatoria en su correspondiente repositorio
        Long idConv = nuevoCorrectorDTO.getIdentificadorConvocatoria();
        MateriaEnConvocatoria matConv = new MateriaEnConvocatoria();
        matConv.setId(null);
        matConv.setCorrector(nuevoCorrector);
        matConv.setIdConvocatoria(idConv);
        matConv.setMateria(mat);
        matConvRepo.save(matConv);

        List<MateriaEnConvocatoria> lista = new ArrayList<>();
        lista.add(matConv);
        nuevoCorrector.setMatEnConv(lista);

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
        
        // Si tratamos de cambiarle el idUsuario por uno que ya existia, lanzamos un conflicto
        // Si 'ese que ya existia' es el propio corrector que se trata de modificar, no pasa nada
        if (corRepo.existsByIdUsuario(entidadCorrector.getIdUsuario()) && !entidadCorrector.getIdUsuario().equals(corrector.getIdUsuario())) {
            throw new CorrectorYaExiste();
        }
        corrector.setIdUsuario(entidadCorrector.getIdUsuario());
        corrector.setTelefono(entidadCorrector.getTelefono());
        corrector.setMaximasCorrecciones(entidadCorrector.getMaximasCorrecciones());

        // [ ] Gestionar las nuevas materias por separado
        // (falta comprobar que la materia no exista ya)

        // Guardamos la nueva materia en su correspondiente repositorio
        // Capaz habria que comprobar que exista o que pertenezca a un conjunto de posibilidades
        Materia mat = correctorMod.getMateria().materia();

        // [x] Comprobar que la materia no exista ya
        // Compruebo si ya existe la materia (por id o por nombre), y en caso contrario la creo
        if (mat.getIdMateria() != null && matRepo.existsByIdMateria(mat.getIdMateria())) {
            mat = matRepo.findByIdMateria(mat.getIdMateria());
        } else if (mat.getNombre() != null && matRepo.existsByNombre(mat.getNombre())) {
            mat = matRepo.findByNombre(mat.getNombre());
        } else if (mat.getIdMateria() != null || mat.getNombre() != null) {
            mat.setId(null);
        } else {
            throw new PeticionIncorrecta();
        }
        matRepo.save(mat);
        
        // [x] Comprobar que la convocatoria no exista ya
        // (ASUMIMOS QUE DOS CONVOCATORIAS SON IGUALES SI COINCIDEN UNICAMENTE EN EL IDCONVOCATORIA)
        // Compruebo si ya existe la convocatoria (por idConvocatoria), y en caso contrario la creo
        // Guardamos la nueva materia en convocatoria en su correspondiente repositorio
        Long idConv = correctorMod.getIdentificadorConvocatoria();
        MateriaEnConvocatoria matConv = new MateriaEnConvocatoria();

        // Comentar
        List<MateriaEnConvocatoria> listaPrueba = matConvRepo.findByIdConvocatoria(idConv);
        if (listaPrueba.stream().anyMatch(mater -> mater.getCorrector().getId().equals(corrector.getId()))) {
            // matConv = matConvRepo.findByIdConvocatoriaAndCorrector(idConv, corrector.getId());
        } else {
            matConv.setId(null); 
            matConv.setCorrector(entidadCorrector);
            matConv.setIdConvocatoria(idConv);
            matConv.setMateria(mat);
            matConvRepo.save(matConv);
        }

        // [x] Si la materia en convocatoria ya esta asociada, no la vuelvo a incluir
        // [ ] Qué hace que dos materias en convocatoria sean iguales?
        List<MateriaEnConvocatoria> lista = corrector.getMatEnConv();
        if (!lista.contains(matConv)) {
            lista.add(matConv);
        }
        corrector.setMatEnConv(lista);

	}

	public void eliminarCorrector(Long id) {
		if (corRepo.existsById(id)) {
			corRepo.deleteById(id);
		} else {
			throw new CorrectorNoEncontrado();
		}
	}

}