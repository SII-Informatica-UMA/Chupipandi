package sii.ms_corrector.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class MateriaEnConvocatoria {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @EqualsAndHashCode.Include
    private Long id;                // id propio de la entidad

    @ManyToOne
    @JoinColumn(name = "CORRECTOR_PK")
    private Corrector corrector;         // clave foranea que apunta al corrector
    @ManyToOne
    @JoinColumn(name = "MATERIA_PK")
    private Materia materia;           // clave foranea que apunta a la materia
    
    private Long idConvocatoria;    // id que identifica la convocatoria

}
