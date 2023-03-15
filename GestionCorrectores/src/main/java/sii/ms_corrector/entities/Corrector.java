package sii.ms_corrector.entities;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Corrector {
    /*
     * Hemos considerado que los atributos comunes a todos
     * los usuarios (nombre, apellidos, email...) los contendrá
     * la entidad Usuario en el microservicio de Gestión de usuarios,
     * y por tanto sería redundante almacenarlos aquí también. Esta entidad
     * contiene por tanto los atributos propios de un corrector.
     */
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long materiaEspecialista; // Un corrector sólo es especialista de una materia (id de la materia)
    private ArrayList<Long> examenes; // Un corrector puede corregir varios exámenes (lista de ids de los exámenes)
    @Column(nullable = false)
    private Integer numeroMaximoExamenes;


    public Corrector(){}
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ArrayList<Long> getExamenes() {
        return examenes;
    }
    public void setExamenes(ArrayList<Long> examenes) {
        this.examenes = examenes;
    }
    public Long getMateriaEspecialista() {
        return materiaEspecialista;
    }
    public void setMateriaEspecialista(Long materiaEspecialista) {
        this.materiaEspecialista = materiaEspecialista; 
    }
    public Integer getNumeroMaximoExamenes() {
        return numeroMaximoExamenes;
    }
    public void setNumeroMaximoExamenes(Integer numeroMaximoExamenes) {
        this.numeroMaximoExamenes = numeroMaximoExamenes;
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
        Corrector other = (Corrector) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Corrector [id=" + id + ", materiaEspecialista=" + materiaEspecialista + ", numeroMaximoExamenes="
                + numeroMaximoExamenes + "]";
    }
}
