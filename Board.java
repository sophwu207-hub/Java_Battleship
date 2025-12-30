import java.awt.Graphics;
import java.awt.Color;
import java.util.Random;

/**
 * Summative Game Board Class(Battleship)
 *
 * @author Sophia Wu
 *
 * @version January 17, 2025
 */

public class Board {
    // Design Decision: I will represent the Board using an array of char(acters) rather than Strings.
    private char[][] board;
    // Instance variable for the number of ship parts left.
    private int ShipsLeft;

    private Color color;

    /**
     * This no-argument constructor will create a new 2D array filled with '' empty characters.
     * This method has no parameters and returns nothing.
     */
    public Board() {
        board = new char[7][7];
        // Fill the board with blank spaces
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                board[row][col] = ' ';
            }
        }
    }

    /**
     * This method clears the board.
     */
    public void clearBoard() {
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                board[row][col] = ' ';
            }
        }
        ShipsLeft = -1;
    }

    /**
     * Accessor method for number of ship parts left.
     * @return number of ship parts left
     */
    public int getShipsLeft() {
        return ShipsLeft;
    }

    /**
     * Mutator method for number of ship parts left. Subtracts one from the instance variable when called.
     * This method has no parameters and returns nothing.
     */
    public void setShipsLeft() {
        ShipsLeft -= 1;
    }

    /**
     * This method helps hide the computer ships
     *
     * @param board The Board object for our game
     * @param rand A Random object to get random numbers
     */
    public void hideShips(Board board, Random rand) {
        ShipsLeft = 12;
        int row, col;
        char direction;
        int[] shipLengths = {2, 3, 3, 4};

        // Chooses ship placement with random number generation
        for (int length : shipLengths) {
            do {
                if ((int) (Math.random() * 2) == 0) {
                    direction = 'h';
                    row = rand.nextInt(7);
                    col = rand.nextInt(7 - length); // Ensure the ship fits horizontally
                } else {
                    direction = 'v';
                    row = rand.nextInt(7 - length); // Ensure the ship fits vertically
                    col = rand.nextInt(7);
                }
                // Loop terminates only when ship is placed without overlapping
            } while (!board.confirmPosition(row, col, direction, length));
        }
    }

    /**
     * Helper method to confirm the validity of the position of the ship parts before placing.
     * It checks if the ship parts can be placed without overlapping existing ships.
     * If so, it places the parts down, and returns true.
     * Otherwise, it does not change the board and returns false.
     *
     * @param row The row to place the ship
     * @param col The column to place the ship
     * @param direction The orientation of the ship(i.e h for horizontal and v for vertical)
     * @param length The length of the ship being placed
     *
     * @return true if the locations were valid and empty; (i.e., ship was placed) or false if the place was invalid.
     */
    private boolean confirmPosition(int row, int col, char direction, int length) {
        if (direction == 'h') {
            // Returns false a spot is already occupied(horizontal check)
            for (int parts = 0; parts < length; parts++) {
                if ((board[row][col + parts] != ' ')) {
                    return false;
                }
            }
            // Places ship parts vertically otherwise
            for (int parts = 0; parts < length; parts++) {
                board[row][col + parts] = 'S';
            }
        }
        else {
            // Returns false a spot is already occupied(vertical check)
            for (int parts = 0; parts < length; parts++) {
                if ((board[row + parts][col] != ' ')) {
                    return false;
                }
            }
            // Places ship parts horizontally otherwise
            for (int parts = 0; parts < length; parts++) {
                board[row + parts][col] = 'S';
            }
        }
        // Returns true if placed without issue
        return true;
    }

    /**
     * This method checks if the guessed location has a ship.
     * It returns the character found in the location specified.
     *
     * @param row The row guessed
     * @param col The column guessed
     * @return what is stored at the specified row and col position
     */
    public char isHit(int row, int col) {
        return board[row][col];
    }

    /**
     * Used to change the character of a grid spot after a guess
     *
     * @param row The row to change
     * @param col The column to change
     * @param set The character to set it to based on hit or miss
     */
    public void setGrid (int row, int col, char set) {
        board[row][col] = set;
    }

    /**
     * Draws and updates the visual representation of the board.
     *
     * @param g Graphics object
     * @param xOffset how far to offset the x coordinates from the left of the screen
     */
    public void drawGrid(Graphics g, int xOffset) {
        // Sets color to black for grid lines
        g.setColor(Color.BLACK);
        for (int count = 0; count <= 7; count++) {
            // Draw horizontal lines
            g.drawLine(xOffset, 40 + count * 60, xOffset + 420, 40 + count * 60);
            // Draw vertical lines
            g.drawLine(xOffset + count * 60, 40, xOffset + count * 60, 40 + 420);
        }

        // Draws squares of specific colour on the grid based on hit or miss.
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                char cell = board[row][col];

                if (cell == ' ') {
                    color = new Color(34, 82, 160);
                    g.setColor(color);
                    g.fillRect(xOffset + col * 60 + 1, 41 + row * 60, 59, 59);
                } else if (cell == '!') {
                    color = new Color(163, 19, 19);
                    g.setColor(color);
                    g.fillRect(xOffset + col * 60 + 1, 41 + row * 60, 59, 59);

                } else if (cell == 'X') {
                    color = new Color(99, 99, 99, 255);
                    g.setColor(Color.GRAY);
                    g.fillRect(xOffset + col * 60 + 1, 41 + row * 60 , 59, 59);
                }
            }
        }
    }

    /**
     * Standard toString method for testing purposes.
     *
     * @return String representation of the current board
     */
    public String toString() {
        return "   0   1   2   3   4   5   6\n" +
                "0: " + board[0][0] + " | " + board[0][1] + " | " + board[0][2] + " | " + board[0][3] + " | " + board[0][4] + " | " + board[0][5] + " | " + board[0][6] + " | " +"\n  ---+---+---+---+---+---+---\n" +
                "1: " + board[1][0] + " | " + board[1][1] + " | " + board[1][2] + " | " + board[1][3] + " | " + board[1][4] + " | " + board[1][5] + " | " + board[1][6] + " | " +"\n  ---+---+---+---+---+---+---\n" +
                "2: " + board[2][0] + " | " + board[2][1] + " | " + board[2][2] + " | " + board[2][3] + " | " + board[2][4] + " | " + board[2][5] + " | " + board[2][6] + " | " +"\n  ---+---+---+---+---+---+---\n" +
                "3: " + board[3][0] + " | " + board[3][1] + " | " + board[3][2] + " | " + board[3][3] + " | " + board[3][4] + " | " + board[3][5] + " | " + board[3][6] + " | " +"\n  ---+---+---+---+---+---+---\n" +
                "4: " + board[4][0] + " | " + board[4][1] + " | " + board[4][2] + " | " + board[4][3] + " | " + board[4][4] + " | " + board[4][5] + " | " + board[4][6] + " | " +"\n  ---+---+---+---+---+---+---\n" +
                "5: " + board[5][0] + " | " + board[5][1] + " | " + board[5][2] + " | " + board[5][3] + " | " + board[5][4] + " | " + board[5][5] + " | " + board[5][6] + " | " +"\n  ---+---+---+---+---+---+---\n" +
                "6: " + board[6][0] + " | " + board[6][1] + " | " + board[6][2] + " | " + board[6][3] + " | " + board[6][4] + " | " + board[6][5] + " | " + board[6][6] + " | " +"\n  ---+---+---+---+---+---+---\n";
    }
}