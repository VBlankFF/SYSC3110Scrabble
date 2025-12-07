import java.util.Objects;
import java.io.Serializable;
/**
 * Tile class for Scrabble game.
 * Represents a Tile.
 * @version 3.0
 */

public class Tile implements Serializable{
    private static final long serialVersionUID = 1L;

    private final char character;
    private final int value;
    private final boolean isBlank;
    private char representedLetter;



    public Tile(char character) {
        this.character = Character.toUpperCase(character);
        this.isBlank = false;
        this.representedLetter = this.character;
        this.value = getTileValue(character);

    }


    public Tile(boolean isBlank){
        if (isBlank){
            this.character = ' ';
            this.isBlank = true;
            this.representedLetter = ' ';
            this.value = 0;
        } else {
            this.character = ' ';
            this.isBlank = false;
            this.representedLetter = ' ';
            this.value = 0;
        }
    }

    /**
     * Gets the value of the tile
     * @param character the character of the tile
     * @return the value of the tile
     */

    private int getTileValue(char character) {
        character = Character.toUpperCase(character);
        if (character == 'A' || character == 'E' || character == 'I' || character == 'O' || character == 'U' || character == 'L' || character == 'N' || character == 'S' || character == 'T' || character == 'R') {
            return 1;
        } else if (character == 'D' || character == 'G') {
            return 2;
        } else if (character == 'B' || character == 'C' || character == 'M' || character == 'P') {
            return 3;
        } else if (character == 'F' || character == 'H' || character == 'V' || character == 'W' || character == 'Y') {
            return 4;
        } else if (character == 'K') {
            return 5;
        } else if (character == 'J' || character == 'X') {
            return 8;
        } else if (character == 'Q' || character == 'Z') {
            return 10;
        }
        return 0;
    }

    /**
     * gets character
     * @return character the character
     */

    public char getCharacter(){
        return character;
    }

    /**
     * Gets the value
     * @return value the value
     */
    public int getValue(){
        return value;
    }


    /**
     * Checks if it is a blank tile
     * @return if tile is blank or not
     */
    public boolean isBlank(){
        return isBlank;
    }

    /**
     * Get the letter the tile represents
     * @return the represented letter
     */
    public char getRepresentedLetter(){
        return representedLetter;
    }

    /**
     * Sets what letter the tile represents
     */
    public void setRepresentedLetter(char letter){
        if (isBlank){
            this.representedLetter = Character.toUpperCase(letter);
        }
    }

    /**
     * toString implementation for Tile class
     * @return the String implementation wanted.
     */
    public String toString(){
        if (isBlank){
            if (representedLetter == ' '){
                return "BLANK(0)";
            } else {
                return representedLetter + "(0)";
            }
        }
        return character + "(" + value + ")";
    }
}

