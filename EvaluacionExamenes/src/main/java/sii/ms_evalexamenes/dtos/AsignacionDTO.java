package sii.ms_evalexamenes.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sii.ms_evalexamenes.entities.Examen;

@Getter
@Setter
@NoArgsConstructor
public class AsignacionDTO {

    private Long idCorrector;
    private Long idExamen;

    public static AsignacionDTO fromExamen(Examen examen) {
        var dto = new AsignacionDTO();
        
        dto.setIdCorrector(examen.getCorrectorId());
        dto.setIdExamen(examen.getId());
        return dto;
    }
}
