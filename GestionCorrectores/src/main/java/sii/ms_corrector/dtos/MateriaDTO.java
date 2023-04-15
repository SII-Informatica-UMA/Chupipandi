package sii.ms_corrector.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaDTO {
    Long id;
    String nombre;
    // FIXME: necesitamos entidad materia en este microservicio? o como la obtentemos?
    // public static MateriaDTO fromMateria(Materia materia) { 
        
    // }
}
