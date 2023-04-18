package sii.ms_corrector.entities;

import java.util.List;

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
@ToString(doNotUseGetters = true, exclude = {"materiaEspecialista"})
@NoArgsConstructor
public class Corrector {
    /*
     * Hemos considerado que los atributos comunes a todos
     * los usuarios (nombre, apellidos, email...) los contendrá
     * la entidad Usuario en el microservicio de Gestión de usuarios,
     * y por tanto sería redundante almacenarlos aquí también. Esta entidad
     * contiene por tanto los atributos propios de un corrector.
     */
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false)
    private Long materiaEspecialista; // Un corrector sólo es especialista de una materia (id de la materia)
    @Column(nullable = false)
    private Integer maximasCorrecciones;

    @OneToMany(mappedBy = "corrector")
    List<MateriaEnConvocatoria> matEnConv;
    private Long idConvocatoria;
    private Long idUsuario;
    private String telefono;
}
