import java.util.*;
import java.util.Dictionary;

public class AIPlayer extends Player {

    /**
     * Constructor
     *
     * @param score player's score
     * @param name  player's name
     * @param index player's turn
     */
    public AIPlayer(int score, String name, int index) {
        super(score, name, index);
    }

    public ArrayList<AIPlay> GetPossibleMoves(ScrabbleModel model)
    {
        Board board = model.getBoard();
        ArrayList<AIPlay> aiPlays = new ArrayList<>();
        for(int row = 0; row < 15; row++)
        {
            for (int col = 0; col < 15; col++)
            {
                if (board.getPosition(row, col) == null)
                    continue;
                aiPlays.add(new AIPlay(row, col, 1, String.valueOf(board.getPosition(row, col).getCharacter())));
                aiPlays.add(new AIPlay(row, col, 2, String.valueOf(board.getPosition(row, col).getCharacter())));
            }
        }
        HashMap<Character, ArrayList<String>> candidateWords = GetCandidateWords(model);
        // second list so more plays can be added without changing the first while it's being iterated over
        ArrayList<AIPlay> possibleMoves = new ArrayList<>();
        for (AIPlay play : aiPlays) {
            for (String s : candidateWords.getOrDefault(play.word.toCharArray()[0], new ArrayList<>())) {
                AIPlay newPlay = new AIPlay(play.xPos, play.yPos, play.direction, s);
                int existingCharPos = s.indexOf(play.word.toCharArray()[0]);
                if (existingCharPos == -1)
                    continue;
                if (s.lastIndexOf(play.word.toCharArray()[0]) != existingCharPos) {
                    AIPlay differentPosPlay = new AIPlay(play.xPos, play.yPos, play.direction, play.word);
                    int lastExistingCharPos = s.lastIndexOf(play.word.toCharArray()[0]);
                    if (play.direction == 1) {
                        newPlay.xPos -= lastExistingCharPos;
                    } else {
                        newPlay.yPos -= lastExistingCharPos;
                    }
                    possibleMoves.add(differentPosPlay);
                }
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
    protected static class AIPlay
    {
        public int xPos;
        public int yPos;
        public int direction;
        public String word;
        public int wordScore = 0;
        public AIPlay(int xPos, int yPos, int direction, String word)
        {
            this.xPos = xPos;
            this.yPos = yPos;
            this.direction = direction;
            this.word = word;
        }
    }
    // Returns a HashMap of all possible words the
    protected HashMap<Character, ArrayList<String>> GetCandidateWords(ScrabbleModel model)
    {
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
