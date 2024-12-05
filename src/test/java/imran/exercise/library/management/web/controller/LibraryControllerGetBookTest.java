package imran.exercise.library.management.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.exception.BookNotFoundException;
import imran.exercise.library.management.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class LibraryControllerGetBookTest {

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
    void getBookByIsbn_ForAnExistingBook_ReturnsOk() throws Exception {
        when(library.findBookByISBN(isbn))
            .thenReturn(new Book(isbn, title, author, publicationYear, availableCopies));

        mockMvc.perform(get("/api/books/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("isbn", is(isbn)))
            .andExpect(jsonPath("title", is(title)))
            .andExpect(jsonPath("author", is(author)))
            .andExpect(jsonPath("publicationYear", is(publicationYear)))
            .andExpect(jsonPath("availableCopies", is(availableCopies)));

        verify(library).findBookByISBN(isbn);
    }

    @Test
    void getBookByIsbn_WhenBookNotFound_ReturnsNotFound() throws Exception {
        when(library.findBookByISBN(isbn))
            .thenThrow(new BookNotFoundException());

        mockMvc.perform(get("/api/books/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(library).findBookByISBN(isbn);
    }

    @Test
    void getBookByIsbn_WhenServiceCallFail_ReturnsInternalServerError() throws Exception {
        when(library.findBookByISBN(isbn))
            .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/books/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(library).findBookByISBN(isbn);
    }

    @Test
    void getBooksByAuthor_ForAnExistingBook_ReturnsOk() throws Exception {
        when(library.findBooksByAuthor(author))
            .thenReturn(List.of(new Book(isbn, title, author, publicationYear, availableCopies)));

        mockMvc.perform(get("/api/books?author=" + author)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", is(1)))
            .andExpect(jsonPath("$[0].isbn", is(isbn)))
            .andExpect(jsonPath("$[0].title", is(title)))
            .andExpect(jsonPath("$[0].author", is(author)))
            .andExpect(jsonPath("$[0].publicationYear", is(publicationYear)))
            .andExpect(jsonPath("$[0].availableCopies", is(availableCopies)));

        verify(library).findBooksByAuthor(author);
    }

    @Test
    void getBookByAuthor_WhenBookNotFound_ReturnsEmptyList() throws Exception {
        when(library.findBooksByAuthor(author))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/books?author=" + author)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", is(0)));

        verify(library).findBooksByAuthor(author);
    }
}
