import java.util.*;
import java.util.Dictionary;

/**
 * AIPlayer is an AI player for the ScrabbleGame
 * @author Amber Skinner
 */
public class AIPlayer extends Player {

    /**
     * Constructor, no different from Player
     *
     * @param score player's score
     * @param name  player's name
     * @param index player's turn
     */
    public AIPlayer(int score, String name, int index) {
        super(score, name, index);
    }

    /**
     * Returns a list of every possible play the AI could make. The plays are not necessarily valid.
     * @param model The model of the ScrabbleGame
     * @return An ArrayList of every play the AI could make, valid or not.
     */
    public ArrayList<AIPlay> GetPossibleMoves(ScrabbleModel model)
    {
        // Get every tile on the board
        Board board = model.getBoard();
        // The list of all existing board letters that could be played around
        ArrayList<AIPlay> aiPlays = new ArrayList<>();
        for(int row = 0; row < 15; row++)
        {
            for (int col = 0; col < 15; col++)
            {
                if (board.getPosition(row, col) == null)
                    continue;
                // Add each existing tile to the list of plays (each is just one letter)
                aiPlays.add(new AIPlay(row, col, 1, String.valueOf(board.getPosition(row, col).getCharacter())));
                aiPlays.add(new AIPlay(row, col, 2, String.valueOf(board.getPosition(row, col).getCharacter())));
            }
        }
        // Makes it able to play in the center, though it is inelegant.
        if (aiPlays.isEmpty())
        {
            final String[] allLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
            for (String c : allLetters)
                aiPlays.add(new AIPlay(7,7,1,c));
        }
        // The list of all words that the AI could possibly play (for the most part)
        HashMap<Character, ArrayList<String>> candidateWords = GetCandidateWords(model);
        // Second list so more plays can be added without changing the first while it's being iterated over
        ArrayList<AIPlay> possibleMoves = new ArrayList<>();
        // For each tile on the board, generate possible plays from the list of possible words
        for (AIPlay play : aiPlays) {
            // For each word that could be formed using this board letter:
            for (String s : candidateWords.getOrDefault(play.word.toCharArray()[0], new ArrayList<>())) {
                // Create a play for the possible word
                AIPlay newPlay = new AIPlay(play.xPos, play.yPos, play.direction, s);
                // Find where the existing tile would be on the board
                int existingCharPos = s.indexOf(play.word.toCharArray()[0]);
                if (existingCharPos == -1)
                    continue;
                // Checking for if the board letter is used twice in the word. If so, there's 2 ways to play the
                // word that the existing tile would line up with, so we make another possible play
                if (s.lastIndexOf(play.word.toCharArray()[0]) != existingCharPos) {
                    AIPlay differentPosPlay = new AIPlay(play.xPos, play.yPos, play.direction, play.word);
                    int lastExistingCharPos = s.lastIndexOf(play.word.toCharArray()[0]);
                    // Move the starting position of the play so the existing tile lines up correctly
                    if (play.direction == 1) {
                        newPlay.xPos -= lastExistingCharPos;
                    } else {
                        newPlay.yPos -= lastExistingCharPos;
                    }
                    possibleMoves.add(differentPosPlay);
                }
                // Move the starting position of the play so the existing tile lines up correctly
                if (play.direction == 1) {
                    newPlay.xPos -= existingCharPos;
                } else {
                    newPlay.yPos -= existingCharPos;
                }
                possibleMoves.add(newPlay);
            }
        }
        return possibleMoves;
    }

    /**
     * A class modeling a play the AI player could make.
     */
    protected static class AIPlay
    {
        // The starting x position of the play. I think this is the row?
        public int xPos;
        // The starting y position of the play. I think this is the column?
        public int yPos;
        // The direction (1 is right, 2 is down, we should really make this an enum at some point)
        public int direction;
        // The word that could be played
        public String word;
        // The score this play would reward
        public int wordScore = 0;

        public AIPlay(int xPos, int yPos, int direction, String word)
        {
            this.xPos = xPos;
            this.yPos = yPos;
            this.direction = direction;
            this.word = word;
        }
    }

    /**
     * Returns a HashMap of almost possible words the AI could play with its held tiles and any additional letter
     * (which would exist on the board). The HashMap has a list of possible words for each (hypothetical) board letter.
     * @param model The model of the ScrabbleGame
     * @return A HashMap containing lists of words that could be played on a letter on the board (which is the key)
      */
    protected HashMap<Character, ArrayList<String>> GetCandidateWords(ScrabbleModel model)
    {
        // Number of letters we could fill in with either blanks or existing board tiles. Begins at 1 because we're
        // playing off of a board tile.
        int maxMissingLetters = 1;
        HashMap<Character, ArrayList<String>> candidateWords = new HashMap<>();
        HashMap<Character, Integer> baseLetterNum = new HashMap<>();
        for (Tile c : tiles)
        {
            if (c.isBlank())
            {
                maxMissingLetters++;
            }
            else
            {
                baseLetterNum.put(c.getCharacter(), baseLetterNum.getOrDefault(c.getCharacter(), 0) + 1);
            }
        }
        var dict = model.getDictionary();
        // due to the unfortunate naming of our dictionary class I have to call a static method on an object. hm.
        for (String word : dict.wordList())
        {
            word = word.toUpperCase();
            // make a new hashmap for char checking
            HashMap<Character, Integer> letterNum = new HashMap<>();
            for (Character c : baseLetterNum.keySet())
            {
                letterNum.put(c, baseLetterNum.get(c));
            }
            // check for number of each char
            for (char c : word.toCharArray())
            {
                letterNum.put(c, letterNum.getOrDefault(c, 0) - 1);
            }
            int missingSum = 0;
            for (int i : letterNum.values())
            {
                if (i < 0)
                    missingSum -= i;
            }
            // We are missing too many letters to play this word, so it's not a candidate word
            if (missingSum > maxMissingLetters)
                continue;
            // We can play this word if the missing letter exists on the board
            else if (missingSum == maxMissingLetters)
            {
                for (char c : letterNum.keySet())
                {
                    if (letterNum.get(c) < 0)
                    {
                        ArrayList<String> thisWordList = candidateWords.getOrDefault(c, new ArrayList<>());
                        if (thisWordList.isEmpty())
                        {
                            thisWordList.add(word);
                            candidateWords.put(c, thisWordList);
                        }
                        else
                        {
                            thisWordList.add(word);
                        }
                    }
                }
            }
            // We can play this word if any of its letters exist on the board
            else
            {
                for (char c : word.toCharArray())
                {
                    ArrayList<String> thisWordList = candidateWords.getOrDefault(c, new ArrayList<>());
                    if (thisWordList.isEmpty())
                    {
                        thisWordList.add(word);
                        candidateWords.put(c, thisWordList);
                    }
                    else
                    {
                        thisWordList.add(word);
                    }
                }
            }
        }
        return candidateWords;
    }
}
