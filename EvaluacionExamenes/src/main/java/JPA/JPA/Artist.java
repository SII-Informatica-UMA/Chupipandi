package es.uma.informatica.jpa.demo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * Entity implementation class for Entity: Artist
 *
 */
@Entity

public class Artist implements Serializable {
	@Id
	private Long id;
	private String firstName;
	private String lastName;
	@ManyToMany
	private List<CD> appearsOnCDs;
	
	private static final long serialVersionUID = 1L;

	public Artist() {
		super();
	}   
	
	public List<CD> getAppearsOnCDs() {
		return appearsOnCDs;
	}



	public void setAppearsOnCDs(List<CD> appearsOnCDs) {
		this.appearsOnCDs = appearsOnCDs;
	}



	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}   
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
   
}
