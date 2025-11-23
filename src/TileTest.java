import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TileTest {
    private Tile a;
    private Tile d;
    private Tile b;
    private Tile f;
    private Tile k;
    private Tile j;
    private Tile q;
    private Tile blank;

    @Before
    public void setUp(){
        a = new Tile('A');
        d = new Tile('D');
        b = new Tile('B');
        f = new Tile('F');
        k = new Tile('K');
        j = new Tile('J');
        q = new Tile('Q');
        blank = new Tile(true);
    }

    @Test
    public void testTileInitializer(){
        Tile b = new Tile('B');
        assertNotNull("Tile should not be null", a);

    }

    @Test
    public void testGetValue(){
        Tile badTile = new Tile('1');
        assertEquals("A should be worth 1 point", 1, a.getValue());
        assertEquals("D should be worth 2 points", 2, d.getValue());
        assertEquals("B should be worth 3 points", 3, b.getValue());
        assertEquals("F should be worth 4 points", 4, f.getValue());
        assertEquals("K should be worth 5 points", 5, k.getValue());
        assertEquals("J should be worth 8 points", 8, j.getValue());
        assertEquals("Q should be worth 10 points", 10, q.getValue());
        assertEquals("badTile should be worth 0 points", 0, badTile.getValue());
        assertEquals("blank should be worth 0 points", 0, blank.getValue());
    }

    @Test
    public void testGetCharacter(){
        Tile badTile = new Tile('1');
        assertEquals("a's character should be 'A'", 'A', a.getCharacter());
        assertEquals("d's character should be 'D'", 'D', d.getCharacter());
        assertEquals("k's character should be 'k'", 'K', k.getCharacter());
        assertEquals("blank's character should be ' '", ' ', blank.getCharacter());

    }

    @Test
    public void testToString(){
        assertEquals("a's toString should be 'A(1)'", "A(1)", a.toString());
        assertEquals("d's toString should be 'D(1)'", "D(2)", d.toString());
        assertEquals("q's toString should be 'Q(10)'", "Q(10)", q.toString());
        assertEquals("blank's toString should be 'Blank(0)'", "BLANK(0)", blank.toString());

    }
}
