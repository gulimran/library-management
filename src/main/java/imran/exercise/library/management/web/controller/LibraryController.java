package imran.exercise.library.management.web.controller;

import imran.exercise.library.management.dto.Book;
import imran.exercise.library.management.service.Library;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class LibraryController {

    private final Library library;

    public LibraryController(Library library) {
        this.library = library;
    }

    /**
     * Returns a book by its ISBN
     * @param isbn - The ISBN of the book
     * @return The book for given ISBN
     */
    @ResponseBody
    @GetMapping("/{isbn}")
    public Book getBook(@PathVariable String isbn) {
        return library.findBookByISBN(isbn);
    }

    /**
     * Returns a list of books by a given author
     * @param author - The author of the book(s) in this Library
     * @return The list of books by the given author
     */
    @ResponseBody
    @GetMapping
    public List<Book> getBooksByAuthor(@RequestParam(name = "author", required = false) String author) {
        return library.findBooksByAuthor(author);
    }

    /**
     * Adds a new book to the library
     * @param newBook - The book to be added
     * @return The new book that is added
     */
    @ResponseBody
    @PostMapping
    public Book addBook(@RequestBody Book newBook) {
        return library.addBook(newBook);
    }

    /**
     * Removes a book from the library by ISBN
     * @param isbn - The ISBN of the book to be removed
     */
    @DeleteMapping("/{isbn}")
    public void removeBook(@PathVariable String isbn) {
        library.removeBook(isbn);
    }

    /**
     * Decreases the available copies of a book by 1 in this Library
     * @param isbn - The ISBN of the book to be borrowed
     * @return The existing book that is now borrowed
     */
    @ResponseBody
    @PutMapping("/borrow/{isbn}")
    public Book borrowBook(@PathVariable String isbn) {
        return library.borrowBook(isbn);
    }

    /**
     * Increases the available copies of a book by 1 in this Library
     * @param isbn - The ISBN of the book to be returned
     * @return The book that is now returned
     */
    @ResponseBody
    @PutMapping("/return/{isbn}")
    public Book returnBook(@PathVariable String isbn) {
        return library.returnBook(isbn);
    }
}
