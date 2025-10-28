import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DictionaryTest {
    private Dictionary dictionary;

    @Before
    public void setUp(){
        dictionary = new Dictionary();
    }

    @Test
    public void testDictionaryInitializer(){
        assertNotNull("Dictionary should not be null", dictionary);
        assertEquals("Dictionary should have 10000 words", 10000, dictionary.getSize());
    }

    @Test
    public void testUppercaseAndLowercase(){
        assertTrue("'abroad' should be a valid word", dictionary.validWord("abroad"));
        assertTrue("'accordance' should be a valid word", dictionary.validWord("accordance"));
        assertTrue("'AFFAIR' should be a valid word", dictionary.validWord("AFFAIR"));
        assertTrue("'AUBURN' should be a valid word", dictionary.validWord("AUBURN"));
        assertTrue("'bOn' should be a valid word", dictionary.validWord("bOn"));
        assertTrue("'boOkSToRe' should be a valid word", dictionary.validWord("boOkSToRe"));
    }

    @Test
    public void testNotValidWord(){
        assertFalse("'Pedri' should not be a valid word", dictionary.validWord("Pedri"));
        assertFalse("'123' should not be a valid word", dictionary.validWord("123"));
        assertFalse("'!/' should not be a valid word", dictionary.validWord("!/"));
        assertFalse("'' should not be a valid word", dictionary.validWord(""));
        assertFalse("'word with spaces' should not be a valid word", dictionary.validWord("word with spaces"));

    }

    @Test
    public void testWordList(){
        List<String> words = Dictionary.wordList();
        assertNotNull("Word list should not be null", words);
        assertEquals("Word list should have 10000 words", 10000, words.size());



    }


}
