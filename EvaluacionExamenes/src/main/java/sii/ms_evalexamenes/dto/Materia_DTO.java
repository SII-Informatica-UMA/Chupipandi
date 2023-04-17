package sii.ms_evalexamenes.dto;

import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.entities.Materia;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Materia_DTO {
    private Long id;
    private String nombre;
    private ArrayList<Long> correctores; 
    private ArrayList<Examen> examenes;
    
    
    public static Materia_DTO fromMateria(Materia materia) {
        
        return null;
    }
}