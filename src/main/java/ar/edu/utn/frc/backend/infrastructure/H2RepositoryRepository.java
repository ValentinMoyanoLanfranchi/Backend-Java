package ar.edu.utn.frc.backend.infrastructure;

import java.util.List;
import java.util.Set;

import ar.edu.utn.frc.backend.domain.model.Repository;
import ar.edu.utn.frc.backend.domain.model.RepositoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class H2RepositoryRepository implements RepositoryRepository {

	private static final String PERSISTENCE_UNIT_NAME = "repository-db";

	@Override
	public void saveAll(Set<Repository> repositories) {
		try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			repositories.forEach(entityManager::persist);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override public List<Repository> findByUserName(String userName) {
		try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
			EntityManager entityManager = entityManagerFactory.createEntityManager();

			return entityManager.createQuery("SELECT r FROM Repository r WHERE r.user.name = :userName", Repository.class)
				.setParameter("userName", userName)
				.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override public List<Repository> findByLanguageName(String languageName) {
		try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
			EntityManager entityManager = entityManagerFactory.createEntityManager();

			return entityManager.createQuery("SELECT r FROM Repository r, IN(r.languages) l WHERE l.name = :languageName", Repository.class)
				.setParameter("languageName", languageName)
				.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Repository> findByTagName(String tagName) {
		try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
			EntityManager entityManager = entityManagerFactory.createEntityManager();

			return entityManager.createQuery("SELECT r FROM Repository r, IN(r.tags) l WHERE l.name = :tagName", Repository.class)
				.setParameter("tagName", tagName)
				.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
