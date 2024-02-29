// Import statements for required libraries
package flappyBird;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

// Main class representing the Flappy Bird game
class FlappyBird implements ActionListener, MouseListener, KeyListener {
    static FlappyBird flappyBird;
    
    // Game window dimensions
    final int WIDTH = 1200, HEIGHT = 800; 
    
    // Game variables    
    int ticks1, ticks2, yMotionP1, yMotionP2, player1score, player2score, bestScore, distance = 600, speed = 10;
    JFrame jFrame = new JFrame();
    JButton startButton, replayButton, mainMenuButton, singlePlayerButton, multiPlayerButton;
    Renderer renderer;
    Random rand;
    Rectangle bird1, bird2;
    Boolean gameOver = false, gameStarted = false, paused = false, multiplayer = false, p1Died = false, p2Died = false;
    ArrayList<Rectangle> columns = new ArrayList<>();
    ArrayList<ImageIcon> pipes = new ArrayList<>();

    // Image icons for various game elements    
    // Replace paths to your saved images paths on your local device
    ImageIcon redBirdIcon = new ImageIcon("path_to_redBird.png"),
           yellowBirdIcon = new ImageIcon("path_to_yellowBird.png"),
           gameOverIcon = new ImageIcon("path_to_gameOver3.png"),
           headLine = new ImageIcon("path_to_headLine.png"),
           startButtonIcon = new ImageIcon("path_to_startButton.png"),
           singlePButtonIcon = new ImageIcon("path_to_singlePlayer.png"),
           multiPButtonIcon = new ImageIcon("path_to_multiPlayer.png"),
           pausedIcon = new ImageIcon("path_to_paused.png"),
           backgroundImg = new ImageIcon("path_to_background2.jpg");

    // BufferedImages for pipe images    
    BufferedImage buffImage, buffImage2;
    {
        try { // Replace paths to the saved images on your local device
            buffImage = ImageIO.read(new File("path_to_pipe.png"));
            buffImage2 = ImageIO.read(new File("path_to_skyPipe.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Main method to start the game
    public static void main (String[] args) {flappyBird = new FlappyBird();}
    
    // Constructor for FlappyBird class
    FlappyBird() {
        // Initialize game components        
        startButton = new JButton(startButtonIcon);
        startButton.setBounds(500, 480, 183, 79);
        startButton.setBackground(Color.BLACK);
        startButton.setBorder(BorderFactory.createEtchedBorder());
        startButton.addActionListener(this);

        // Initialize single-player components
        singlePlayerButton = new JButton(singlePButtonIcon);
        singlePlayerButton.setBounds(350, 489, 233, 60);
        singlePlayerButton.setVisible(false);
        singlePlayerButton.addActionListener(this);

        // Initialize multi-player components
        multiPlayerButton = new JButton(multiPButtonIcon);
        multiPlayerButton.setBounds(617, 489, 233, 60);
        multiPlayerButton.setVisible(false);
        multiPlayerButton.addActionListener(this);

        // Initialize replay button components
        replayButton = new JButton();
        replayButton.setBounds(535, 540, 50, 50);
        replayButton.setOpaque(false);
        replayButton.setContentAreaFilled(false);
        replayButton.setBorderPainted(false);
        replayButton.setFocusPainted(false);
        replayButton.addActionListener(this);

        // Initialize main-menu button components
        mainMenuButton = new JButton();
        mainMenuButton.setBounds(620, 540, 50, 50);
        mainMenuButton.setOpaque(false);
        mainMenuButton.setContentAreaFilled(false);
        mainMenuButton.setBorderPainted(false);
        mainMenuButton.setFocusPainted(false);
        mainMenuButton.addActionListener(this);

        // Set up the game window
        renderer = new Renderer();
        rand = new Random();
        Timer timer = new Timer(20, this);

        jFrame.setTitle("Flappy Bird");
        jFrame.add(startButton);
        jFrame.add(singlePlayerButton);
        jFrame.add(multiPlayerButton);
        jFrame.add(replayButton);
        jFrame.add(mainMenuButton);
        jFrame.add(renderer);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(WIDTH, HEIGHT);
        jFrame.addMouseListener(this);
        jFrame.addKeyListener(this);
        jFrame.setResizable(false);
        jFrame.setVisible(true);

        // Initialize bird and pipes
        bird1 = new Rectangle(WIDTH / 2 - 40, HEIGHT / 2 - 10, redBirdIcon.getIconWidth() - 27, redBirdIcon.getIconHeight() - 25);
        bird2 = new Rectangle(WIDTH / 2 - 120, HEIGHT / 2 - 3, yellowBirdIcon.getIconWidth() - 27, yellowBirdIcon.getIconHeight() - 25);

        addPipe(true);
        addPipe(true);
        addPipe(true);
        addPipe(true);

        // Start game timer
        timer.start();
    }

    // Method to add a pipe to the game
    void addPipe(Boolean start) {
        int space = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);

        Image groundPipeImage = buffImage.getScaledInstance(width, height, Image.SCALE_SMOOTH),
                skyPipeImage = buffImage2.getScaledInstance(width, HEIGHT - height - space, Image.SCALE_SMOOTH);
        ImageIcon groundPipe = new ImageIcon(groundPipeImage), skyPipe = new ImageIcon(skyPipeImage);
        JLabel jLabelG = new JLabel(), jLabelS = new JLabel();
        jLabelG.setIcon(groundPipe);
        jLabelS.setIcon(skyPipe);
        jFrame.add(jLabelG);
        jFrame.add(jLabelS);

        pipes.add(groundPipe);
        pipes.add(skyPipe);

        if (start) {
            columns.add(new Rectangle(WIDTH + width + columns.size() * space,HEIGHT - height - 65 , groundPipe.getIconWidth(), groundPipe.getIconHeight()));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * space, 0, skyPipe.getIconWidth(), skyPipe.getIconHeight() - 30));
        }
        else {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + distance, HEIGHT - height - 65, groundPipe.getIconWidth(), groundPipe.getIconHeight()));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, skyPipe.getIconWidth(), skyPipe.getIconHeight() - 30));
        }
    }

    // Method to paint a pipe on the game window
    void paintPipe(Graphics g, Rectangle column, ImageIcon pipe) {
        g.drawImage(pipe.getImage(), column.x, column.y, null);
    }

    // Method to handle jump for player 1
    void jumpP1() {
        if(gameOver) {
            if (!multiplayer)
                bird1 = new Rectangle(WIDTH / 2 - 40, HEIGHT / 2 - 10, redBirdIcon.getIconWidth() - 27, redBirdIcon.getIconHeight() - 25);

            columns.clear();
            pipes.clear();
            yMotionP1 = 0;
            player1score = 0;

            addPipe(true);
            addPipe(true);
            addPipe(true);
            addPipe(true);

            if (multiplayer) {
                bird1 = new Rectangle(WIDTH / 2 - 40, HEIGHT / 2 - 10, redBirdIcon.getIconWidth() - 27, redBirdIcon.getIconHeight() - 25);
                bird2 = new Rectangle(WIDTH / 2 - 120, HEIGHT / 2 - 3, yellowBirdIcon.getIconWidth() - 27, yellowBirdIcon.getIconHeight() - 25);
                yMotionP2 = 0;
                player2score = 0;
                p1Died = false;
                p2Died = false;
            }
            gameOver = false;
        }

        else {
            if (!paused) {
                if (yMotionP1 > 0)
                    yMotionP1 = 0;
                yMotionP1 -= 10;
            }
        }
    }

    // Method to handle jump for player 2
    void jumpP2 () {
        if (!paused) {
            if(yMotionP2 > 0)
                yMotionP2 = 0;
            yMotionP2 -= 10;
        }
    }

    // ActionListener method for handling game events
    @Override
    public void actionPerformed(ActionEvent e) {
        ticks1++;
        if (multiplayer)
            ticks2++;

        replayButton.setVisible(false);
        mainMenuButton.setVisible(false);

        if (!gameStarted) {
            if (e.getSource() == startButton) {
                startButton.setVisible(false);
                singlePlayerButton.setVisible(true);
                multiPlayerButton.setVisible(true);
            }
            if (e.getSource() == multiPlayerButton) {
                multiplayer = true;
                gameStarted = true;
            }
            if (e.getSource() == singlePlayerButton)
                gameStarted = true;
        }

        if (gameStarted && !paused) {
            singlePlayerButton.setVisible(false);
            multiPlayerButton.setVisible(false);
            jFrame.requestFocusInWindow();

            for (Rectangle column : columns) {column.x -= speed;}

            if (ticks1 % 2 == 0 && yMotionP1 < 15)
                yMotionP1 += 2;
            if (ticks2 % 2 == 0 && yMotionP2 < 15 && multiplayer)
                yMotionP2 += 2;

            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);
                ImageIcon pipe = pipes.get(i);
                if (column.x + column.width < 0) {
                    columns.remove(column);
                    pipes.remove(pipe);
                    if (column.y == 0)
                        addPipe(false);
                }
            }

            bird1.y += yMotionP1;
            birdSituations(bird1, 1);
            if (multiplayer) {
                bird2.y += yMotionP2;
                birdSituations(bird2, 2);
            }

            if (gameOver) {
                replayButton.setVisible(true);
                mainMenuButton.setVisible(true);
            }
            if (e.getSource() == replayButton && gameOver)
                jumpP1();
            if (e.getSource() == mainMenuButton && gameOver) {
                gameStarted = false;
                startButton.setVisible(true);
                jumpP1();
                multiplayer = false;
            }

            renderer.repaint();
        }
    }

    // Method to handle bird interactions with pipes
    void birdSituations(Rectangle bird, int i) {
        for (Rectangle column : columns) {
            if (bird.x == column.x + column.width && !gameOver && column.y == 0) {
                if(i == 1) {
                    player1score++;
                    if (bestScore < player1score)
                        bestScore = player1score;
                }
                else {
                    player2score++;
                    if (bestScore < player2score)
                        bestScore = player2score;
                }
            }
            if (column.intersects(bird)) {
                isGameEnded(i);

                if (bird.x <= column.x)
                    bird.x = column.x - bird.width;
                else {
                    if (column.y != 0)
                        bird.y = column.y - bird.height;
                    else if(bird.y < column.height)
                        bird.y = column.height;
                }
            }
        }

        if (bird.y >= HEIGHT - bird.height - 75 || bird.y < 0)
            isGameEnded(i);
        if (bird.y + yMotionP1 >= HEIGHT - 75 - bird.height && i == 1)
            bird.y = HEIGHT - 75 - bird.height;
        if (bird.y + yMotionP2 >= HEIGHT - 75 - bird.height && i == 2)
            bird.y = HEIGHT - 75 - bird.height;
    }

    // Method to determine if the game has ended
    void isGameEnded(int i) {
        if (!multiplayer)
            gameOver = true;
        if (multiplayer) {
            switch (i) {
                case 1 -> p1Died = true;
                case 2 -> p2Died = true;
            }
            if (p1Died && p2Died)
                gameOver = true;
        }
    }

    // Method to repaint the game window
    void repaint(Graphics g) {
        g.drawImage(backgroundImg.getImage(), 0, 0, WIDTH, HEIGHT, null);
        g.drawImage(redBirdIcon.getImage(), bird1.x, bird1.y, null); //**
        if (multiplayer)
            g.drawImage(yellowBirdIcon.getImage(), bird2.x, bird2.y, null);

        for (int i = 0; i < columns.size(); i++)
            paintPipe(g, columns.get(i), pipes.get(i));

        if (!gameStarted)
            g.drawImage(headLine.getImage(), 150, 50, null);
        if (paused)
            g.drawImage(pausedIcon.getImage(), 250, 150, null);
        if (gameOver)
            g.drawImage(gameOverIcon.getImage(), 301, 40, null);

        g.setColor(Color.white);
        if (gameStarted) {
            if (!multiplayer) {
                g.setFont(new Font("Arial", Font.BOLD, 100));
                g.drawString(String.valueOf(player1score), WIDTH / 2 - 25, 100);
            }
            else {
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("P1 Score: "+player1score, 10, 100);
                g.drawString("P2 Score: "+player2score, 10, 160);
            }
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Best Score: "+bestScore, 10, 40);
        }
    }

    // MouseListener method triggered when a mouse button is pressed
    @Override
    public void mousePressed(MouseEvent e) {
        if (gameStarted && !gameOver && !p1Died)
            jumpP1();
    }

    // KeyListener method triggered when a key is pressed
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && gameStarted && !gameOver) {
            if (!multiplayer)
                jumpP1();
            else if(!p2Died)
                jumpP2();
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameStarted && !gameOver)
            paused = !paused;
    }

    // Unused MouseListener methods
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    // Unused KeyListener methods
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
