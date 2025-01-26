package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, MpaRatingDbStorage.class})
class FilmorateApplicationTests {

	@Autowired
	private UserDbStorage userStorage;

	@Autowired
	private FilmDbStorage filmStorage;

	@Autowired
	private GenreDbStorage genreStorage;

	@Autowired
	private MpaRatingDbStorage mpaRatingStorage;

	@Test
	public void testFindUserById() {
		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testAddAndRetrieveFilm() {
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Description of Test Film");
		film.setReleaseDate(LocalDate.of(2020, 1, 1));
		film.setDuration(120);
		film.setMpaRating(new MpaRating(1, "G"));
		Film savedFilm = filmStorage.addFilm(film);

		Film retrievedFilm = filmStorage.getFilmById(savedFilm.getId());

		assertThat(retrievedFilm).isNotNull().satisfies(f -> {
			assertThat(f).hasFieldOrPropertyWithValue("name", "Test Film");
			assertThat(f).hasFieldOrPropertyWithValue("duration", 120);
		});
	}

	@Test
	public void testRetrieveAllGenres() {
		List<Genre> genres = genreStorage.getAllGenres();

		assertThat(genres).isNotEmpty();
		assertThat(genres).anyMatch(genre -> genre.getName().equals("Комедия"));
	}

	@Test
	public void testRetrieveMpaRatingById() {
		Optional<MpaRating> mpaRating = mpaRatingStorage.findById(1);

		assertThat(mpaRating).isPresent().hasValueSatisfying(rating ->
				assertThat(rating).hasFieldOrPropertyWithValue("name", "G")
		);
	}

	@Test
	public void testRetrieveFriendsOfUser() {
		List<User> friends = userStorage.getFriends(1);

		assertThat(friends).isNotEmpty();
		assertThat(friends).allSatisfy(friend -> {
			assertThat(friend.getId()).isNotNull();
			assertThat(friend.getName()).isNotBlank();
		});
	}
}