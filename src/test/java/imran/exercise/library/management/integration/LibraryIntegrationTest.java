package imran.exercise.library.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import imran.exercise.library.management.domain.BookEntity;
import imran.exercise.library.management.persistence.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LibraryIntegrationTest {

    private static final  BookEntity book1 = new BookEntity("isbn-1", "Title 1", "Author 1", 2020, 1);
    private static final  BookEntity book2 = new BookEntity("isbn-2", "Title 2", "Author 1", 2021, 1);
    private static final  BookEntity book3 = new BookEntity("isbn-3", "Title 3", "Author 3", 2022, 1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LibraryRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        resetDatabase();
    }

    @Test
    void getBookByIsbn_ReturnsBook() throws Exception {
        mockMvc.perform(get("/api/books/isbn-1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isbn", is(book1.isbn())))
            .andExpect(jsonPath("$.title", is(book1.title())))
            .andExpect(jsonPath("$.author", is(book1.author())))
            .andExpect(jsonPath("$.publicationYear", is(book1.publicationYear())))
            .andExpect(jsonPath("$.availableCopies", is(book1.availableCopies())));
    }

    @Test
    void getBooksByAuthor_ReturnsMultipleMatchingBooks() throws Exception {
        mockMvc.perform(get("/api/books?author=Author 1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", is(2)))
            .andExpect(jsonPath("$[0].isbn", is(book1.isbn())))
            .andExpect(jsonPath("$[0].title", is(book1.title())))
            .andExpect(jsonPath("$[0].author", is(book1.author())))
            .andExpect(jsonPath("$[0].publicationYear", is(book1.publicationYear())))
            .andExpect(jsonPath("$[0].availableCopies", is(book1.availableCopies())))
            .andExpect(jsonPath("$[1].isbn", is(book2.isbn())))
            .andExpect(jsonPath("$[1].title", is(book2.title())))
            .andExpect(jsonPath("$[1].author", is(book2.author())))
            .andExpect(jsonPath("$[1].publicationYear", is(book2.publicationYear())))
            .andExpect(jsonPath("$[1].availableCopies", is(book2.availableCopies())));
    }

    @Test
    void addBook_ReturnsAddedBook_AddsBookToDatabase() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody())))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("isbn", is(book3.isbn())))
            .andExpect(jsonPath("title", is(book3.title())))
            .andExpect(jsonPath("author", is(book3.author())))
            .andExpect(jsonPath("publicationYear", is(book3.publicationYear())))
            .andExpect(jsonPath("availableCopies", is(book3.availableCopies())));

        assertThat(repository.findById(book3.isbn()), is(Optional.of(book3)));
    }

    @Test
    void removeBook_ReturnsRemovedBook_DeletesBookFromDatabase() throws Exception {
        mockMvc.perform(delete("/api/books/isbn-1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertThat(repository.findById("isbn-1"), is(Optional.empty()));
    }

    @Test
    void borrowBook_DecrementsNumberOfAvailableCopies() throws Exception {
        assertThat(repository.findById("isbn-1"), is(Optional.of(book1)));

        mockMvc.perform(put("/api/books/borrow/isbn-1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.isbn", is(book1.isbn())))
            .andExpect(jsonPath("$.title", is(book1.title())))
            .andExpect(jsonPath("$.author", is(book1.author())))
            .andExpect(jsonPath("$.publicationYear", is(book1.publicationYear())))
            .andExpect(jsonPath("$.availableCopies", is(0)));

        assertThat(repository.findById(book1.isbn()),
            is(Optional.of(new BookEntity("isbn-1", "Title 1", "Author 1", 2020, 0))));
    }

    @Test
    void returnBook_IncrementsNumberOfAvailableCopies() throws Exception {
        repository.save(new BookEntity("isbn-1", "Title 1", "Author 1", 2020, 0));
        assertThat(repository.findById("isbn-1"),
            is(Optional.of(new BookEntity("isbn-1", "Title 1", "Author 1", 2020, 0))));

        mockMvc.perform(put("/api/books/return/isbn-1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.isbn", is(book1.isbn())))
            .andExpect(jsonPath("$.title", is(book1.title())))
            .andExpect(jsonPath("$.author", is(book1.author())))
            .andExpect(jsonPath("$.publicationYear", is(book1.publicationYear())))
            .andExpect(jsonPath("$.availableCopies", is(book1.availableCopies())));

        assertThat(repository.findById("isbn-1"), is(Optional.of(book1)));
    }

    private void resetDatabase() {
        repository.deleteAll();
        repository.save(book1);
        repository.save(book2);
    }

    private static Map<String, Object> requestBody() {
        return Map.of(
            "isbn", "isbn-3",
            "title", "Title 3",
            "author", "Author 3",
            "publicationYear", 2022,
            "availableCopies", 1
        );
    }
}
