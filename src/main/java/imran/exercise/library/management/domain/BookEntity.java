package imran.exercise.library.management.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class BookEntity {

    @Id
    private String isbn;
    private String title;
    private String author;
    private Integer publicationYear;
    private Integer availableCopies;

    public BookEntity() {
    }

    public BookEntity(String isbn, String title, String author, Integer publicationYear, Integer availableCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.availableCopies = availableCopies;
    }

    public String isbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String author() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer publicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Integer availableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEntity that = (BookEntity) o;
        return Objects.equals(isbn, that.isbn) && Objects.equals(title, that.title) && Objects.equals(author, that.author) && Objects.equals(publicationYear, that.publicationYear) && Objects.equals(availableCopies, that.availableCopies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, title, author, publicationYear, availableCopies);
    }
}
