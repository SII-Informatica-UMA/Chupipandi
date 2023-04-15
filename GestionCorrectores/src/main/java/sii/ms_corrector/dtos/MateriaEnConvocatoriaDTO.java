package sii.ms_corrector.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MateriaEnConvocatoriaDTO {
    Long idMateria;
    Long idConvocatoria;
}
