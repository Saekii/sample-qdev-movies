package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        return "movies";
    }

    /**
     * Ahoy matey! This be our movie search endpoint that handles both API requests and HTML form submissions.
     * Supports searching by name, id, and genre - perfect for finding buried treasure!
     * 
     * @param name Movie name to search for (partial matches, case-insensitive)
     * @param id Exact movie ID to find
     * @param genre Genre to filter by (partial matches, case-insensitive)
     * @param model Spring model for HTML responses
     * @return JSON response for API calls, HTML template for browser requests
     */
    @GetMapping(value = "/movies/search")
    public Object searchMovies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String genre,
            org.springframework.ui.Model model,
            javax.servlet.http.HttpServletRequest request) {
        
        logger.info("Arrr! Movie search request received - name='{}', id='{}', genre='{}'", name, id, genre);
        
        // Check if this be an API request (JSON response expected)
        String acceptHeader = request.getHeader("Accept");
        boolean isApiRequest = acceptHeader != null && acceptHeader.contains("application/json");
        
        // Validate search parameters - at least one must be provided, ye scurvy dog!
        if (!movieService.isValidSearchRequest(name, id, genre)) {
            logger.warn("Invalid search request - no valid parameters provided");
            
            if (isApiRequest) {
                return ResponseEntity.badRequest()
                    .body(new SearchErrorResponse("Arrr! Ye must provide at least one search parameter (name, id, or genre), matey!"));
            } else {
                model.addAttribute("movies", movieService.getAllMovies());
                model.addAttribute("genres", movieService.getAllGenres());
                model.addAttribute("searchError", "Arrr! Ye must provide at least one search parameter, matey!");
                model.addAttribute("searchName", name);
                model.addAttribute("searchId", id);
                model.addAttribute("searchGenre", genre);
                return "movies";
            }
        }
        
        // Perform the search expedition!
        List<Movie> searchResults = movieService.searchMovies(name, id, genre);
        
        if (isApiRequest) {
            // Return JSON response for API requests
            logger.info("Returning JSON response with {} movies", searchResults.size());
            return ResponseEntity.ok(searchResults);
        } else {
            // Return HTML template for browser requests
            logger.info("Returning HTML response with {} movies", searchResults.size());
            model.addAttribute("movies", searchResults);
            model.addAttribute("genres", movieService.getAllGenres());
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            
            if (searchResults.isEmpty()) {
                model.addAttribute("noResults", true);
                model.addAttribute("noResultsMessage", 
                    "Arrr! No treasure found with those search criteria, matey! Try adjusting yer search parameters.");
            }
            
            return "movies";
        }
    }

    /**
     * Simple error response class for API requests
     */
    public static class SearchErrorResponse {
        private final String error;
        
        public SearchErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}