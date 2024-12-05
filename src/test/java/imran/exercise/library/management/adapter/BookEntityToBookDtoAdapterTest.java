package imran.exercise.library.management.adapter;

import imran.exercise.library.management.domain.BookEntity;
import imran.exercise.library.management.dto.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class BookEntityToBookDtoAdapterTest {

    private static final String isbn = "testIsbn";
    private static final String title = "title";
    private static final String author = "author";
    private static final Integer publicationYear = 2024;
    private static final Integer availableCopies = 1;
    private static final Book book = new Book(isbn, title, author, publicationYear, availableCopies);

    private BookEntityToBookDtoAdapter underTest;

    @BeforeEach
    void setUp() {
        underTest = new BookEntityToBookDtoAdapter();
    }

    @Test
    void returnsBookDto_WhenCopiesAvailable() {
        Book actual = underTest.adapt(bookEntity());
        assertThat(actual, is(book));
    }

    private BookEntity bookEntity() {
        return new BookEntity(isbn, title, author, publicationYear, 1);
    }

    private BookEntity unavailableBookEntity() {
        return new BookEntity(isbn, title, author, publicationYear, 0);
    }

}
