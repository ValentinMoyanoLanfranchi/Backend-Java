package ar.edu.utn.frc.backend.domain.model;

import java.util.List;
import java.util.Set;

public interface RepositoryRepository {

	void saveAll(Set<Repository> repositories);

	List<Repository> findByUserName(String userName);

	List<Repository> findByLanguageName(String languageName);

	List<Repository> findByTagName(String tagName);

}
