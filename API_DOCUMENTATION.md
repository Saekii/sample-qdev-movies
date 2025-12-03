# Movie Search API Documentation 🏴‍☠️

Ahoy matey! This be the comprehensive API documentation for our movie search and filtering functionality. Navigate these waters like a true pirate!

## Overview

The Movie Search API provides powerful search and filtering capabilities for our treasure chest of movies. It supports both HTML responses for web browsers and JSON responses for API clients.

## Base URL

```
http://localhost:8080
```

## Authentication

No authentication required - this be open waters for all landlubbers!

## Content Types

- **HTML Responses**: Default for browser requests
- **JSON Responses**: Include `Accept: application/json` header

## Endpoints

### 1. Get All Movies

**Endpoint:** `GET /movies`

**Description:** Returns all movies with a pirate-themed search form.

**Response:** HTML page with movie grid and search interface

**Example:**
```bash
curl http://localhost:8080/movies
```

---

### 2. Search Movies 🔍

**Endpoint:** `GET /movies/search`

**Description:** Arrr! The main treasure hunting endpoint for searching and filtering movies.

#### Parameters

All parameters are optional, but **at least one must be provided**.

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `name` | String | Movie name (partial match, case-insensitive) | `Prison`, `family`, `HERO` |
| `id` | Long | Exact movie ID (1-12) | `1`, `5`, `12` |
| `genre` | String | Genre filter (partial match, case-insensitive) | `Drama`, `crime`, `ACTION` |

#### Response Formats

##### HTML Response (Default)
Returns the movies page with search results, search form, and pirate-themed messaging.

**Success Response:**
- **Status:** 200 OK
- **Content-Type:** text/html
- **Body:** HTML page with filtered movies

**Error Response:**
- **Status:** 200 OK (still returns HTML page)
- **Content-Type:** text/html
- **Body:** HTML page with error message and all movies

##### JSON Response
Include `Accept: application/json` header for API usage.

**Success Response:**
- **Status:** 200 OK
- **Content-Type:** application/json
- **Body:** Array of movie objects

```json
[
  {
    "id": 1,
    "movieName": "The Prison Escape",
    "director": "John Director",
    "year": 1994,
    "genre": "Drama",
    "description": "Two imprisoned men bond over a number of years...",
    "duration": 142,
    "imdbRating": 5.0,
    "icon": "🎬"
  }
]
```

**Error Response:**
- **Status:** 400 Bad Request
- **Content-Type:** application/json
- **Body:** Error object

```json
{
  "error": "Arrr! Ye must provide at least one search parameter (name, id, or genre), matey!"
}
```

#### Examples

##### Search by Movie Name
```bash
# HTML Response
curl "http://localhost:8080/movies/search?name=Prison"

# JSON Response
curl -H "Accept: application/json" "http://localhost:8080/movies/search?name=Prison"
```

**Expected Results:** Movies containing "Prison" in the name (case-insensitive)

##### Search by Movie ID
```bash
# HTML Response
curl "http://localhost:8080/movies/search?id=1"

# JSON Response
curl -H "Accept: application/json" "http://localhost:8080/movies/search?id=1"
```

**Expected Results:** Exactly one movie with ID 1, or empty if not found

##### Search by Genre
```bash
# HTML Response
curl "http://localhost:8080/movies/search?genre=Drama"

# JSON Response
curl -H "Accept: application/json" "http://localhost:8080/movies/search?genre=Drama"
```

**Expected Results:** All movies with "Drama" in their genre field

##### Combined Search
```bash
# Search for movies with "Family" in name AND "Crime" in genre
curl -H "Accept: application/json" "http://localhost:8080/movies/search?name=Family&genre=Crime"
```

**Expected Results:** Movies matching ALL specified criteria (AND logic)

##### Invalid Request
```bash
# No parameters provided
curl -H "Accept: application/json" "http://localhost:8080/movies/search"
```

**Expected Response:** 400 Bad Request with pirate-themed error message

---

### 3. Get Movie Details

**Endpoint:** `GET /movies/{id}/details`

**Description:** Get detailed information for a specific movie.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Response:** HTML page with movie details and reviews

**Example:**
```bash
curl http://localhost:8080/movies/1/details
```

## Search Behavior

### Text Matching Rules

1. **Case-Insensitive:** All text searches ignore case
   - `"prison"` matches `"The Prison Escape"`
   - `"DRAMA"` matches `"Drama"`

2. **Partial Matching:** Substring matching for name and genre
   - `"Prison"` matches `"The Prison Escape"`
   - `"Crime"` matches `"Crime/Drama"`

3. **Whitespace Handling:** Leading/trailing spaces are trimmed
   - `"  Prison  "` is treated as `"Prison"`

### ID Matching Rules

1. **Exact Match:** ID must match exactly
2. **Positive Numbers Only:** IDs must be > 0
3. **Long Type:** Accepts long integers

### Genre Matching Rules

1. **Multi-Genre Support:** Handles complex genres like "Crime/Drama"
2. **Partial Matching:** `"Crime"` matches `"Crime/Drama"`
3. **Case-Insensitive:** `"action"` matches `"Action/Sci-Fi"`

### Combination Logic

When multiple parameters are provided, **AND logic** is used:
- All specified criteria must match
- Empty/null parameters are ignored
- At least one valid parameter must be provided

## Error Handling

### Client Errors (4xx)

#### 400 Bad Request
**Cause:** No valid search parameters provided

**HTML Response:**
- Returns movies page with error message
- Shows all movies
- Preserves search form with entered values

**JSON Response:**
```json
{
  "error": "Arrr! Ye must provide at least one search parameter (name, id, or genre), matey!"
}
```

### Success Scenarios

#### Empty Results
**HTML Response:**
- Shows "no results" message with pirate theme
- Provides suggestions to adjust search criteria
- Maintains search form with entered values

**JSON Response:**
```json
[]
```

## Available Movies

The system contains 12 movies with the following genres:

- **Action/Crime**
- **Action/Sci-Fi** 
- **Adventure/Fantasy**
- **Adventure/Sci-Fi**
- **Crime/Drama**
- **Drama**
- **Drama/History**
- **Drama/Romance**
- **Drama/Thriller**

## Rate Limiting

No rate limiting currently implemented - sail freely!

## Caching

No caching headers currently set - fresh data on every request.

## Logging

All search requests are logged with pirate-themed messages:

```
INFO  - Arrr! Movie search request received - name='Prison', id='null', genre='null'
INFO  - Arrr! Search expedition complete! Found 1 movies in our treasure chest
```

## Testing the API

### Using curl

```bash
# Test basic search
curl "http://localhost:8080/movies/search?name=Prison"

# Test JSON response
curl -H "Accept: application/json" "http://localhost:8080/movies/search?genre=Drama"

# Test combined search
curl -H "Accept: application/json" "http://localhost:8080/movies/search?name=Family&genre=Crime"

# Test error handling
curl -H "Accept: application/json" "http://localhost:8080/movies/search"
```

### Using Browser

Navigate to:
- `http://localhost:8080/movies` - Use the search form
- `http://localhost:8080/movies/search?name=Prison` - Direct URL search

### Using JavaScript

```javascript
// Fetch movies with Drama genre
fetch('/movies/search?genre=Drama', {
  headers: {
    'Accept': 'application/json'
  }
})
.then(response => response.json())
.then(movies => console.log('Found movies:', movies))
.catch(error => console.error('Search failed:', error));
```

## Performance Considerations

- **In-Memory Search:** All searches performed on in-memory movie collection
- **No Pagination:** All matching results returned in single response
- **Small Dataset:** 12 movies total, suitable for demonstration
- **No Indexing:** Linear search through movie collection

## Future Enhancements

Potential improvements for production use:

1. **Pagination:** Add `page` and `size` parameters
2. **Sorting:** Add `sort` parameter for result ordering
3. **Additional Filters:** Search by director, year, rating
4. **Fuzzy Matching:** Handle typos and similar terms
5. **Caching:** Add response caching for better performance
6. **Rate Limiting:** Implement request throttling
7. **Database Integration:** Replace in-memory storage

## Troubleshooting

### Common Issues

1. **No Results Found**
   - Check spelling of search terms
   - Try partial matches instead of exact terms
   - Verify movie data is loaded (check application logs)

2. **400 Bad Request**
   - Ensure at least one search parameter is provided
   - Check parameter names are correct (`name`, `id`, `genre`)

3. **Unexpected Results**
   - Remember searches are case-insensitive and use partial matching
   - Multiple parameters use AND logic (all must match)

### Debug Information

Enable debug logging to see detailed search operations:

```yaml
# application.yml
logging:
  level:
    com.amazonaws.samples.qdevmovies.movies: DEBUG
```

---

*Arrr! May this documentation guide ye safely through the treacherous waters of movie searching, matey! 🏴‍☠️*