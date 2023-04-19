package sii.ms_evalexamenes.dtos;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.entities.Materia;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExamenDTO {
    
    private Long id;
    private Long materia;
    private Long codigoAlumno;
    private Float nota;

    public static ExamenDTO fromExamen(Examen examen) {
        var dto = new ExamenDTO();

        dto.setId(examen.getId());
        dto.setMateria(examen.getMateria().getId());
        dto.setNota(examen.getCalificacion());
        dto.setCodigoAlumno(examen.getAlumnoId());

        return dto;
    }

    public Examen examen() {
        var examen = new Examen();
        examen.setId(id);
        examen.setCalificacion(nota);
        // ¿Se podría obtener teniendo una variable de tipo MateriaRepository?
        Materia mat = new Materia();
        mat.setId(materia);
        examen.setMateria(mat);
        examen.setAlumnoId(codigoAlumno);
        // Se tendría que obtener del microservicio de Correctores de alguna forma
        examen.setCorrectorId(0L);
        examen.setFechaYHora(new Timestamp(System.currentTimeMillis()));
        
        return examen;
    }
}