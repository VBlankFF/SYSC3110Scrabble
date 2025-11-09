import java.util.Objects;

public class Tile {
    private final char character;
    private final int value;


    public Tile(char character) {
        this.character = Character.toUpperCase(character);
        this.value = getTileValue(character);

    }

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

    public char getCharacter(){
        return character;
    }

    public int getValue(){
        return value;
    }

    public String toString(){
        return character + "(" + value + ")";
    }

}

