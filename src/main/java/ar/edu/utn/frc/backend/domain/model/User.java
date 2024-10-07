package ar.edu.utn.frc.backend.domain.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Comparable<User> {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @OneToMany(mappedBy = "user")
    Set<Repository> repositories;

    public User() {
		super();
    }

    public User(String name) {
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
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

	public Integer getId() {
		return id;
	}

	@Override
    public int compareTo(User user) {
        return this.name.compareTo(user.getName());
    }
}
