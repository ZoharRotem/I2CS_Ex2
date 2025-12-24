package test.java;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import assignments.Ex2.Index2D;

public class Index2DTest {

    @Test
    public void testCtorAndGetters() {
        Index2D p = new Index2D(5, 7);
        assertEquals(5, p.getX());
        assertEquals(7, p.getY());
    }

    @Test
    public void testCopyCtor() {
        Index2D p1 = new Index2D(3, 4);
        Index2D p2 = new Index2D(p1);
        assertEquals(3, p2.getX());
        assertEquals(4, p2.getY());
        assertNotSame(p1, p2); // לוודא שזה לא אותו אובייקט
    }

    @Test
    public void testToString() {
        Index2D p = new Index2D(1, 2);
        assertEquals("(1, 2)", p.toString());
    }

    @Test
    public void testDistance() {
        Index2D p1 = new Index2D(0, 0);
        Index2D p2 = new Index2D(3, 4);
        assertEquals(5.0, p1.distance2D(p2), 0.0001); // מרחק פיתגורס
    }

    @Test
    public void testEqualsSameObject() {
        Index2D p = new Index2D(2, 3);
        assertTrue(p.equals(p));
    }

    @Test
    public void testEqualsNull() {
        Index2D p = new Index2D(2, 3);
        assertFalse(p.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() {
        Index2D p = new Index2D(2, 3);
        assertFalse(p.equals("hello"));
    }

    @Test
    public void testEqualsDifferentValues() {
        Index2D p1 = new Index2D(1, 2);
        Index2D p2 = new Index2D(1, 3);
        Index2D p3 = new Index2D(2, 3);

        assertFalse(p1.equals(p2));
        assertFalse(p1.equals(p3));
    }

    @Test
    public void testEqualsSameValues() {
        Index2D p1 = new Index2D(7, 9);
        Index2D p2 = new Index2D(7, 9);
        assertTrue(p1.equals(p2));
    }
}
