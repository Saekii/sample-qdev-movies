package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! Search through our treasure chest of movies using various criteria.
     * This method be the heart of our movie hunting expedition!
     * 
     * @param name The movie name to search for (partial matches allowed, case-insensitive)
     * @param id The exact movie ID to find
     * @param genre The genre to filter by (partial matches allowed, case-insensitive)
     * @return List of movies matching the search criteria, empty if no treasure be found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Starting movie search expedition with name='{}', id='{}', genre='{}'", name, id, genre);
        
        List<Movie> treasureChest = new ArrayList<>(movies);
        
        // Filter by ID first - exact match required, ye scurvy dog!
        if (id != null && id > 0) {
            logger.debug("Searching for movie with ID: {}", id);
            treasureChest = treasureChest.stream()
                .filter(movie -> movie.getId() == id)
                .collect(Collectors.toList());
            logger.debug("Found {} movies matching ID criteria", treasureChest.size());
        }
        
        // Filter by name - partial match, case-insensitive like a true pirate!
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            logger.debug("Searching for movies with name containing: '{}'", searchName);
            treasureChest = treasureChest.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
            logger.debug("Found {} movies matching name criteria", treasureChest.size());
        }
        
        // Filter by genre - partial match, case-insensitive, handles multi-genre formats
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            logger.debug("Searching for movies with genre containing: '{}'", searchGenre);
            treasureChest = treasureChest.stream()
                .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                .collect(Collectors.toList());
            logger.debug("Found {} movies matching genre criteria", treasureChest.size());
        }
        
        logger.info("Arrr! Search expedition complete! Found {} movies in our treasure chest", treasureChest.size());
        return treasureChest;
    }

    /**
     * Ahoy! Get all available genres from our movie treasure chest.
     * Useful for building search forms and helping landlubbers find what they seek!
     * 
     * @return List of unique genres found in our movie collection
     */
    public List<String> getAllGenres() {
        logger.debug("Gathering all genres from our treasure chest");
        List<String> genres = movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        logger.debug("Found {} unique genres in our collection", genres.size());
        return genres;
    }

    /**
     * Validate search parameters to prevent scurvy bugs from infesting our search!
     * 
     * @param name Movie name parameter
     * @param id Movie ID parameter  
     * @param genre Genre parameter
     * @return true if at least one valid search parameter is provided
     */
    public boolean isValidSearchRequest(String name, Long id, String genre) {
        boolean hasValidName = name != null && !name.trim().isEmpty();
        boolean hasValidId = id != null && id > 0;
        boolean hasValidGenre = genre != null && !genre.trim().isEmpty();
        
        boolean isValid = hasValidName || hasValidId || hasValidGenre;
        logger.debug("Search parameter validation: name={}, id={}, genre={}, valid={}", 
                    hasValidName, hasValidId, hasValidGenre, isValid);
        
        return isValid;
    }
}
