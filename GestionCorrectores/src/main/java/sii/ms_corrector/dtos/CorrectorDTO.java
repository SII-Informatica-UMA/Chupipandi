package sii.ms_corrector.dtos;

import java.util.List;

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
    Long id;
    Long identificadorUsuario;
    String telefono;
    int maximasCorrecciones;
    List<MateriaEnConvocatoriaDTO> materias;

    public static CorrectorDTO fromCorrector(Corrector corrector) {
        var dto = new CorrectorDTO();
        dto.setId(corrector.getId());
        dto.setIdentificadorUsuario(null);  // de donde lo obtenemos?
        dto.setTelefono(null);  // de donde lo obtenemos?
        dto.setMaximasCorrecciones(corrector.getMaximasCorrecciones());
        dto.setMaterias(null);
        return null;
    }
}
