package sii.ms_evalexamenes.entities;

import java.util.ArrayList;

import jakarta.persistence.Column;
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
@ToString(doNotUseGetters = true, exclude = {"correctores", "examenes"})
@NoArgsConstructor
public class Materia {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false)
    private String nombre;
    private ArrayList<Long> correctores; // Una materia puede tener varios correctores (lista de ids de los correctores)
    @OneToMany(mappedBy = "materia")
    private ArrayList<Examen> examenes; // Una materia puede tener varios exámenes







    private Long idConvocatoria;
}
