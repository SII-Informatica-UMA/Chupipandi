package sii.ms_evalexamenes.entities;

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
    @ManyToOne
    private Materia materia; // Un examen sólo pertenece a una materia
    @ManyToOne
    private Corrector corrector; // Un examen sólo tiene un corrector
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
    public Materia getMateria() {
        return materia;
    }
    public void setMateria(Materia materia) {
        this.materia = materia;
    }
    public Corrector getCorrector() {
        return corrector;
    }
    public void setCorrector(Corrector corrector) {
        this.corrector = corrector;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
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
        return "Examen [id=" + id + ", calificacion=" + calificacion + "]";
    }
    
}
