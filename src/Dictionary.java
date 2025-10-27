import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Dictionary {
    private List<String> validWords;

    public Dictionary() {
        validWords = new ArrayList<>();
        validWords = wordList();

    }

    public static List<String> wordList() {
        List<String> lines = Collections.emptyList();

        try {
            lines = Files.readAllLines(
                    Paths.get("scrabbleWords.txt"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;

    }

    public boolean validWord(String word) {
        return validWords.contains(word.toLowerCase());
    }

}