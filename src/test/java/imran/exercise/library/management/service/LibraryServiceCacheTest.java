package imran.exercise.library.management.service;

import imran.exercise.library.management.adapter.BookDtoToBookEntityAdapter;
import imran.exercise.library.management.adapter.BookEntityToBookDtoAdapter;
import imran.exercise.library.management.cache.SimpleNoExpiryCache;
import imran.exercise.library.management.domain.BookEntity;
import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.persistence.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class LibraryServiceCacheTest {

    private static final String isbn = "testIsbn";
    private static final String title = "title";
    private static final String author = "author";
    private static final Integer publicationYear = 2024;
    private static final Integer availableCopies = 1;

    private static final Book book = new Book(isbn, title, author, publicationYear, availableCopies);
    private static final BookEntity bookEntity = new BookEntity(isbn, title, author, publicationYear, availableCopies);

    private final LibraryRepository libraryRepository = mock(LibraryRepository.class);
    private final SimpleNoExpiryCache cache = new SimpleNoExpiryCache();

    private Library underTest;

    @BeforeEach
    void setUp() {
        cache.clear();
        underTest = new LibraryService(
            cache,
            libraryRepository,
            new BookEntityToBookDtoAdapter(),
            new BookDtoToBookEntityAdapter());
    }

    @Test
    void addBook_AddsBookToCache() {
        when(libraryRepository.findById(isbn)).thenReturn(Optional.empty());
        when(libraryRepository.save(bookEntity)).thenReturn(bookEntity);

        underTest.addBook(book);

        assertEquals(cache.get(book.isbn()), book);
    }

    @Test
    void removeBook_RemovesBookFromCache() {
        cache.put(book.isbn(), book);
        when(libraryRepository.findById(isbn)).thenReturn(Optional.of(bookEntity));

        underTest.removeBook(isbn);

        assertNull(cache.get(book.isbn()));
    }

    @Test
    void findBookByIsbn_ReturnsBookFromCache() {
        cache.put(book.isbn(), book);

        assertEquals(underTest.findBookByISBN(isbn), cache.get(book.isbn()));
        verifyNoInteractions(libraryRepository);
    }

    @Test
    void findBookByAuthor_ReturnsMultipleBooks() {
        Book anotherBook = new Book("isbn2", "title2", author, 1999, 2);

        cache.put(book.isbn(), book);
        cache.put(anotherBook.isbn(), anotherBook);

        assertThat(underTest.findBooksByAuthor(author), containsInAnyOrder(List.of(book, anotherBook).toArray()));
        verifyNoInteractions(libraryRepository);
    }
}
