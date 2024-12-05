package imran.exercise.library.management.service;

import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.exception.BookNotFoundException;

import java.util.List;

public interface Library {

    /**
     * Adds a new book to the library
     * @param book - The book to be added
     * @return The new book that is added
     */
    Book addBook(Book book);

    /**
     * Removes a book from the library by ISBN
     * @param isbn - The ISBN of the book to be removed
     */
    void removeBook(String isbn);

    /**
     * Returns a book by its ISBN
     * @param isbn - The ISBN of the book
     * @return The book for given ISBN
     */
    Book findBookByISBN(String isbn) throws BookNotFoundException;

    /**
     * Returns a list of books by a given author
     * @param author - The author of the book(s) in this Library
     * @return The list of books by the given author
     */
    List<Book> findBooksByAuthor(String author);

    /**
     * Decreases the available copies of a book by 1 in this Library
     * @param isbn - The ISBN of the book to be borrowed
     * @return The existing book that is now borrowed
     */
    Book borrowBook(String isbn);

    /**
     * Increases the available copies of a book by 1 in this Library
     * @param isbn - The ISBN of the book to be returned
     * @return The book that is now returned
     */
    Book returnBook(String isbn);
}
