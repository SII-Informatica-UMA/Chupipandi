package sii.ms_evalexamenes.dtos;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sii.ms_evalexamenes.entities.Examen;

@Getter
@Setter
@NoArgsConstructor
public class ExamenDTO {
    
    private Long id;
    private Long materia;
    private Long codigoAlumno;
    private Float nota;

    public static ExamenDTO fromExamen(Examen examen) {
        var dto = new ExamenDTO();

        dto.setId(examen.getId());
        dto.setMateria(examen.getMateriaId());
        dto.setNota(examen.getCalificacion());
        dto.setCodigoAlumno(examen.getAlumnoId());

        return dto;
    }

    public Examen examen() {
        var examen = new Examen();
        examen.setCalificacion(nota);
        // ¿Se podría obtener teniendo una variable de tipo MateriaRepository?
        examen.setMateriaId(materia);
        examen.setAlumnoId(codigoAlumno);
        // Se tendría que obtener del microservicio de Correctores de alguna forma
        examen.setCorrectorId(0L);
        examen.setFechaYHora(new Timestamp(System.currentTimeMillis()));
        
        return examen;
    }
}
