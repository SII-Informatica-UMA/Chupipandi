package sii.ms_evalexamenes.dtos;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sii.ms_evalexamenes.entities.Examen;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamenNuevoDTO {
    private Long materia;
    private Long codigoAlumno;

    public Examen examen() {
        Examen examen = new Examen();
        examen.setMateriaId(materia);
        examen.setAlumnoId(codigoAlumno);
        examen.setCalificacion(null);
        examen.setFechaYHora(new Timestamp(System.currentTimeMillis()));
        examen.setCorrectorId(0L);
        return examen;
    }
}
