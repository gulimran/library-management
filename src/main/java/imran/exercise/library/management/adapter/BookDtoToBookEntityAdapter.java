package imran.exercise.library.management.adapter;

import imran.exercise.library.management.domain.BookEntity;
import imran.exercise.library.management.dto.Book;
import org.springframework.stereotype.Component;

@Component
public class BookDtoToBookEntityAdapter {

    public BookEntity adapt(Book book) {
        return new BookEntity(
            book.isbn(),
            book.title(),
            book.author(),
            book.publicationYear(),
            book.availableCopies());
    }
}
