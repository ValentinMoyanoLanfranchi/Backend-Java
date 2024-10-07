package ar.edu.utn.frc.backend.domain.services;

import ar.edu.utn.frc.backend.domain.model.Language;
import ar.edu.utn.frc.backend.domain.model.Repository;
import ar.edu.utn.frc.backend.domain.model.RepositoryRepository;
import ar.edu.utn.frc.backend.domain.model.Tag;
import ar.edu.utn.frc.backend.domain.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class RepositoryService {

	private final RepositoryRepository repositoryRepository;

	public RepositoryService(RepositoryRepository repositoryRepository) {
		this.repositoryRepository = repositoryRepository;
	}

	public void saveAll(Set<Repository> repositories) {
		repositoryRepository.saveAll(repositories);
	}

	public List<Repository> findByUserName(String userName) {
		return repositoryRepository.findByUserName(userName);
	}

	public List<Repository> findByTagName(String tagName) {
		return repositoryRepository.findByTagName(tagName);
	}

	public List<Repository> findByLanguageName(String languageName) {
		return repositoryRepository.findByLanguageName(languageName);
	}

	public Set<Repository> loadRepositories() {
		//Requerimiento 2 (mantenemos una instancia de cada usuario, tag y lenguaje para evitar duplicados)
		Map<String, User> users = new HashMap<>();
		Map<String, Tag> tags = new HashMap<>();
		Map<String, Language> languages = new HashMap<>();

		try (Stream<String> stream = Files.lines(
			Path.of(
				ClassLoader
					.getSystemResource("REPOSITORIES.txt")
					.toURI()))) {

			//Con skip ignoramos la primer línea que contiene los nombres de las columnas
			return stream.skip(1)
				.map((line) -> mapRepository(line, users, tags, languages))
				.collect(Collectors.toSet());

		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private Repository mapRepository(String line,
									 Map<String, User> processedUsers,
									 Map<String, Tag> processedTags,
									 Map<String, Language> processedLanguages) {

		String[] values = line.split("\\|");

		Integer repositoryId = Integer.parseInt(values[0]);
		String userName = values[1];
		String repositoryName = values[2];
		String description = values[3];
		LocalDateTime lastUpdate = LocalDateTime.parse(
			values[4],
			DateTimeFormatter.ISO_DATE_TIME);
		Set<Language> languages = parseLanguages(values[5], processedLanguages);
		double stars = Double.parseDouble(values[6]);
		Set<Tag> tags = parseTags(values[7], processedTags);
		String url = values[8];
		User user = processedUsers.getOrDefault(userName, new User(userName));

		Repository repository = new Repository(
			repositoryId,
			repositoryName,
			description,
			lastUpdate,
			stars,
			url,
			user,
			tags,
			languages
		);

		//Guardamos el usuario luego de que el repositorio fue creado, para tener el repositorio dentro del usuario (se agrega dentro del constructor del repositorio).
		processedUsers.put(userName, user);
		//Lo mismo, luago de que el repositorio se agregó a cada lenguaje, se guarda el en el mapa de lenguajes procesados.
		languages
			.forEach((language) -> processedLanguages.put(language.getName(), language));

		return repository;
	}

	private Set<Language> parseLanguages(String values, Map<String, Language> languages) {
		if (!values.trim().equalsIgnoreCase("")) {
			return Arrays
				.stream(values.split(","))
				.map((languageName) -> {
					Language language = languages.getOrDefault(
						languageName,
						new Language(languageName));
					languages.put(languageName, language);
					return language;
				})
				.collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}

	private Set<Tag> parseTags(String values, Map<String, Tag> tags) {
		if (!values.trim().equalsIgnoreCase("")) {
			return Arrays
				.stream(values.split(","))
				.map((tagName) -> {
					Tag tag = tags.getOrDefault(tagName, new Tag(tagName));
					tags.put(tagName, tag);
					return tag;
				})
				.collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}

	public void generateRepositoriesReport(String filePath, Set<Language> languages, double upperLimit) {
		File file = new File(filePath);

		try (PrintWriter printWriter = new PrintWriter(file)) {

			languages.forEach((language) -> {
				printWriter.println(format("%s,%s,%s",
					language.getName(),
					language.numberOfRepositories(),
					language.numberOfStars(upperLimit)));
			});

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
