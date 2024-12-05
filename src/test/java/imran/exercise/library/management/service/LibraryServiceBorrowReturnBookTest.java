package imran.exercise.library.management.service;

import imran.exercise.library.management.adapter.BookDtoToBookEntityAdapter;
import imran.exercise.library.management.adapter.BookEntityToBookDtoAdapter;
import imran.exercise.library.management.cache.SimpleNoExpiryCache;
import imran.exercise.library.management.domain.BookEntity;
import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.exception.BookNotFoundException;
import imran.exercise.library.management.exception.BookUnavailableException;
import imran.exercise.library.management.persistence.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LibraryServiceBorrowReturnBookTest {

    private static final String isbn = "testIsbn";
    private static final String title = "title";
    private static final String author = "author";
    private static final Integer publicationYear = 2024;
    private static final Integer availableCopies = 1;

    private final LibraryRepository libraryRepository = mock(LibraryRepository.class);

    private Library underTest;

    @BeforeEach
    void setUp() {
        underTest = new LibraryService(
            new SimpleNoExpiryCache(),
            libraryRepository,
            new BookEntityToBookDtoAdapter(),
            new BookDtoToBookEntityAdapter());
    }

    @Test
    void borrowBook_DecrementsAvailableCopies() {
        BookEntity bookEntity = new BookEntity(isbn, title, author, publicationYear, availableCopies);
        Book borrowedBookExpected = new Book(isbn, title, author, publicationYear, 0);

        when(libraryRepository.findById(isbn)).thenReturn(Optional.of(bookEntity));

        Book borrowedBookActual = underTest.borrowBook(isbn);

        assertThat(borrowedBookActual, is(borrowedBookExpected));
    }

    @Test
    void borrowBook_ThrowsExceptionWhenNoAvailableCopies() {
        BookEntity bookEntity = new BookEntity(isbn, title, author, publicationYear, 0);

        when(libraryRepository.findById(isbn)).thenReturn(Optional.of(bookEntity));

        assertThrows(BookUnavailableException.class, () ->
            underTest.borrowBook(isbn), "BookUnavailableException error was expected");
    }

    @Test
    void borrowBook_ThrowsBookNotFoundException() {
        when(libraryRepository.findById(isbn)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () ->
            underTest.borrowBook(isbn), "BookNotFoundException error was expected");
    }

    @Test
    void returnBook_IncrementsAvailableCopies() {
        BookEntity bookEntity = new BookEntity(isbn, title, author, publicationYear, availableCopies);
        Book returnedBookExpected = new Book(isbn, title, author, publicationYear, 2);

        when(libraryRepository.findById(isbn)).thenReturn(Optional.of(bookEntity));

        Book returnedBookActual = underTest.returnBook(isbn);

        assertThat(returnedBookActual, is(returnedBookExpected));
    }

    @Test
    void returnBook_ThrowsBookNotFoundException() {
        when(libraryRepository.findById(isbn)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () ->
            underTest.returnBook(isbn), "BookNotFoundException error was expected");
    }
}
