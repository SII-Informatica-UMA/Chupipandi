package es.uma.informatica.jpa.demo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

/**
 * Entity implementation class for Entity: Book
 *
 */
@Entity

public class Book extends Item implements Serializable {

	   
	private String isbn;
	private Integer nbOfPage;
	private Boolean illustrations;
	@ElementCollection
	@CollectionTable(name = "TAG")
	@Column(name = "VALUE")
	private List<String> tags;
	
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	private static final long serialVersionUID = 1L;

	public Book() {
		super();
	}   
	public String getIsbn() {
		return this.isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}   
	public Integer getNbOfPage() {
		return this.nbOfPage;
	}

	public void setNbOfPage(Integer nbOfPage) {
		this.nbOfPage = nbOfPage;
	}   
	public Boolean getIllustrations() {
		return this.illustrations;
	}

	public void setIllustrations(Boolean illustrations) {
		this.illustrations = illustrations;
	}
   
}
