import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyWiwa extends JPanel implements ActionListener, KeyListener {
    // setting screen size
    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg;
    Image wiwaImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // setting size and position for wiwa
    int wiwaX = boardWidth / 8;
    int wiwaY = boardHeight / 2;
    int wiwaWidth = 80;
    int wiwaHeight = 55;

    // wiwa class to create wiwa
    class Wiwa {
        int x = wiwaX;
        int y = wiwaY;
        int width = wiwaWidth;
        int height = wiwaHeight;
        Image img;

        Wiwa(Image img) {
            this.img = img;
        }
    }

    // pipe class to create pipe
    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // game logic
    Wiwa wiwa;
    int velocityY = 0; // moving up, toward 0,0 so y will be negative, and vice versa (wiwa will ony
                       // move up and down)
    int velocityX = -5; // pipes moving to left (looks as if brid is moving to right)
    int gravity = 1; // wiwa will fall due to force of gravity if not pushed up

    ArrayList<Pipe> pipes;
    Random random = new Random();

    // game timers
    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;

    // constructor
    FlappyWiwa() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("./flappyBG.jpeg")).getImage();
        wiwaImg = new ImageIcon(getClass().getResource("./wiwa.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        // change to png file later!!!
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        wiwa = new Wiwa(wiwaImg);
        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        gameLoop = new Timer(1000 / 60, this); // 60 frames per second
        gameLoop.start(); // start game timer, running 60 times per second
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2)); // quarter to 3/4 of pipe
                                                                                             // height
        int openingSpace = boardHeight / 4;
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // invoke function from super JPanel
        draw(g);

    }

    // setting graphics
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(wiwa.img, wiwa.x, wiwa.y, wiwa.width, wiwa.height, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
           // g.drawString("Happy Birthday Wiwa!", 10, 65);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        wiwa.y += velocityY;
        wiwa.y = Math.max(wiwa.y, 0); // wiwa cannot move past top of screen

        // moving pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            // wiwa is past the x position of the pipe
            if (!pipe.passed && wiwa.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // 0.5 because will add up to 1 point after passing both pipes
            }
            if (collision(wiwa, pipe)) {
                gameOver = true;
            }
        }
        if (wiwa.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Wiwa a, Pipe b) {
        // if wiwa doesnt reach any of the pipe corners, true
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); // calls paint componenet

        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) { // if space bar is pressed
            velocityY = -9;

            if (gameOver) {
                // reset conditions to start game again
                wiwa.y = wiwaY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
