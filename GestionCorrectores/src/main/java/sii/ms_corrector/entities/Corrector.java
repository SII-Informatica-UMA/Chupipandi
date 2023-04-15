package sii.ms_corrector.entities;

import java.util.ArrayList;

import jakarta.persistence.Column;
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
@ToString(doNotUseGetters = true, exclude = {"materiaEspecialista", "examenes"})
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
    private ArrayList<Long> examenes; // Un corrector puede corregir varios exámenes (lista de ids de los exámenes)
    @Column(nullable = false)
    private Integer maximasCorrecciones;

    private Long idConvocatoria;
    private Long idUsuario;
    private String telefono;
}
