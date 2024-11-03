import java.awt.*;
import java.awt.color.*;
import java.awt.event.*;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image birdImg;
    Image backgroundImg;
    Image topPipeImg;
    Image bottompipeImg;

    //bird

    int birdY = boardHeight/2;
    int birdX = boardWidth/8;
    int birdwidth = 34;
    int birdheight =24;




    class Bird {
        int y =  birdY;
        int x = birdX;
        int height = birdheight;
        int widght = birdwidth;
        Image img;

        Bird(Image img){
            this.img = img;

        }
    }

    //pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipewidth = 64;
    int pipeheight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipewidth;
        int heigth = pipeheight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }
    //Logic Game
    Bird bird;
    int gravity = 1;
    int velocityX = -4;
    int velocityY = 0;
    Timer gameLoop;

    Timer placePipesTimer;
    ArrayList<Pipe> pipes;
    Random random = new Random();

    boolean gameOver = false;
    double score = 0;





    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.blue);

        setFocusable(true);
        addKeyListener(this);


        //Load image

        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappyBird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottompipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();


        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //PlacePipes Timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //Game Timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

    }

    public void placePipes(){
        int randomPipeY = (int) (pipeY - pipeheight/4 - Math.random()*(pipeheight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottompipe = new Pipe(bottompipeImg);
        bottompipe.y = topPipe.y + pipeheight + openingSpace;
        pipes.add(bottompipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);

    }

    public void draw (Graphics g){
        //backgraund
        g.drawImage(backgroundImg, 0, 0, boardWidth,boardHeight, null );
        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.widght, bird.height, null);

        //pipes
        for (int i = 0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.heigth, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver){
            g.drawString("Game over " + String.valueOf((int) score), 10,35);

        }else {
            g.drawString(String.valueOf((int)score), 10,35);
        }
    }


    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipe
        for (int i = 0; i< pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
            }

            if (colission(bird, pipe)){
                gameOver = true;
            }
        }
        if (bird.y > boardHeight){
            gameOver = true;

        }
    }

    public boolean colission(Bird a, Pipe b){
        return a.x < b.x + b.width &&
                a.x + a.widght > b.x &&
                a.y < b.y + b.heigth &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
        }
        if (gameOver){
            //Restart game by resetting the conditions

            bird.y = birdY;
            velocityY = 0;
            pipes.clear();
            score = 0;
            gameOver = false;
            gameLoop.start();
            placePipesTimer.start();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyReleased(KeyEvent e) {

    }

}
