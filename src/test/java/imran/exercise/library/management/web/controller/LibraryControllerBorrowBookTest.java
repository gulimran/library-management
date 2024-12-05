package imran.exercise.library.management.web.controller;

import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.exception.BookNotFoundException;
import imran.exercise.library.management.exception.BookUnavailableException;
import imran.exercise.library.management.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class LibraryControllerBorrowBookTest {

    private static final String isbn = "testIsbn";
    private static final String title = "title";
    private static final String author = "author";
    private static final Integer publicationYear = 2024;
    private static final Integer availableCopies = 1;
    private static final Book book = new Book(isbn, title, author, publicationYear, availableCopies);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService library;

    @Test
    void borrowBook_ForAnExistingBook_ReturnsOk() throws Exception {
        when(library.borrowBook(isbn)).thenReturn(book);

        mockMvc.perform(put("/api/books/borrow/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("isbn", is(isbn)))
            .andExpect(jsonPath("title", is(title)))
            .andExpect(jsonPath("author", is(author)))
            .andExpect(jsonPath("publicationYear", is(publicationYear)))
            .andExpect(jsonPath("availableCopies", is(availableCopies)));

        verify(library).borrowBook(isbn);
    }

    @Test
    void borrowBook_ForNonExistingBook_ReturnsNotFound() throws Exception {
        when(library.borrowBook(isbn)).thenThrow(new BookNotFoundException());

        mockMvc.perform(put("/api/books/borrow/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(library).borrowBook(isbn);
    }

    @Test
    void borrowBook_ForNoAvailableCopies_ReturnsBadRequest() throws Exception {
        when(library.borrowBook(isbn)).thenThrow(new BookUnavailableException());

        mockMvc.perform(put("/api/books/borrow/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(library).borrowBook(isbn);
    }

    @Test
    void borrowBook_WhenServiceCallFails_ReturnsInternalServerError() throws Exception {
        when(library.borrowBook(isbn))
            .thenThrow(new RuntimeException());

        mockMvc.perform(put("/api/books/borrow/" + isbn)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        verify(library).borrowBook(isbn);
    }
}
