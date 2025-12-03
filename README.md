# Movie Service - Spring Boot Demo Application 🏴‍☠️

Ahoy matey! A swashbuckling movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate twist!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **🔍 Movie Search & Filtering**: Hunt for treasure with our powerful search functionality!
  - Search by movie name (partial matches supported)
  - Filter by exact movie ID
  - Filter by genre (supports multi-genre formats like "Crime/Drama")
  - Combine multiple search criteria for precise treasure hunting
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **🏴‍☠️ Pirate-Themed Interface**: Search for movies with authentic pirate language and styling

## Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Maven** for dependency management
- **Thymeleaf** for templating
- **Log4j 2.20.0**
- **JUnit 5.8.2** with **Mockito 4.6.1**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🔍 Movie Search**: Use the search form on the movies page or API endpoint

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Service layer with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review service
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data (12 movies)
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Enhanced with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MoviesControllerTest.java     # Controller tests with search scenarios
            ├── MovieServiceTest.java         # Service layer tests
            └── MovieTest.java                # Model tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a pirate-themed search form.

### 🔍 Search Movies (NEW!)
```
GET /movies/search?name={name}&id={id}&genre={genre}
```

**Arrr! This be our treasure hunting endpoint, matey!**

**Parameters (all optional, but at least one required):**
- `name` (query parameter): Movie name to search for (partial matches, case-insensitive)
- `id` (query parameter): Exact movie ID to find (1-12)
- `genre` (query parameter): Genre to filter by (partial matches, case-insensitive)

**Response Format:**
- **HTML Response** (default): Returns the movies page with search results
- **JSON Response**: Include `Accept: application/json` header for API usage

**Examples:**

Search by name (partial match):
```
http://localhost:8080/movies/search?name=Prison
```

Search by exact ID:
```
http://localhost:8080/movies/search?id=1
```

Search by genre:
```
http://localhost:8080/movies/search?genre=Drama
```

Combine multiple criteria:
```
http://localhost:8080/movies/search?name=Family&genre=Crime
```

API request (JSON response):
```bash
curl -H "Accept: application/json" "http://localhost:8080/movies/search?genre=Action"
```

**Error Handling:**
- Returns 400 Bad Request if no valid search parameters provided
- Returns empty results with pirate-themed message if no movies match criteria
- Handles invalid parameters gracefully

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## Search Functionality Features

### 🏴‍☠️ Pirate-Themed Search Interface
- **Search Form**: Beautifully styled with pirate colors and terminology
- **Input Fields**: Movie name, ID, and genre dropdown with all available genres
- **Buttons**: "Hunt for Treasure!" search button and "Clear Search" option
- **Results Display**: Shows search results count with pirate language
- **No Results**: Displays encouraging pirate-themed message when no movies found

### Search Capabilities
- **Case-Insensitive**: All text searches ignore case
- **Partial Matching**: Name and genre searches support partial matches
- **Multi-Genre Support**: Handles complex genres like "Crime/Drama"
- **Parameter Validation**: Ensures at least one valid search parameter
- **Whitespace Handling**: Trims and validates input parameters
- **Combined Searches**: Use multiple criteria with AND logic

### API Features
- **Content Negotiation**: Automatically detects JSON vs HTML requests
- **RESTful Design**: Follows REST conventions for search endpoints
- **Error Responses**: Consistent error format for both JSON and HTML
- **Logging**: Comprehensive logging with pirate-themed messages

## Testing

Run the comprehensive test suite:

```bash
mvn test
```

**Test Coverage:**
- **MovieServiceTest**: 25+ test cases covering all search scenarios
- **MoviesControllerTest**: 15+ test cases for both HTML and API responses
- **Edge Cases**: Invalid parameters, empty results, case sensitivity
- **Integration Tests**: End-to-end search functionality

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that at least one search parameter is provided
2. Verify movie data is loaded (check logs for "Arrr! Starting movie search expedition")
3. Ensure proper URL encoding for special characters in search terms

## Contributing

This project demonstrates modern Spring Boot development practices. Feel free to:
- Add more movies to the catalog
- Enhance the search functionality (e.g., search by director, year)
- Improve the pirate-themed UI/UX
- Add pagination for large result sets
- Implement advanced filtering options

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May ye find all the movie treasure ye seek, matey! 🏴‍☠️*
