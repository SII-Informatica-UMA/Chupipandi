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
import sii.ms_corrector.services.exceptions.CorrectorNoEncontrado;
import sii.ms_corrector.services.exceptions.CorrectorYaExiste;

@Service
@Transactional
public class CorrectorService {

    private CorrectorRepository corRepo;

    private MateriaEnConvocatoriaRepository matConvRepo;

    private MateriaService matService;

    // Logger para facilitar el desarrollo de los tests
    final static Logger LOG = Logger.getLogger("test.CorrectorTests");

    @Autowired
    public CorrectorService(CorrectorRepository corRepo,
                            MateriaEnConvocatoriaRepository matConvRepo,
                            MateriaService matService) {
        this.corRepo = corRepo;
        this.matConvRepo = matConvRepo;
        this.matService = matService;
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
        return corRepo.findById(id).orElseThrow(() -> new CorrectorNoEncontrado());
    }

    public Long añadirCorrector(CorrectorNuevoDTO nuevoCorrectorDTO) {
        Corrector nuevoCorrector = nuevoCorrectorDTO.corrector();
        if (corRepo.existsByIdUsuario(nuevoCorrector.getIdUsuario())) {
            throw new CorrectorYaExiste();
        }
        
        Materia mat = nuevoCorrectorDTO.getMateria().materia();
        
        // [x] Comprobar que la materia no exista ya
        // (Comprueba que existe verificando si pertenece al conjunto de las materias inicializadas)
        mat = matService.comprobarMateria(mat);
        
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

	public Corrector modificarCorrector(Long id, CorrectorNuevoDTO correctorMod) {
        Corrector entidadCorrector = correctorMod.corrector();
        entidadCorrector.setId(id);
		
        Corrector corrector = corRepo.findById(entidadCorrector.getId()).orElseThrow(() -> new CorrectorNoEncontrado());
        
        // Si tratamos de cambiarle el idUsuario por uno que ya existia, lanzamos un conflicto
        // Si 'ese que ya existia' es el propio corrector que se trata de modificar, no pasa nada
        if (corRepo.existsByIdUsuario(entidadCorrector.getIdUsuario()) && !entidadCorrector.getIdUsuario().equals(corrector.getIdUsuario())) {
            throw new CorrectorYaExiste();
        }
        if (entidadCorrector.getIdUsuario() != null) {
            corrector.setIdUsuario(entidadCorrector.getIdUsuario());
        }
        if (!entidadCorrector.getTelefono().isEmpty()) {
            corrector.setTelefono(entidadCorrector.getTelefono());
        }
        // Cuando no se modifica el valor de maximasCorrecciones, el valor que se recibe del formulario es 0, no null
        if (entidadCorrector.getMaximasCorrecciones() != null && entidadCorrector.getMaximasCorrecciones() != 0) {
            corrector.setMaximasCorrecciones(entidadCorrector.getMaximasCorrecciones());
        }

        if (correctorMod.getMateria() == null || correctorMod.getIdentificadorConvocatoria() == null) {
            return corRepo.save(corrector);
        }

        Materia mat = correctorMod.getMateria().materia();

        // [x] Comprobar que la materia no exista ya
        // (Comprueba que existe verificando si pertenece al conjunto de las materias inicializadas)
        mat = matService.comprobarMateria(mat);
        
        // [x] Comprobar que la convocatoria no exista ya
        // (ASUMIMOS QUE DOS CONVOCATORIAS SON IGUALES SI COINCIDEN UNICAMENTE EN EL IDCONVOCATORIA)
        // Compruebo si ya existe la convocatoria (por idConvocatoria), y en caso contrario la creo
        // Guardamos la nueva materia en convocatoria en su correspondiente repositorio
        Long idConv = correctorMod.getIdentificadorConvocatoria();
        MateriaEnConvocatoria matConv = new MateriaEnConvocatoria();

        // Recojo la lista de materias en convocatoria dada una convocatoria
        // (pueden haber varias, pues mas de un corrector puede tener una asignacion con la misma convocatoria)
        List<MateriaEnConvocatoria> listaPrueba = matConvRepo.findByIdConvocatoria(idConv);
        if (!listaPrueba.stream().anyMatch(mater -> mater.getCorrector().getId().equals(corrector.getId()))) {
            matConv.setId(null); 
            matConv.setCorrector(entidadCorrector);
            matConv.setIdConvocatoria(idConv);
            matConv.setMateria(mat);
            matConvRepo.save(matConv);
            List<MateriaEnConvocatoria> lista = corrector.getMatEnConv();
            lista.add(matConv);
            corrector.setMatEnConv(lista);
        }
        return corrector;
	}

	public void eliminarCorrector(Long id) {
		if (!corRepo.existsById(id))
			throw new CorrectorNoEncontrado();
        corRepo.deleteById(id);
	}
}