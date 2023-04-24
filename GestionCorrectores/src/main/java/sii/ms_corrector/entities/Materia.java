package sii.ms_corrector.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
@ToString(doNotUseGetters = true, exclude = {"id"}) // para debug
@NoArgsConstructor
public class Materia {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @EqualsAndHashCode.Include
    private Long id;            // id propio de la entidad
    
    private Long idMateria;     // id que identifica la materia
    private String nombre;      // nombre de la materia
}
