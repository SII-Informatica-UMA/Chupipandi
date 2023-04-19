package sii.ms_corrector.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class Materia {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @EqualsAndHashCode.Include
    private Long id;                                    // id propio de la entidad
    
    @OneToMany(mappedBy = "materia")
    private List<MateriaEnConvocatoria> convocatorias;  // relacion con la entidad intermedia

    private Long idMateria;                             // id que identifica la materia
    private String nombre;                              // nombre de la materia
}
