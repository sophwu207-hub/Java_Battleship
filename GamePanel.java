import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Summative Game Panel(Battleship) extends from JPanel
 *
 * @author Sophia Wu
 *
 * @version January 17, 2025
 */

public class GamePanel extends JPanel {

    Random rand = new Random();
    Board computerBoard = new Board();
    Board playerBoard = new Board();

    // Instantiates String for leaderboard pop up, and ArrayList for sorting scores
    String leaderboard;
    ArrayList<Integer> scores = new ArrayList<>();

    private Timer gameTimer;

    // Instantiates string objects for username input and name of top scorer
    String name;
    String topScorer;

    int playerGuessCounter = 0;
    int computerGuessCounter = 0;
    int playerScore;

    JLabel scoreLabel = new JLabel("Score " + playerScore);
    JButton restart = new JButton("Restart");

    private Color color = new Color(34, 82, 160);

    /**
     * Constructor for the GamePanel class.
     * Initializes the game panel, mouse listener, and game timer.
     */
    public GamePanel() {
        setBackground(color);

        // Add mouse listener for interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });

        // Initialize the game timer that decreases the score over time and checks game state
        gameTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerScore--;
                checkGameState();
            }
        });

        // Set up the restart button so it resets the game when clicked
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
    }

    /**
     * Starts the game by prompting for the player's name and setting up the boards.
     */
    public void startGame() {
        // JOptionPane for user to input name
        name = JOptionPane.showInputDialog("Enter name for leaderboard: ");

        // Hide ships
        computerBoard.hideShips(computerBoard, rand);
        playerBoard.hideShips(playerBoard, rand);

        // Add score JLabel and restart JButton
        add(scoreLabel);
        add(restart);

        // Start the game timer
        gameTimer.start();

        // Print out board toString()s for testing purposes
        System.out.println(computerBoard.toString());
        System.out.println(playerBoard.toString());
    }

    /**
     * Resets the game to its initial state, including clearing boards and resetting scores.
     */
    public void resetGame() {
        // Reset all necessary game state variables
        playerScore = 0;
        playerGuessCounter = 0;
        computerGuessCounter = 0;
        leaderboard = "Leaderboard:\n";

        // Clear the boards
        playerBoard.clearBoard();
        computerBoard.clearBoard();

        // Hide ships again
        computerBoard.hideShips(computerBoard, rand);
        playerBoard.hideShips(playerBoard, rand);

        // Restart the game
        gameTimer.start();
        repaint();
    }


     // Determines whether game is over, makes computer guess if user has made more
     // guesses than computer, adds new scores to score file(file input), and repaints
     // the board. This runs every 500ms due to the timer.
    private void checkGameState() {
        // If the game is over, stop the timer
        if (playerBoard.getShipsLeft() == 0 || computerBoard.getShipsLeft() == 0) {
            scoreLabel.setText("Score: " + playerScore);
            gameTimer.stop();

            // Save new score to file
            try {
                FileWriter appendFile = new FileWriter("scores.txt", true);
                PrintWriter fileOutput = new PrintWriter(appendFile);
                fileOutput.println(playerScore);
                fileOutput.close();
            } catch (IOException ioException) {
                System.err.println("Java Exception: " + ioException);
                System.out.println("Sorry, error with output file scores.txt.");
            }
            showLeaderboard();
            return;
        }
        // Updates scoreLabel with most recent score
        scoreLabel.setText("Score: " + playerScore);

        // Makes computer guess if user has had more turns
        if (playerGuessCounter > computerGuessCounter) {
            makeComputerGuess();
            // Calls the paintComponent method to redraw the display
            repaint();
        }
    }

    /**
     * Handles the computer's guess, randomly selecting a position and updating the board.
     */
    public void makeComputerGuess() {
        int row, col;
        // Generates random computer row and column guesses until they're valid
        do {
            row = rand.nextInt(7);
            col = rand.nextInt(7);
        } while (playerBoard.isHit(row, col) == '!' || playerBoard.isHit(row, col) == 'X');  // Ensure it's not already guessed

        // Adds to computer guess counter for comparison to player guess counter
        computerGuessCounter++;

        // Checks if shot is hit or miss and plays sound if hit
        if (playerBoard.isHit(row, col) == 'S') {
            playSound("resources/crash_x.wav");
            playerBoard.setGrid(row, col, '!');
            playerBoard.setShipsLeft();
        }
        else if (playerBoard.isHit(row, col) == ' ') {
            playerBoard.setGrid(row, col, 'X');
        }
    }


    /**
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the player grid
        playerBoard.drawGrid(g, 10);

        // Draw the computer grid
        computerBoard.drawGrid(g, 470);
    }

    /**
     * Handles mouse clicks on the game board. Makes a guess if the click is on the computer's grid.
     * @param e The MouseEvent containing the click position.
     */
    public void handleMouseClick(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Check if the click is on the computer's grid
        if (x > 470 && x < 890) {
            int row = (y - 40) / 60;
            int col = (x - 470) / 60;

            // Checks if shot is hit or miss and plays sounds, changes board, and adjusts scores accordingly
            if (row >= 0 && row < 7) {
                if (computerBoard.isHit(row, col) == 'S') {
                    playSound("resources/explosion_x.wav");
                    computerBoard.setGrid(row, col, '!');
                    computerBoard.setShipsLeft();
                    playerScore += 100;
                    playerGuessCounter++;
                } else if (computerBoard.isHit(row, col) == ' ') {
                    playSound("resources/splash2.wav");
                    computerBoard.setGrid(row, col, 'X');
                    playerScore -= 5;
                    playerGuessCounter++;
                }
            }
            repaint();
        }
    }

     // Reads the scores from the file, sorts them, and updates the leaderboard.
     // Also checks if the player's score is the top score.
    private void showLeaderboard() {
        // Sets and resets leaderboard String for scores to be added to
        leaderboard = "Leaderboard:\n";
        // Clear any previous scores from ArrayList, since they'll be read from file again
        scores.clear();

        try {
            // Read scores from the file and add them to the ArrayList
            Scanner fileInput = new Scanner(new File("scores.txt"));
            while (fileInput.hasNext()) {
                scores.add(Integer.parseInt(fileInput.nextLine()));
            }
            fileInput.close();
        } catch (IOException ioException) {
            System.err.println("Java Exception: " + ioException);
        }

        // Sort the scores in descending order
        scores.sort(Collections.reverseOrder());

        // If the player's score is the highest, save the name as the top scorer
        if (playerScore == scores.get(0)) {
            try {
                PrintWriter fileOutput = new PrintWriter("topScorer.txt");
                fileOutput.println(name);
                fileOutput.close();
            } catch (IOException ioException) {
                System.err.println("Java Exception: " + ioException);
                System.out.println("Sorry, error with output file scores.txt.");
            }
        }

        // Read the top scorer's name from the file
        try {
            Scanner fileInput = new Scanner(new File("topScorer.txt"));
            topScorer = fileInput.nextLine();
            fileInput.close();
        } catch (IOException ioException) {
            System.err.println("Java Exception: " + ioException);
        }

        // Add the sorted scores with ranking numbers
        for (int count = 0; count < scores.size(); count++) {
            leaderboard += (count + 1) + ". " + scores.get(count) + "\n";
        }

        // Prints the list of scores in order with the top scorer's name in the header
        JOptionPane.showMessageDialog(null, leaderboard, "Top score held by " + topScorer, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Plays a sound effect from the specified file path.
     * @param soundName The file path and the sound file.
     */
    public void playSound(String soundName) {
        try {
            // Creates new File object with provided file parameter
            File soundFile = new File(soundName);
            // Open an audio input stream from the specified file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            // Obtain a clip instance from the system's audio
            Clip clip = AudioSystem.getClip();
            // Open clip with the audio input stream
            clip.open(audioStream);
            // Start playing audio clip
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Unable to play sound.");
        }
    }
}