package ar.edu.utn.frc.backend.domain.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.time.LocalDateTime;

@Entity
@Table(name = "repository")
public class Repository {

    @Id
    private Integer id;
	@Column(name = "REPOSITORY_NAME")
    private String repositoryName;
    private String description;
	@Column(name = "LAST_UPDATE")
    private LocalDateTime lastUpdate;
    private double stars;
    private String url;
    @ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "OWNER_ID")
    private User user;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "tag_repository",
            joinColumns = @JoinColumn(name = "id_repository"),
            inverseJoinColumns = @JoinColumn(name = "id_tag"))
    private Set<Tag> tags;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "language_repository",
            joinColumns = @JoinColumn(name = "id_repository"),
            inverseJoinColumns = @JoinColumn(name = "id_language"))

    private Set<Language> languages;


	public Repository() {
		super();
	}

    public Repository(Integer id,
                      String repositoryName,
                      String description,
                      LocalDateTime lastUpdate,
                      double stars,
                      String url,
                      User user,
                      Set<Tag> tags,
                      Set<Language> languages) {
		this();

        this.id = id;
        this.repositoryName = repositoryName;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.stars = stars;
        this.url = url;
        this.user = user;
        this.tags = tags;
        this.languages = languages;

        this.user.addRepository(this);
        this.languages
                .forEach((language) -> language.addRepository(this));
    }

    public long lastUpdatedFromNowInDays() {
        return Math.abs(
                Duration.between(
                        this.lastUpdate, LocalDateTime.now()).toDays()
        );
    }

    public long starsBetweenZeroAndFive(double upperLimit){
        return Math.round((this.stars / upperLimit) * 5);
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.lastUpdate = LocalDateTime.now();
    }

    public void addStar() {
        this.stars++;
        this.lastUpdate = LocalDateTime.now();
    }

    public void removeStar() {
        this.stars--;
        this.lastUpdate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        return Objects.equals(repositoryName, that.repositoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repositoryName);
    }

    @Override
    public String toString() {
        return "Repository{" +
                "repositoryName='" + repositoryName + '\'' +
                ", description='" + description + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", stars=" + stars +
                ", url='" + url + '\'' +
                ", user=" + user +
                ", tags=" + tags +
                ", languages=" + languages +
                '}';
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public double getStars() {
        return stars;
    }

    public String getUrl() {
        return url;
    }

    public User getUser() {
        return user;
    }

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Set<Language> getLanguages() {
        return Collections.unmodifiableSet(languages);
    }

	public Integer getId() {
		return id;
	}
}
