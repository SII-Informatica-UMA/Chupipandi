package sii.ms_corrector.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MateriaEnConvocatoriaDTO {
    private Long idMateria;
    private Long idConvocatoria;
    // FIXME: necesitamos entidad materia en este microservicio? o como la obtentemos?
    // public static MateriaEnConvocatoriaDTO fromMateria(Materia materia) { 
    //     var dto = MateriaEnConvocatoriaDTO();
    // }
}
