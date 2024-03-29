package sii.ms_corrector.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    private Integer maximasCorrecciones;

    @OneToMany(mappedBy = "corrector", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<MateriaEnConvocatoria> matEnConv;
    private Long idUsuario;
    private String telefono;
}
