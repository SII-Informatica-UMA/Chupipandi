package sii.ms_corrector.dtos;

import java.util.List;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sii.ms_corrector.entities.Corrector;
import sii.ms_corrector.entities.MateriaEnConvocatoria;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString   // para debug
public class CorrectorDTO {
    private Long id;
    private Long identificadorUsuario;
    private String telefono;
    private int maximasCorrecciones;
    private List<MateriaEnConvocatoriaDTO> materias;

    public static CorrectorDTO fromCorrector(Corrector corrector) {
        var dto = new CorrectorDTO();
        dto.setId(corrector.getId());
        dto.setIdentificadorUsuario(corrector.getIdUsuario());
        dto.setTelefono(corrector.getTelefono());
        dto.setMaximasCorrecciones(corrector.getMaximasCorrecciones());
        Function<MateriaEnConvocatoria, MateriaEnConvocatoriaDTO> mapper = (mat -> MateriaEnConvocatoriaDTO.fromMateriaEnConvocatoria(mat));
        dto.setMaterias(corrector.getMatEnConv().stream().map(mapper).toList());
        return dto;
    }

    // Método en desuso

    // public Corrector corrector() {
    //     var correct = new Corrector();
    //     correct.setId(id);
    //     correct.setIdUsuario(identificadorUsuario);
    //     correct.setTelefono(telefono);
    //     correct.setMaximasCorrecciones(maximasCorrecciones);
    //     correct.setMatEnConv(materias.stream().map(mat -> mat.materiaEnConvocatoria()).toList());
    //     return correct;
    // }
}
