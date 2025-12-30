import javax.swing.JFrame;
import java.awt.BorderLayout;

/**
 * Summative Game Window(Battleship) extends from JFrame
 *
 * @author Sophia Wu
 *
 * @version January 17, 2025
 */

public class GameWindow extends JFrame {
    /**
     * No argument-constructor that displays the game window
     */
    public GameWindow() {
        setTitle("Battleship");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add the game panel
        GamePanel gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // Starts the game
        gamePanel.startGame();
    }
}