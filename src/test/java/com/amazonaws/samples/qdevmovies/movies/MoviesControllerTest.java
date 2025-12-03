package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;
    private HttpServletRequest mockRequest;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        mockRequest = mock(HttpServletRequest.class);
        
        // Create mock services with pirate-themed test data
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "The Pirate's Treasure", "Captain Hook", 2023, "Adventure", "A swashbuckling adventure", 120, 4.5),
                    new Movie(2L, "Blackbeard's Revenge", "Anne Bonny", 2022, "Action", "Epic pirate battles", 135, 4.0),
                    new Movie(3L, "The Kraken's Call", "Davy Jones", 2021, "Horror/Adventure", "Sea monster terror", 110, 3.5)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "The Pirate's Treasure", "Captain Hook", 2023, "Adventure", "A swashbuckling adventure", 120, 4.5));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matches = matches && movie.getMovieName().toLowerCase().contains(name.toLowerCase());
                    }
                    
                    if (id != null && id > 0) {
                        matches = matches && movie.getId() == id;
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        matches = matches && movie.getGenre().toLowerCase().contains(genre.toLowerCase());
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Adventure", "Horror/Adventure");
            }
            
            @Override
            public boolean isValidSearchRequest(String name, Long id, String genre) {
                boolean hasValidName = name != null && !name.trim().isEmpty();
                boolean hasValidId = id != null && id > 0;
                boolean hasValidGenre = genre != null && !genre.trim().isEmpty();
                return hasValidName || hasValidId || hasValidGenre;
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("genres"));
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    // Arrr! New search functionality tests, matey!
    
    @Test
    public void testSearchMoviesByName_HtmlRequest() {
        // Mock browser request (no JSON accept header)
        when(mockRequest.getHeader("Accept")).thenReturn("text/html");
        
        Object result = moviesController.searchMovies("Pirate", null, null, model, mockRequest);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertEquals("Pirate", model.getAttribute("searchName"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesById_ApiRequest() {
        // Mock API request (JSON accept header)
        when(mockRequest.getHeader("Accept")).thenReturn("application/json");
        
        Object result = moviesController.searchMovies(null, 1L, null, model, mockRequest);
        
        assertTrue(result instanceof ResponseEntity);
        @SuppressWarnings("unchecked")
        ResponseEntity<List<Movie>> response = (ResponseEntity<List<Movie>>) result;
        assertEquals(200, response.getStatusCodeValue());
        
        List<Movie> movies = response.getBody();
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals(1L, movies.get(0).getId());
    }
    
    @Test
    public void testSearchMoviesByGenre_HtmlRequest() {
        when(mockRequest.getHeader("Accept")).thenReturn("text/html");
        
        Object result = moviesController.searchMovies(null, null, "Adventure", model, mockRequest);
        
        assertEquals("movies", result);
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size()); // "Adventure" and "Horror/Adventure"
    }
    
    @Test
    public void testSearchMoviesMultipleCriteria_HtmlRequest() {
        when(mockRequest.getHeader("Accept")).thenReturn("text/html");
        
        Object result = moviesController.searchMovies("Kraken", null, "Horror", model, mockRequest);
        
        assertEquals("movies", result);
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Kraken's Call", movies.get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesNoResults_HtmlRequest() {
        when(mockRequest.getHeader("Accept")).thenReturn("text/html");
        
        Object result = moviesController.searchMovies("NonexistentMovie", null, null, model, mockRequest);
        
        assertEquals("movies", result);
        assertTrue((Boolean) model.getAttribute("noResults"));
        assertNotNull(model.getAttribute("noResultsMessage"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
    }
    
    @Test
    public void testSearchMoviesInvalidParameters_HtmlRequest() {
        when(mockRequest.getHeader("Accept")).thenReturn("text/html");
        
        // Empty parameters should trigger validation error
        Object result = moviesController.searchMovies("", null, "", model, mockRequest);
        
        assertEquals("movies", result);
        assertNotNull(model.getAttribute("searchError"));
        assertTrue(model.getAttribute("searchError").toString().contains("Arrr!"));
    }
    
    @Test
    public void testSearchMoviesInvalidParameters_ApiRequest() {
        when(mockRequest.getHeader("Accept")).thenReturn("application/json");
        
        Object result = moviesController.searchMovies(null, null, null, model, mockRequest);
        
        assertTrue(result instanceof ResponseEntity);
        @SuppressWarnings("unchecked")
        ResponseEntity<MoviesController.SearchErrorResponse> response = 
            (ResponseEntity<MoviesController.SearchErrorResponse>) result;
        assertEquals(400, response.getStatusCodeValue());
        
        MoviesController.SearchErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.getError().contains("Arrr!"));
    }
    
    @Test
    public void testSearchMoviesCaseInsensitive_HtmlRequest() {
        when(mockRequest.getHeader("Accept")).thenReturn("text/html");
        
        // Test case-insensitive search
        Object result = moviesController.searchMovies("PIRATE", null, null, model, mockRequest);
        
        assertEquals("movies", result);
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesPartialMatch_HtmlRequest() {
        when(mockRequest.getHeader("Accept")).thenReturn("text/html");
        
        // Test partial name matching
        Object result = moviesController.searchMovies("Treasure", null, null, model, mockRequest);
        
        assertEquals("movies", result);
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
    }
    
    @Test
    public void testSearchErrorResponseClass() {
        MoviesController.SearchErrorResponse errorResponse = 
            new MoviesController.SearchErrorResponse("Test error message");
        assertEquals("Test error message", errorResponse.getError());
    }
}
