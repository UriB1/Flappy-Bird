package flappyBird;
import javax.swing.*;
import java.awt.*;
import java.io.Serial;

// The Renderer class extends JPanel and is responsible for rendering the game graphics.
class Renderer extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    // Overrides the paintComponent method of JPanel to customize the rendering behavior.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (FlappyBird.flappyBird != null) {
            // Call the repaint method of the FlappyBird instance, passing the Graphics object
            FlappyBird.flappyBird.repaint(g);
        }
    }
}
