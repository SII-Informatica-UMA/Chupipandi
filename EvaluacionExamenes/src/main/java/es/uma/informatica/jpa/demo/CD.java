package es.uma.informatica.jpa.demo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

/**
 * Entity implementation class for Entity: CD
 *
 */
@Entity

public class CD extends Item implements Serializable {


	private String musicCompany;
	private Integer numberOfCDs;
	private Float totalDuration;
	private String gender;
	
	@ManyToMany(mappedBy = "appearsOnCDs")
	private List<Artist> createdByArtists;
	
	private static final long serialVersionUID = 1L;
	
	public CD() {
		super();
	}
	
	

	public String getMusicCompany() {
		return musicCompany;
	}



	public void setMusicCompany(String musicCompany) {
		this.musicCompany = musicCompany;
	}



	public Integer getNumberOfCDs() {
		return numberOfCDs;
	}



	public void setNumberOfCDs(Integer numberOfCDs) {
		this.numberOfCDs = numberOfCDs;
	}



	public Float getTotalDuration() {
		return totalDuration;
	}



	public void setTotalDuration(Float totalDuration) {
		this.totalDuration = totalDuration;
	}



	public String getGender() {
		return gender;
	}



	public void setGender(String gender) {
		this.gender = gender;
	}



	public List<Artist> getCreatedByArtists() {
		return createdByArtists;
	}
	public void setCreatedByArtists(List<Artist> createdByArtists) {
		this.createdByArtists = createdByArtists;
	}
   
}
