package sii.ms_evalexamenes.dto;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sii.ms_evalexamenes.entities.Materia;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Examen_DTO {
    
    private Long id;
    private Integer calificacion;
    private Timestamp fechaYHora;
    private Materia materia; 
    private Long correctorId; 
    private Long alumnoId; 
}
