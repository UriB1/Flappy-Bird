package flappyBird;
import javax.swing.*;
import java.awt.*;
import java.io.Serial;

class Renderer extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (FlappyBird.flappyBird != null) {
            FlappyBird.flappyBird.repaint(g);
        }
    }
}