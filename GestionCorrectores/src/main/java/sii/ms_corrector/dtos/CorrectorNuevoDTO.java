package sii.ms_corrector.dtos;

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
public class CorrectorNuevoDTO {
    /*
     * Puede ser que se simplifique el problema usando ModelMapper,
     * tanto para entidades locales como externas quiero pensar
     */
    private Long identificadorUsuario;
    private Long identificadorConvocatoria;
    private String telefono;
    private MateriaDTO materia;
    private int maximasCorrecciones;

    // usaremos este metodo?
    public static CorrectorNuevoDTO fromCorrector(Corrector corrector) {
        var dto = new CorrectorNuevoDTO();
        dto.setIdentificadorUsuario(corrector.getIdUsuario());             // de donde lo obtenemos?
        dto.setIdentificadorConvocatoria(corrector.getIdConvocatoria());    // de donde lo obtenemos?
        dto.setTelefono(corrector.getTelefono());                                      // de donde lo obtenemos?
        // de donde obtenemos el nombre de la materia? (capaz no usamos este metodo 'fromCorrector')
        // (de momento no se usa)
        dto.setMateria(MateriaDTO.builder().id(corrector.getMateriaEspecialista()).nombre("").build());
        dto.setMaximasCorrecciones(corrector.getMaximasCorrecciones());
        return dto;
    }

    public Corrector corrector() {
        var correct = new Corrector();
        correct.setIdConvocatoria(identificadorConvocatoria);
        correct.setIdUsuario(identificadorUsuario);
        correct.setTelefono(telefono);
        correct.setMateriaEspecialista(materia.getId());
        correct.setMaximasCorrecciones(maximasCorrecciones);
        return correct;
    }
}
