package code;

/**
 * Main class: Entry point for the 2048 AI project.
 * It launches the GameFrame GUI on the Swing Event Dispatch Thread.
 */
public class Main {

    /**
     * Main method: Launches the game GUI in a thread-safe way using SwingUtilities.invokeLater.
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Create and display the game frame
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
