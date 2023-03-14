package sii.ms_evalexamenes.entities;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Examen {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer calificacion;
    @Column(nullable = false)
    private Timestamp fechaYHora;
    @ManyToOne(optional = false)
    private Materia materia; // Un examen sólo pertenece a una materia
    @Column(nullable = false)
    private Long correctorId; // Id del corrector del examen (un examen sólo tiene un corrector)
    @Column(nullable = false)
    private Long alumnoId; // Id del alumno al que pertenece el examen (un examen sólo tiene un alumno)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getCalificacion() {
        return calificacion;
    }
    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }
    /*  
        Para fecha:
            String fecha = (new SimpleDateFormat("yyyy-MM-dd")).format(fechaYHora);
        Para hora:
            String hora = (new SimpleDateFormat("HH:mm:ss")).format(fechaYHora);
    */
    public Timestamp getFechaYHora() {
        return fechaYHora;
    }
    public void setFechaYHora(Timestamp fechaYHora) {
        this.fechaYHora = fechaYHora;
    }
    public Materia getMateria() {
        return materia;
    }
    public void setMateria(Materia materia) {
        this.materia = materia;
    }
    public Long getCorrectorId() {
        return correctorId;
    }
    public void setCorrectorId(Long correctorId) {
        this.correctorId = correctorId;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    public Long getAlumnoId() {
        return alumnoId;
    }
    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Examen other = (Examen) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Examen [id=" + id + ", calificacion=" + calificacion + ", fechaYHora=" + (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(fechaYHora) + "]";
    }
}
