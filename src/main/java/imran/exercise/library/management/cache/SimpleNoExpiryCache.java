package imran.exercise.library.management.cache;

import imran.exercise.library.management.dto.Book;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleNoExpiryCache {

    public final ConcurrentHashMap<String, Book> cache = new ConcurrentHashMap<>();

    public Book put(String isbn, Book value) {
        Objects.requireNonNull(isbn);
        return cache.put(isbn, value);
    }

    public Book get(String isbn) {
        Objects.requireNonNull(isbn);
        return cache.get(isbn);
    }

    public List<Book> getAll() {
        return new ArrayList<>(cache.values());
    }

    public Book remove(String isbn) {
        Objects.requireNonNull(isbn);
        return cache.remove(isbn);
    }

    public void clear() {
        cache.clear();
    }
}
