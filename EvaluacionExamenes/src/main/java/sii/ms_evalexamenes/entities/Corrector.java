package sii.ms_evalexamenes.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Corrector {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String primerApellido;
    private String segundoApellido;
    @Column(nullable = false)
    private String email;
    private Integer prefijoTelefonico;
    private Integer numeroMovil;
    @Column(nullable = false)
    private Integer numeroMaximoExamenes;
    @ManyToOne
    private Materia materiaEspecialista; // Un corrector sólo puede especializarse en una materia
    @OneToMany(mappedBy = "corrector")
    private List<Examen> examenes; // Un corrector puede corregir varios exámenes

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setMateriaEspecialista(Materia materiaEspecialista) {
        this.materiaEspecialista = materiaEspecialista;
    }
    public List<Examen> getExamenes() {
        return examenes;
    }
    public void setExamenes(List<Examen> examenes) {
        this.examenes = examenes;
    }
    public String getNombre() {
        return nombre;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getPrimerApellido() {
        return primerApellido;
    }
    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }
    public String getSegundoApellido() {
        return segundoApellido;
    }
    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }
    public Integer getPrefijoTelefonico() {
        return prefijoTelefonico;
    }
    public void setPrefijoTelefonico(Integer prefijoTelefonico) {
        this.prefijoTelefonico = prefijoTelefonico;
    }
    public Integer getNumeroMovil() {
        return numeroMovil;
    }
    public void setNumeroMovil(Integer numeroMovil) {
        this.numeroMovil = numeroMovil;
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
        return "Corrector [id=" + id + ", nombre=" + nombre + ", primerApellido=" + primerApellido
                + ", segundoApellido=" + segundoApellido + ", email=" + email + ", prefijoTelefonico="
                + prefijoTelefonico + ", numeroMovil=" + numeroMovil + ", numeroMaximoExamenes=" + numeroMaximoExamenes
                + "]";
    }

    
    
}
