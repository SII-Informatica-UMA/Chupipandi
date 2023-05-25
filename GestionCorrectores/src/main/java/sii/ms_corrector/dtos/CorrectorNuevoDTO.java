package sii.ms_corrector.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sii.ms_corrector.entities.Corrector;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString   // para debug
public class CorrectorNuevoDTO {
    private Long identificadorUsuario;
    private Long identificadorConvocatoria;
    private String telefono;
    private MateriaDTO materia;
    private int maximasCorrecciones;

    public Corrector corrector() {
        var correct = new Corrector();
        correct.setIdUsuario(identificadorUsuario);
        correct.setTelefono(telefono);
        correct.setMaximasCorrecciones(maximasCorrecciones);
        return correct;
    }

    // Método en desuso

    // public static CorrectorNuevoDTO fromCorrector(Corrector corrector) {
    //     var dto = new CorrectorNuevoDTO();
    //     dto.setIdentificadorUsuario(corrector.getIdUsuario());
    //     dto.setTelefono(corrector.getTelefono());
    //     dto.setMaximasCorrecciones(corrector.getMaximasCorrecciones());
    //     return dto;
    // }
}
