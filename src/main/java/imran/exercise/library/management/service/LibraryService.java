package imran.exercise.library.management.service;

import imran.exercise.library.management.adapter.BookDtoToBookEntityAdapter;
import imran.exercise.library.management.adapter.BookEntityToBookDtoAdapter;
import imran.exercise.library.management.cache.SimpleNoExpiryCache;
import imran.exercise.library.management.domain.BookEntity;
import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.exception.BookAlreadyExistsException;
import imran.exercise.library.management.exception.BookNotFoundException;
import imran.exercise.library.management.exception.BookUnavailableException;
import imran.exercise.library.management.persistence.LibraryRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LibraryService implements Library {

    private final SimpleNoExpiryCache cache;
    private final LibraryRepository libraryRepository;
    private final BookEntityToBookDtoAdapter toBookDtoAdapter;
    private final BookDtoToBookEntityAdapter toBookDomainAdapter;

    private final ConcurrentHashMap<String, Object> borrowLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> returnLocks = new ConcurrentHashMap<>();

    public LibraryService(
        SimpleNoExpiryCache cache, LibraryRepository libraryRepository,
        BookEntityToBookDtoAdapter toBookDtoAdapter, BookDtoToBookEntityAdapter toBookDomainAdapter) {
        this.cache = cache;
        this.libraryRepository = libraryRepository;
        this.toBookDtoAdapter = toBookDtoAdapter;
        this.toBookDomainAdapter = toBookDomainAdapter;
    }

    @Override
    public Book addBook(Book book) {
        if (libraryRepository.findById(book.isbn()).isPresent()) {
            throw new BookAlreadyExistsException();
        }
        BookEntity saved = libraryRepository.save(toBookDomainAdapter.adapt(book));
        cache.put(book.isbn(), book);
        return toBookDtoAdapter.adapt(saved);
    }

    @Override
    public void removeBook(String isbn) {
        BookEntity bookEntity = libraryRepository.findById(isbn).orElseThrow(BookNotFoundException::new);
        libraryRepository.delete(bookEntity);
        cache.remove(isbn);
    }

    @Override
    public Book findBookByISBN(String isbn) {
        Book book = cache.get(isbn);

        if (book != null) {
            return book;
        }

        return libraryRepository.findById(isbn).map(toBookDtoAdapter::adapt).orElseThrow(BookNotFoundException::new);
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        List<Book> cachedList = cache.getAll().stream().filter(book -> book.author().equals(author)).toList();
        if (!cachedList.isEmpty()) {
            return cachedList;
        }

        List<BookEntity> bookEntities = libraryRepository.findAll(booksByAuthor(author));
        return toBookDtoAdapter.adapt(bookEntities);
    }

    @Override
    public Book borrowBook(String isbn) {
        Object lock = borrowLocks.computeIfAbsent(isbn, key -> new Object());

        synchronized (lock) {
            try {
                BookEntity bookEntity = libraryRepository.findById(isbn).orElseThrow(BookNotFoundException::new);

                if (bookEntity.availableCopies() <= 0) {
                    throw new BookUnavailableException();
                }

                bookEntity.setAvailableCopies(bookEntity.availableCopies() - 1);
                libraryRepository.save(bookEntity);
                return toBookDtoAdapter.adapt(bookEntity);
            } finally {
                borrowLocks.remove(isbn);
            }
        }
    }

    @Override
    public Book returnBook(String isbn) {
        Object lock = returnLocks.computeIfAbsent(isbn, key -> new Object());

        synchronized (lock) {
            try {
                BookEntity bookEntity = libraryRepository.findById(isbn).orElseThrow(BookNotFoundException::new);

                bookEntity.setAvailableCopies(bookEntity.availableCopies() + 1);
                libraryRepository.save(bookEntity);
                return toBookDtoAdapter.adapt(bookEntity);
            } finally {
                returnLocks.remove(isbn);
            }
        }
    }

    private Example<BookEntity> booksByAuthor(String author) {
        return Example.of(new BookEntity(null, null, author, null, null));
    }
}
