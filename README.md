# Library Management

A Concurrent Library Management System with RESTful API.

A thread-safe `Library` class that manages a collection of books concurrently. It has the following methods:

- `addBook(Book book)`: Adds a new book to the library
- `removeBook(String isbn)`: Removes a book from the library by ISBN
- `findBookByISBN(String isbn)`: Returns a book by its ISBN
- `findBooksByAuthor(String author)`: Returns a list of books by a given author
- `borrowBook(String isbn)`: Decreases the available copies of a book by 1
- `returnBook(String isbn)`: Increases the available copies of a book by 1

## Build and Run

Gradle is used as a build tool.  Use the following command to build

```
./gradlew clean build
```

Use the following command to run all unit and integration test

```
./gradlew test
```

Integration tests are in `LibraryIntegrationTest` class.

To run the application

```
./gradlew bootRun
```

## Add book

Adds a new book to the library

### Request

```
curl -v -X POST http://localhost:8080/api/books \
-H "Content-Type: application/json" \
-d '{"isbn": "isbn-1234", "title": "BookTitle", "author": "BookAuthor",  "publicationYear": 2022, "availableCopies": 1}'
```
### Response

```
HTTP/1.1 200
Content-Type: application/json
{"isbn":"isbn-1234","title":"BookTitle","author":"BookAuthor","publicationYear":2022,"availableCopies":1}%
```

## Find Book By ISBN

Returns a book by its ISBN

### Request

```
curl -v -X GET http://localhost:8080/api/books/isbn-1234
```

### Response

```
HTTP/1.1 200
Content-Type: application/json
{"isbn":"isbn-1234","title":"BookTitle","author":"BookAuthor","publicationYear":2022,"availableCopies":1}
```

## Get Books By Author

Returns a list of books by a given author

### Request

```
curl -v -X GET http://localhost:8080/api/books?author=BookAuthor
```

### Response

```
HTTP/1.1 200
Content-Type: application/json
[{"isbn":"isbn-1234","title":"BookTitle","author":"BookAuthor","publicationYear":2022,"availableCopies":1}]
```

## Borrow Book

Returns borrowed book and decreases the available copies of the book by 1

### Request

```
curl -v -X PUT http://localhost:8080/api/books/borrow/isbn-1234 
```

### Response

```
HTTP/1.1 200
Content-Type: application/json
{"isbn":"isbn-1234","title":"BookTitle","author":"BookAuthor","publicationYear":2022,"availableCopies":0}
```

## Return Book

Returns returned book and increases the available copies of the book by 1

### Request

```
curl -v -X PUT http://localhost:8080/api/books/return/isbn-1234
```

### Response

```
HTTP/1.1 200
Content-Type: application/json
{"isbn":"isbn-1234","title":"BookTitle","author":"BookAuthor","publicationYear":2022,"availableCopies":1}
```

## Remove Book

Removes a book from the library by ISBN

### Request

```
curl -v -X DELETE http://localhost:8080/api/books/isbn-1234
```

### Response

```
HTTP/1.1 200
```

## Application

Application is a ReST API based system that uses `Spring Boot` framework, in-memory H2 database and `JUnit` for unit and integration testing.

The structure of the application is:

- [LibraryController](src%2Fmain%2Fjava%2Fimran%2Fexercise%2Flibrary%2Fmanagement%2Fweb%2Fcontroller%2FLibraryController.java) - depends on [Library](src%2Fmain%2Fjava%2Fimran%2Fexercise%2Flibrary%2Fmanagement%2Fservice%2FLibrary.java) interface
- [LibraryService](src%2Fmain%2Fjava%2Fimran%2Fexercise%2Flibrary%2Fmanagement%2Fservice%2FLibraryService.java) - implements [Library](src%2Fmain%2Fjava%2Fimran%2Fexercise%2Flibrary%2Fmanagement%2Fservice%2FLibrary.java) interface and depends on [LibraryRepository](src%2Fmain%2Fjava%2Fimran%2Fexercise%2Flibrary%2Fmanagement%2Fpersistence%2FLibraryRepository.java), [BookDtoToBookEntityAdapter](src%2Fmain%2Fjava%2Fimran%2Fexercise%2Flibrary%2Fmanagement%2Fadapter%2FBookDtoToBookEntityAdapter.java) and [BookEntityToBookDtoAdapter](src%2Fmain%2Fjava%2Fimran%2Fexercise%2Flibrary%2Fmanagement%2Fadapter%2FBookEntityToBookDtoAdapter.java)
- [LibraryRepository](src%2Fmain%2Fjava%2Fimran%2Fexercise%2Flibrary%2Fmanagement%2Fpersistence%2FLibraryRepository.java) interacts with H2 database using JPA.

## Database

This system uses in-memory H2 database.  The database console can be accessed from:

http://localhost:8080/h2-console

The database configuration and credentials are in the application.properties.

## Thread Safety

For the following operations:

- borrow book
- return book

We are modifying the available number of copies of a book by incrementing or decrementing the number in the database.  For these operations, thread safety is implemented by using object locks for synchronisation around the operation using ConcurrentHashMap.

## Caching

A simple in memory cache is used that does not expire.  This [SimpleNoExpiryCache](src%2Fmain%2Fjava%2Fimran%2Fexercise%2Flibrary%2Fmanagement%2Fcache%2FSimpleNoExpiryCache.java) is backed by ConcurrentHashMap.

## Code Coverage

The test coverage is `100%`.  Only exception and global exception handler (i.e. controller advice) classes are not covered. 

## Future Improvements

The following list highlights the tasks that can further improve the performance and quality of this application:

- Behaviour testing - Use Cucumber to create scenarios for functional testing in business readable language.
- Input validation - Validation of input data for the rest endpoints such when adding a book.
- Rate limiting - Implement a basic rate limiting mechanism for the API endpoints.
- Authentication - Add authentication to the API using JWT tokens.

Note: Currently the above tasks are not done due to time constraints for this exercise.