package ar.edu.utn.frc.backend;

import ar.edu.utn.frc.backend.domain.model.Language;
import ar.edu.utn.frc.backend.domain.model.Repository;
import ar.edu.utn.frc.backend.domain.services.RepositoryService;
import ar.edu.utn.frc.backend.infrastructure.H2RepositoryRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Simulacro {

    public static void main(String[] args) {

        RepositoryService service = new RepositoryService(new H2RepositoryRepository());

		//Requerimiento 1
        Set<Repository> repositories = service.loadRepositories();

		//Requerimeinto 3
        System.out.printf("Número total de repositorios importados: %s%n", repositories.size());

        double upperLimit = repositories
                .stream()
                .mapToDouble(Repository::getStars)
                .max()
                .orElseThrow();

        long totalStars = repositories
                .stream()
                .mapToLong((repository) -> repository.starsBetweenZeroAndFive(upperLimit))
                .sum();


        System.out.printf("Número total de estrellas de todos los repositorios: %s%n", totalStars);

		//Requerimiento 4
		service.generateRepositoriesReport(
			"REPORTE.txt",
			repositories.stream()
				.flatMap((repository) -> repository.getLanguages().stream())
				.collect(Collectors.toSet()),
			upperLimit
		);


		//Requerimiento 5
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Nombre de ususario | Cantidad de repositorios | Total de estrellas")
                        .append("\n");
        repositories
                .stream()
                .map(Repository::getUser)
                .distinct()
                .sorted()
                .forEach((user)-> {
                    long totalRepositories = user.numberOfRepositories();
                    long numberOfStars = user.numberOfStars(upperLimit);

                    stringBuilder.append(String.format("%s | %s | %s", user.getName(), totalRepositories, numberOfStars))
                            .append("\n");
                });

        System.out.println(stringBuilder);


		//Requerimiento 6
		service.saveAll(repositories);

		//Consultas a la base de datos
		//Repositorios por usuario.
		List<Repository> repositoriesByUser = service.findByUserName("freeCodeCamp");

		//Repositorios por lenguaje.
		List<Repository> repositoriesByLanguage = service.findByLanguageName("JavaScript");

		//Repositorios por etiqueta.
		List<Repository> repositoriesByTag = service.findByTagName("nodejs");

		//Consultar la lista de repositorios de un usuario específico y la lista de lenguajes que utiliza en todos sus repositorios.
		Set<Language> languagesUsedInAllUserRepositories = repositoriesByUser
			.stream()
				.flatMap((repository) -> repository.getLanguages().stream())
				.collect(Collectors.toSet());

		System.out.println("Fin.");
    }
}
