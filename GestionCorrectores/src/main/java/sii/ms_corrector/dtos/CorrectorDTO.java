package sii.ms_corrector.dtos;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CorrectorDTO {
    Long id;
    Long identificadorUsuario;
    String telefono;
    int maximasCorrecciones;
    List<MateriaEnConvocatoriaDTO> materias;
}
