# Milestone 3 SYSC3110 Scrabble Project
The goal of this project is to reproduce a simplified version of the classic board game Scrabble. 


## Features
During the first milestone, the project should implement a text-based playable version of the game where player can
use the keyboard to input their input on the console during each turn. Each player should be able to view their drawn letters, place words following the rules, pass their turn, and view the updated game board in text form. The deliverables includes the code, UML diagrams, and documentations.

During milestone 2, the project should implement a GUI version of the game instead of a text-based one. The deliverables includes the code, UML diagrams, and documentations.

During milestone 3, blank tiles and premium squares were added to the game. Also, users can now play against AI players. The deliverables includes the code, UML diagrams, and documentations.

## Project Division
Here is how the work was divided:

- Emmanuel Konate: Board class, TileBag class, UMLs, Data Structure Explanation
- Aymen Zebentout: Player class, ReadMe
- Joseph Dereje: Tile class, Dictionary class, Sequence Diagrams
- Amber: ScrabbleGame class, AIPlayer class

## AI Strategy
The strategy of the AI is as follows:
- Find almost every playable move (The move calculating algorithm does not find every move, but it's reasonably close)
- Calculate the score and validity of each move
- Play the valid move with the highest score
- If there is no valid moves, swap all non-blank tiles
- If swapping is impossible, pass turn
