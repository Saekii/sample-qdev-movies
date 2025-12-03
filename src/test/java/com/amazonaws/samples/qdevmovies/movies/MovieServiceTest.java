package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! Test class for our MovieService treasure hunting functionality.
 * These tests ensure our search methods work like a well-oiled pirate ship!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Should load 12 movies from the JSON file
        assertEquals(12, movies.size());
    }

    @Test
    public void testGetMovieById_ValidId() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals(1L, movie.get().getId());
        assertEquals("The Prison Escape", movie.get().getMovieName());
    }

    @Test
    public void testGetMovieById_InvalidId() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieById_NullId() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieById_NegativeId() {
        Optional<Movie> movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());
    }

    // Arrr! Search functionality tests, ye scurvy dogs!

    @Test
    public void testSearchMoviesByName_ExactMatch() {
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByName_PartialMatch() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByName_CaseInsensitive() {
        List<Movie> results = movieService.searchMovies("PRISON", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByName_NoMatch() {
        List<Movie> results = movieService.searchMovies("Nonexistent Movie", null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesById_ValidId() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    public void testSearchMoviesById_InvalidId() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByGenre_ExactMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertFalse(results.isEmpty());
        // Should find movies with "Drama" genre
        assertTrue(results.stream().anyMatch(movie -> movie.getGenre().contains("Drama")));
    }

    @Test
    public void testSearchMoviesByGenre_PartialMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Crime");
        assertFalse(results.isEmpty());
        // Should find movies with "Crime/Drama" genre
        assertTrue(results.stream().anyMatch(movie -> movie.getGenre().contains("Crime")));
    }

    @Test
    public void testSearchMoviesByGenre_CaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(movie -> movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenre_NoMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "NonexistentGenre");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesMultipleCriteria_NameAndGenre() {
        List<Movie> results = movieService.searchMovies("Family", null, "Crime");
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertTrue(results.get(0).getGenre().contains("Crime"));
    }

    @Test
    public void testSearchMoviesMultipleCriteria_IdAndName() {
        List<Movie> results = movieService.searchMovies("Prison", 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesMultipleCriteria_AllParameters() {
        List<Movie> results = movieService.searchMovies("Prison", 1L, "Drama");
        assertEquals(1, results.size());
        Movie movie = results.get(0);
        assertEquals(1L, movie.getId());
        assertEquals("The Prison Escape", movie.getMovieName());
        assertEquals("Drama", movie.getGenre());
    }

    @Test
    public void testSearchMoviesMultipleCriteria_NoMatch() {
        // Search for a movie with ID 1 but wrong name - should return empty
        List<Movie> results = movieService.searchMovies("Wrong Name", 1L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesEmptyParameters() {
        List<Movie> results = movieService.searchMovies("", null, "");
        // Should return all movies when no valid parameters provided
        assertEquals(12, results.size());
    }

    @Test
    public void testSearchMoviesNullParameters() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        // Should return all movies when all parameters are null
        assertEquals(12, results.size());
    }

    @Test
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Should contain unique genres from our movie collection
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Crime/Drama"));
        assertTrue(genres.contains("Action/Crime"));
        assertTrue(genres.contains("Action/Sci-Fi"));
        assertTrue(genres.contains("Adventure/Fantasy"));
        assertTrue(genres.contains("Adventure/Sci-Fi"));
        assertTrue(genres.contains("Drama/History"));
        assertTrue(genres.contains("Drama/Romance"));
        assertTrue(genres.contains("Drama/Thriller"));
        
        // Should be sorted
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i-1).compareTo(genres.get(i)) <= 0);
        }
    }

    @Test
    public void testIsValidSearchRequest_ValidName() {
        assertTrue(movieService.isValidSearchRequest("Test Movie", null, null));
    }

    @Test
    public void testIsValidSearchRequest_ValidId() {
        assertTrue(movieService.isValidSearchRequest(null, 1L, null));
    }

    @Test
    public void testIsValidSearchRequest_ValidGenre() {
        assertTrue(movieService.isValidSearchRequest(null, null, "Drama"));
    }

    @Test
    public void testIsValidSearchRequest_MultipleValid() {
        assertTrue(movieService.isValidSearchRequest("Test", 1L, "Drama"));
    }

    @Test
    public void testIsValidSearchRequest_EmptyName() {
        assertFalse(movieService.isValidSearchRequest("", null, null));
    }

    @Test
    public void testIsValidSearchRequest_WhitespaceName() {
        assertFalse(movieService.isValidSearchRequest("   ", null, null));
    }

    @Test
    public void testIsValidSearchRequest_ZeroId() {
        assertFalse(movieService.isValidSearchRequest(null, 0L, null));
    }

    @Test
    public void testIsValidSearchRequest_NegativeId() {
        assertFalse(movieService.isValidSearchRequest(null, -1L, null));
    }

    @Test
    public void testIsValidSearchRequest_EmptyGenre() {
        assertFalse(movieService.isValidSearchRequest(null, null, ""));
    }

    @Test
    public void testIsValidSearchRequest_AllInvalid() {
        assertFalse(movieService.isValidSearchRequest("", null, ""));
        assertFalse(movieService.isValidSearchRequest(null, null, null));
        assertFalse(movieService.isValidSearchRequest("   ", 0L, "   "));
    }

    @Test
    public void testSearchMoviesWithWhitespace() {
        // Test that leading/trailing whitespace is handled properly
        List<Movie> results1 = movieService.searchMovies("  Prison  ", null, null);
        List<Movie> results2 = movieService.searchMovies("Prison", null, null);
        
        assertEquals(results1.size(), results2.size());
        if (!results1.isEmpty()) {
            assertEquals(results1.get(0).getMovieName(), results2.get(0).getMovieName());
        }
    }

    @Test
    public void testSearchMoviesGenreWithSlash() {
        // Test searching for genres that contain slashes like "Crime/Drama"
        List<Movie> results = movieService.searchMovies(null, null, "Crime/Drama");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(movie -> movie.getGenre().contains("Crime/Drama")));
    }
}