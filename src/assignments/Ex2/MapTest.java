package assignments.Ex2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

class MapTest {

    private Map2D map3x3;
    private Map2D mapEmpty;

    private int[][] mat3x3 = {
            {0, 1, 0},
            {1, 0, 1},
            {0, 1, 0}
    };

    @BeforeEach
    void setup() {
        map3x3 = new Map(mat3x3);
        mapEmpty = new Map(5, 5, 0);
    }

    // ================= BASIC =================

    @Test
    void testInitFromMatrix() {
        Map2D m = new Map(mat3x3);
        assertEquals(3, m.getWidth());
        assertEquals(3, m.getHeight());
        assertEquals(1, m.getPixel(1, 0));
        assertEquals(0, m.getPixel(0, 0));
    }

    @Test
    void testInitSize() {
        Map2D m = new Map(10, 7, 3);
        assertEquals(10, m.getWidth());
        assertEquals(7, m.getHeight());
        assertEquals(3, m.getPixel(5, 5));
    }

    @Test
    void testSetAndGetPixel() {
        Pixel2D p = new Index2D(2, 2);
        mapEmpty.setPixel(p, 7);
        assertEquals(7, mapEmpty.getPixel(p));
    }

    @Test
    void testIsInside() {
        assertTrue(map3x3.isInside(new Index2D(0, 0)));
        assertTrue(map3x3.isInside(new Index2D(2, 2)));
        assertFalse(map3x3.isInside(new Index2D(-1, 0)));
        assertFalse(map3x3.isInside(new Index2D(3, 1)));
    }

    // ================= EQUALS =================

    @Test
    void testEqualsSameContent() {
        Map2D m1 = new Map(mat3x3);
        Map2D m2 = new Map(mat3x3);
        assertEquals(m1, m2);
    }

    @Test
    void testNotEqualsDifferentContent() {
        Map2D m1 = new Map(mat3x3);
        Map2D m2 = new Map(3, 3, 0);
        assertNotEquals(m1, m2);
    }

    // ================= FILL =================

    @Test
    void testFillSimple() {
        Pixel2D start = new Index2D(0, 0);
        mapEmpty.fill(start, 5, false);

        for (int x = 0; x < mapEmpty.getWidth(); x++) {
            for (int y = 0; y < mapEmpty.getHeight(); y++) {
                assertEquals(5, mapEmpty.getPixel(x, y));
            }
        }
    }

    @Test
    void testFillBlocked() {
        mapEmpty.setPixel(2, 2, -1);
        mapEmpty.fill(new Index2D(0, 0), 3, false);
        assertEquals(-1, mapEmpty.getPixel(2, 2));
    }

    // ================= SHAPES =================

    @Test
    void testDrawLine() {
        mapEmpty.drawLine(new Index2D(0, 0), new Index2D(4, 4), 9);
        assertEquals(9, mapEmpty.getPixel(0, 0));
        assertEquals(9, mapEmpty.getPixel(4, 4));
    }

    @Test
    void testDrawRect() {
        mapEmpty.drawRect(new Index2D(1, 1), new Index2D(3, 3), 7);
        assertEquals(7, mapEmpty.getPixel(1, 1));
        assertEquals(7, mapEmpty.getPixel(3, 3));
    }

    @Test
    void testDrawCircle() {
        mapEmpty.drawCircle(new Index2D(2, 2), 1.5, 8);
        assertEquals(8, mapEmpty.getPixel(2, 2));
    }

    // ================= SHORTEST PATH =================

    @Test
    void testShortestPathExists() {
        Map2D m = new Map(5, 5, 0);
        Pixel2D p1 = new Index2D(0, 0);
        Pixel2D p2 = new Index2D(4, 4);

        Pixel2D[] path = m.shortestPath(p1, p2, -1, false);
        assertNotNull(path);
        assertEquals(p1, path[0]);
        assertEquals(p2, path[path.length - 1]);
    }

    @Test
    void testShortestPathBlocked() {
        Map2D m = new Map(3, 3, 0);
        m.setPixel(1, 0, -1);
        m.setPixel(1, 1, -1);
        m.setPixel(1, 2, -1);

        Pixel2D[] path = m.shortestPath(
                new Index2D(0, 1),
                new Index2D(2, 1),
                -1,
                false
        );
        assertNull(path);
    }

    // ================= PERFORMANCE =================

    @Test
    @Timeout(value = 1, unit = SECONDS)
    void testLargeFillPerformance() {
        Map2D big = new Map(500, 500, 0);
        big.fill(new Index2D(0, 0), 1, false);
        assertEquals(1, big.getPixel(499, 499));
    }
}
