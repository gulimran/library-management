package imran.exercise.library.management.service;

import imran.exercise.library.management.adapter.BookDtoToBookEntityAdapter;
import imran.exercise.library.management.adapter.BookEntityToBookDtoAdapter;
import imran.exercise.library.management.cache.SimpleNoExpiryCache;
import imran.exercise.library.management.domain.BookEntity;
import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.exception.BookAlreadyExistsException;
import imran.exercise.library.management.exception.BookNotFoundException;
import imran.exercise.library.management.persistence.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class LibraryServiceAddRemoveBookTest {

    private static final String isbn = "testIsbn";
    private static final String title = "title";
    private static final String author = "author";
    private static final Integer publicationYear = 2024;
    private static final Integer availableCopies = 1;

    private static final Book book = new Book(isbn, title, author, publicationYear, availableCopies);
    private static final BookEntity bookEntity = new BookEntity(isbn, title, author, publicationYear, availableCopies);

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
    void addBook_AddsNewNonExistingBook() {
        when(libraryRepository.findById(isbn)).thenReturn(Optional.empty());
        when(libraryRepository.save(bookEntity)).thenReturn(bookEntity);
        underTest.addBook(book);

        verify(libraryRepository).findById(isbn);
        verify(libraryRepository).save(bookEntity);
        verifyNoMoreInteractions(libraryRepository);
    }

    @Test
    void addBook_ThrowsExceptionWhenBookAlreadyExists() {
        when(libraryRepository.findById(isbn)).thenReturn(Optional.of(bookEntity));

        assertThrows(BookAlreadyExistsException.class, () ->
            underTest.addBook(book), "BookAlreadyExistsException error was expected");
    }

    @Test
    void removeBook_RemovesAnExistingBook() {
        when(libraryRepository.findById(isbn)).thenReturn(Optional.of(bookEntity));
        underTest.removeBook(isbn);

        verify(libraryRepository).findById(isbn);
        verify(libraryRepository).delete(bookEntity);
        verifyNoMoreInteractions(libraryRepository);
    }

    @Test
    void removeBook_ThrowsExceptionWhenBookDoesNotExists() {
        when(libraryRepository.findById(isbn)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () ->
            underTest.removeBook(isbn), "BookNotFoundException error was expected");
    }
}
