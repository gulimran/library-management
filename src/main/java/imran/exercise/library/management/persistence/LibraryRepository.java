package imran.exercise.library.management.persistence;

import imran.exercise.library.management.domain.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<BookEntity, String> {
}
