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
    //private Long idConvocatoria;


    public static Materia_DTO fromMateria(Materia materia) {
        var dto = new Materia_DTO();

        dto.setId(materia.getId());
        dto.setNombre(materia.getNombre());
        dto.setCorrectores(materia.getCorrectores());
        dto.setExamenes(materia.getExamenes());
        //dto.setIdconvocatoria(materia.getIdConvocatoria());
        
        return dto;
    }



    public Materia materia(){
        var materia = new Materia();
        materia.setId(id);
        materia.setNombre(nombre);
        materia.setCorrectores(correctores);
        materia.setExamenes(examenes);
                
        return materia;
    }
    
}