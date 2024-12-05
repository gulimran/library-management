package imran.exercise.library.management.adapter;

import imran.exercise.library.management.domain.BookEntity;
import imran.exercise.library.management.dto.Book;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookEntityToBookDtoAdapter {

    public Book adapt(BookEntity bookEntity) {
            return new Book(
                bookEntity.isbn(),
                bookEntity.title(),
                bookEntity.author(),
                bookEntity.publicationYear(),
                bookEntity.availableCopies());
    }

    public List<Book> adapt(List<BookEntity> bookEntities) {
            return bookEntities.stream().map(this::adapt).toList();
    }
}
