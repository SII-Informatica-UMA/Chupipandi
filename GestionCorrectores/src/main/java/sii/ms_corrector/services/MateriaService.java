package sii.ms_corrector.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sii.ms_corrector.entities.Materia;
import sii.ms_corrector.repositories.MateriaRepository;
import sii.ms_corrector.services.exceptions.PeticionIncorrecta;

@Service
@Transactional
public class MateriaService {
    private MateriaRepository matRepo;

    private boolean inicializado = false;

    @Autowired
    public MateriaService(MateriaRepository matRepo) {
        this.matRepo = matRepo;
    }

    // Este metodo se usa para comprobar si una materia existe en la base de datos
    // Si es la primera vez que se ejecuta, inicializa la base de datos con un conjunto de materias predefinido
    public Materia comprobarMateria(Materia mat) {
        if (!inicializado) {
            // JaCoCo muestra esta rama del if como no cubierta.
            // Pero no podemos cubrirla porque dentro del ambito de los tests, 'inicializado'
            // siempre va a estar a true desde el momento en que se ejecuta el primer test
            // Podria cubrirse, pero habria que hacerlo con un test totalmente independiente de los demas,
            // donde no se le aplique el @BeforeEach que provoca la inicializacion de la BD
            // (hemos considerado que no merece la pena hacer otra clase nested solo para este test)
            inicializar();
            inicializado = true;
        }
        if (mat.getIdMateria() != null && matRepo.existsByIdMateria(mat.getIdMateria())) {
            return matRepo.findByIdMateria(mat.getIdMateria());
        } else if (mat.getNombre() != null && matRepo.existsByNombre(mat.getNombre())){
            return matRepo.findByNombre(mat.getNombre());
        } else {
            // error en la peticion, id y nombre a nulos o no existe esa materia
            throw new PeticionIncorrecta();
        }
    }
    
    public void yaInicializado() {
        inicializado = true;
    }

    public void inicializar() {
        Materia mat = new Materia();
        mat.setNombre("Matemáticas II");
        mat.setIdMateria(1L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Física");
        mat.setIdMateria(2L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Química");
        mat.setIdMateria(3L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Lengua Castellana y Literatura");
        mat.setIdMateria(4L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Historia de España");
        mat.setIdMateria(5L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Geografía");
        mat.setIdMateria(6L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Biología");
        mat.setIdMateria(7L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Lengua Extranjera");
        mat.setIdMateria(8L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Dibujo Técnico II");
        mat.setIdMateria(9L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Economía");
        mat.setIdMateria(10L);
        matRepo.save(mat);

        mat = new Materia();
        mat.setNombre("Filosofía");
        mat.setIdMateria(11L);
        matRepo.save(mat);
    }
}
