package imran.exercise.library.management.service;

import imran.exercise.library.management.adapter.BookDtoToBookEntityAdapter;
import imran.exercise.library.management.adapter.BookEntityToBookDtoAdapter;
import imran.exercise.library.management.cache.SimpleNoExpiryCache;
import imran.exercise.library.management.domain.BookEntity;
import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.exception.BookNotFoundException;
import imran.exercise.library.management.persistence.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LibraryServiceFindBookTest {

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
    void findBookByIsbn_ReturnsBook() {
        when(libraryRepository.findById(isbn)).thenReturn(Optional.of(bookEntity));
        assertThat(underTest.findBookByISBN(isbn), is(book));
    }

    @Test
    void findBookByIsbn_ThrowsBookNotFoundException() {
        when(libraryRepository.findById(isbn)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () ->
            underTest.findBookByISBN(isbn), "BookNotFoundException error was expected");
    }

    @Test
    void findBookByIsbn_ThrowsException() {
        when(libraryRepository.findById(isbn)).thenThrow(new RuntimeException("junit test"));
        assertThrows(RuntimeException.class, () ->
            underTest.findBookByISBN(isbn), "RuntimeException error was expected");
    }

    @Test
    void findBookByAuthor_ReturnsABook() {
        when(libraryRepository.findAll(booksByAuthor(author))).thenReturn(List.of(bookEntity));
        assertThat(underTest.findBooksByAuthor(author), is(List.of(book)));
    }

    @Test
    void findBookByAuthor_ReturnsMultipleBooks() {
        BookEntity anotherBookEntity = new BookEntity("isbn2", "title2", author, 1999, 2);
        Book anotherBook = new Book("isbn2", "title2", author, 1999, 2);

        when(libraryRepository.findAll(booksByAuthor(author))).thenReturn(List.of(bookEntity, anotherBookEntity));
        assertThat(underTest.findBooksByAuthor(author), is(List.of(book, anotherBook)));
    }

    @Test
    void findBookByAuthor_ReturnsEmptyList() {
        when(libraryRepository.findAll(booksByAuthor(author))).thenReturn(List.of());
        assertThat(underTest.findBooksByAuthor(author), is(List.of()));
    }

    private Example<BookEntity> booksByAuthor(String author) {
        return Example.of(new BookEntity(null, null, author, null, null));
    }
}
