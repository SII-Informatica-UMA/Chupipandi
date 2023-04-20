package sii.ms_evalexamenes.entities;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
@ToString(doNotUseGetters = true, exclude = {"materia", "correctorId", "alumnoId"})
@NoArgsConstructor
public class Examen {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @EqualsAndHashCode.Include
    private Long id;
    private Float calificacion;
    @Column(nullable = false)
    @Getter(AccessLevel.NONE)   // mantengo getter por si mas adelante distinguimos hora/fecha
    private Timestamp fechaYHora;
    @ManyToOne(optional = false)
    @JsonIgnoreProperties("examenes")
    private Materia materia; // Un examen sólo pertenece a una materia
    @Column(nullable = false)
    private Long correctorId; // Id del corrector del examen (un examen sólo tiene un corrector)
    @Column(nullable = false)
    private Long alumnoId; // Id del alumno al que pertenece el examen (un examen sólo tiene un alumno)

    /*  
        Para fecha:
            String fecha = (new SimpleDateFormat("yyyy-MM-dd")).format(fechaYHora);
        Para hora:
            String hora = (new SimpleDateFormat("HH:mm:ss")).format(fechaYHora);
    */
    public Timestamp getFechaYHora() {
        return fechaYHora;
    }
}
