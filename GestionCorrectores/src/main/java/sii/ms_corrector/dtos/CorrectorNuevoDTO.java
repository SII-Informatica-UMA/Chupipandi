package sii.ms_corrector.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CorrectorNuevoDTO {
    Long identificadorUsuario;
    Long identificadorConvocatoria;
    String telefono;
    MateriaDTO materia;
    int maximasCorrecciones;
}
