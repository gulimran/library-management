package imran.exercise.library.management.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.exception.BookAlreadyExistsException;
import imran.exercise.library.management.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class LibraryControllerAddBookTest {

    private static final String isbn = "testIsbn";
    private static final String title = "title";
    private static final String author = "author";
    private static final Integer publicationYear = 2024;
    private static final Integer availableCopies = 1;
    private static final Book book = new Book(isbn, title, author, publicationYear, availableCopies);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService library;

    @Test
    void addBook_ForANonExistingBook_ReturnsOk() throws Exception {
        when(library.addBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody())))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("isbn", is(isbn)))
            .andExpect(jsonPath("title", is(title)))
            .andExpect(jsonPath("author", is(author)))
            .andExpect(jsonPath("publicationYear", is(publicationYear)));

        verify(library).addBook(book);
    }

    @Test
    void addBook_ForAnExistingBook_ReturnsBadRequest() throws Exception {
        when(library.addBook(any(Book.class))).thenThrow(new BookAlreadyExistsException());

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody())))
                .andExpect(status().isBadRequest());

        verify(library).addBook(book);
    }

    @Test
    void addBook_WhenServiceCallFails_ReturnsInternalServerError() throws Exception {
        when(library.addBook(book))
            .thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody())))
            .andExpect(status().isInternalServerError());

        verify(library).addBook(book);
    }

    private static Map<String, Object> requestBody() {
        return Map.of(
            "isbn", isbn,
            "title", title,
            "author", author,
            "publicationYear", publicationYear,
            "availableCopies", availableCopies
        );
    }
}
