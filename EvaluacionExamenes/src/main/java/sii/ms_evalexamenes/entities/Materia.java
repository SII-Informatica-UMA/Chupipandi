package sii.ms_evalexamenes.entities;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Materia {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    private ArrayList<Long> correctores; // Una materia puede tener varios correctores (lista de ids de los correctores)
    @OneToMany(mappedBy = "materia")
    private ArrayList<Examen> examenes; // Una materia puede tener varios exámenes

    public Materia(){}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public ArrayList<Long> getCorrectores() {
        return correctores;
    }
    public void setCorrectores(ArrayList<Long> correctores) {
        this.correctores = correctores;
    }
    public ArrayList<Examen> getExamenes() {
        return examenes;
    }
    public void setExamenes(ArrayList<Examen> examenes) {
        this.examenes = examenes;
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
        Materia other = (Materia) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Materia [id=" + id + ", nombre=" + nombre + "]";
    }
}
