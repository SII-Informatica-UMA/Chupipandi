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
    Long identificadorUsuario;
    Long identificadorConvocatoria;
    String telefono;
    MateriaDTO materia;
    int maximasCorrecciones;

    public static CorrectorNuevoDTO fromCorrector(Corrector corrector) {
        var dto = new CorrectorNuevoDTO();
        dto.setIdentificadorUsuario(null);              // de donde lo obtenemos?
        dto.setIdentificadorConvocatoria(null);    // de donde lo obtenemos?
        dto.setTelefono(null);                                      // de donde lo obtenemos?
        dto.setMateria(null);                                        // de donde lo obtenemos?
        dto.setMaximasCorrecciones(corrector.getMaximasCorrecciones());
        return null;
    }
}
