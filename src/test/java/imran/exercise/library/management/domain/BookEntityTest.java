package imran.exercise.library.management.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BookEntityTest {

    private static final BookEntity book1 = new BookEntity("isbn", "title", "author", 2024, 1);

    @Test
    void testEquals_WhenBookEntityObjects_AreEqual() {
        BookEntity book2 = new BookEntity("isbn", "title", "author", 2024, 1);
        assertEquals(book1, book2, "BookEntity with same data should be equal");
    }

    @Test
    void testEquals_WhenSomeDataInBookEntityObjects_IsDifferent() {
        BookEntity book2 = new BookEntity("isbn", "title2", "author", 2025, 1);
        assertNotEquals(book1, book2, "BookEntity with different data should not be equal");
    }

    @Test
    void testEquals_WhenBookEntityObject_IsNotEqualToNull() {
        assertNotEquals(book1, null, "BookEntity should not be equal to null");
    }

    @Test
    void testEquals_WhenBookEntityObject_IsNotEqualToDifferentClass() {
        assertNotEquals(book1, new Object(), "BookEntity should not be equal to a different class object");
    }

    @Test
    void testHasCode_WhenBookEntityObjects_AreEqual() {
        BookEntity book2 = new BookEntity("isbn", "title", "author", 2024, 1);
        assertEquals(book1.hashCode(), book2.hashCode(), "BookEntity with same data should have same hashcode");
    }

    @Test
    void testHasCode_WhenSomeDataInBookEntityObjects_IsDifferent() {
        BookEntity book2 = new BookEntity("isbn", "title2", "author", 2025, 1);
        assertNotEquals(book1.hashCode(), book2.hashCode(), "BookEntity with different data should have different hashcode");
    }
}
