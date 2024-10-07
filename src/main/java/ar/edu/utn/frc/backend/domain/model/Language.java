package ar.edu.utn.frc.backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "language")
public class Language {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;
	@ManyToMany(mappedBy = "languages")
	private Set<Repository> repositories;

	public Language() {
		super();
	}

	public Language(String name) {
		this();

		this.name = name;
		this.repositories = new HashSet<>();
	}

	public long numberOfRepositories() {
		return this.repositories.size();
	}

	public long numberOfStars(double upperLimit) {

		return this.repositories
			.stream()
			.mapToLong((repository) -> repository.starsBetweenZeroAndFive(upperLimit))
			.sum();
	}

	protected void addRepository(Repository repository) {
		this.repositories.add(repository);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Language language = (Language) o;
		return Objects.equals(name, language.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return "Language{" +
			"name='" + name + '\'' +
			'}';
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
