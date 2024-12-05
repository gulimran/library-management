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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class LibraryControllerRemoveBookTest {

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
    void removeBook_ForAnExistingBook_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/books/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(library).removeBook(isbn);
    }

    @Test
    void removeBook_ForANonExistingBook_ReturnsBadRequest() throws Exception {
        doThrow(new BookAlreadyExistsException()).when(library).removeBook(isbn);

        mockMvc.perform(delete("/api/books/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(library).removeBook(isbn);
    }

    @Test
    void removeBook_WhenServiceCallFails_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException()).when(library).removeBook(isbn);

        mockMvc.perform(delete("/api/books/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(library).removeBook(isbn);
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
