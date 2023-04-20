package sii.ms_corrector.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sii.ms_corrector.entities.Materia;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString   // para debug
public class MateriaDTO {
    private Long id;
    private String nombre;

    public static MateriaDTO fromMateria(Materia materia) { 
        var dto = new MateriaDTO();
        dto.setId(materia.getIdMateria());
        dto.setNombre(materia.getNombre());
        return dto;
    }

    public Materia materia() {
        var mat = new Materia();
        mat.setIdMateria(id);
        mat.setNombre(nombre);
        return mat;
    }
}
