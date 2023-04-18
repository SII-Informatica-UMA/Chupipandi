package sii.ms_corrector.dtos;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sii.ms_corrector.entities.Corrector;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrectorDTO {
    /*
     * Puede ser que se simplifique el problema usando ModelMapper,
     * tanto para entidades locales como externas quiero pensar
     */
    private Long id;
    private Long identificadorUsuario;
    private String telefono;
    private int maximasCorrecciones;
    // Set ya que son unicas
    private Set<MateriaEnConvocatoriaDTO> materias;

    public static CorrectorDTO fromCorrector(Corrector corrector) {
        var dto = new CorrectorDTO();
        dto.setId(corrector.getId());
        dto.setIdentificadorUsuario(corrector.getIdUsuario());  // de donde lo obtenemos?
        dto.setTelefono(corrector.getTelefono());  // de donde lo obtenemos?
        dto.setMaximasCorrecciones(corrector.getMaximasCorrecciones());
        // que son las materias en convocatoria?
        // El id de la materia deberia ser siempre el mismo, puesto que cada corrector solo puede estar especializado
        // en una materia.
        // El id de la convocatoria entiendo yo que es lo unico que varia, ... habria que obtenerlo de la base de datos?
        dto.setMaterias(Set.of(
            MateriaEnConvocatoriaDTO.builder()
            .idMateria(corrector.getMateriaEspecialista())
            .idConvocatoria(0L).build()));
        return dto;
    }

    public Corrector corrector() {
        var correct = new Corrector();
        correct.setId(id);
        correct.setIdUsuario(identificadorUsuario);
        correct.setTelefono(telefono);
        // El id de la materia deberia ser siempre el mismo, puesto que cada corrector solo puede estar especializado
        // un corrector siempre tendra un array con al menos una materia, no?
        correct.setMateriaEspecialista(materias.stream().findFirst().get().getIdMateria());
        correct.setMaximasCorrecciones(maximasCorrecciones);
        return correct;
    }
}