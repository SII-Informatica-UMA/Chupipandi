package sii.ms_corrector.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sii.ms_corrector.entities.MateriaEnConvocatoria;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString   // para debug
public class MateriaEnConvocatoriaDTO {
    private Long idMateria;
    private Long idConvocatoria;

    public static MateriaEnConvocatoriaDTO fromMateriaEnConvocatoria(MateriaEnConvocatoria materia) { 
        var dto = new MateriaEnConvocatoriaDTO();
        dto.setIdMateria(materia.getMateria().getIdMateria());
        dto.setIdConvocatoria(materia.getIdConvocatoria());
        return dto;
    }

    // Método en desuso

    // public MateriaEnConvocatoria materiaEnConvocatoria() {
    //     var mat = new MateriaEnConvocatoria();
    //     Materia m = new Materia();
    //     m.setIdMateria(idMateria);
    //     mat.setMateria(m);
    //     mat.setIdConvocatoria(idConvocatoria);
    //     return mat;
    // }
}
