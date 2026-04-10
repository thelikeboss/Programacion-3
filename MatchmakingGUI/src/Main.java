package src;

public class Main {
    public static void main(String[] args) {
        // Aquí iniciará la GUI principal
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MatchmakingGUI();
        });
    }
}
