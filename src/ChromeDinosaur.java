import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 750;
    int boardHeight = 250;

    //images
    Image dinoImage;
    Image dinoDeadImage;
    Image dinoJumpImage;
    Image cactus1Image;
    Image cactus2Image;
    Image cactus3Image;


    class Block{
        int x;
        int y;
        int width;
        int height;
        Image img;

        Block(int x, int y, int width, int height, Image img){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int dinoWidth = 88;
    int dinoHeight = 94;
    int dinoX = 50;
    int dinoY = boardHeight - dinoHeight;

    Block dino;

    // cactus
    int cactus1Width = 34;
    int cactus2Width = 69;
    int cactus3Width = 102;

    int cactusHeight = 70;
    int cactusX = 700;
    int cactusY = boardHeight - cactusHeight;
    ArrayList<Block> cactusArray;

    //physics
    int velocityX = -12;
    int velocityY = 0; // dino jump speed
    int gravity = 1;

    boolean gameOver = false;
    int score = 0;


    Timer gameLoop;
    Timer placeCactusTimer;

    

    // CONSTRUCTOR 
    public ChromeDinosaur(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.lightGray);
        setFocusable(true);
        addKeyListener(this);


        dinoImage = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
        dinoDeadImage = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
        dinoJumpImage = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
        cactus1Image = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
        cactus2Image = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
        cactus3Image = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();

        //Dinosaur
        dino = new Block(dinoX, dinoY, dinoWidth, dinoHeight, dinoImage);
        // cactus
        cactusArray = new ArrayList<Block>();

        //game timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        //place cactus timer
        placeCactusTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placeCactus();
            }
        });

        placeCactusTimer.start();
    }


    // METHODS

    void placeCactus(){
        if (gameOver){
            return;
        }


        double placeCactusChance = Math.random(); // 0 - .9999
        if (placeCactusChance > .90) {
            Block cactus = new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Image);
            cactusArray.add(cactus);
        }
        else if (placeCactusChance > .70){
            Block cactus = new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Image);
            cactusArray.add(cactus);
        }
        else if (placeCactusChance >.5) {
            Block cactus = new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Image);
            cactusArray.add(cactus);
        }

        // Ensures list isn't ever growing
        if (cactusArray.size() > 10) {
            cactusArray.remove(0);
        }
    }


    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g){
        g.drawImage(dino.img, dino.x, dino.y, dino.width, dino.height, null);

        //cactus
        for (int i = 0; i < cactusArray.size(); i++){
            Block cactus = cactusArray.get(i);
            g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }
        g.setColor(Color.black);
        g.setFont(new Font("Ariel", Font.PLAIN, 32));
        if (gameOver){
            g.drawString("Game Over: " + String.valueOf(score), 10, 35);
        }
        else{
            g.drawString(String.valueOf(score), 10, 35);
        }
    }   

    public void move(){
        //dino
        velocityY += gravity;
        dino.y += velocityY;

        if (dino.y > dinoY){
            dino.y = dinoY;
            velocityY = 0;
            dino.img = dinoImage;
        }

        //cactus
        for (int i = 0; i<cactusArray.size(); i++){
            Block cactus = cactusArray.get(i);
            cactus.x += velocityX;

            if (collision(dino, cactus)) {
                gameOver = true;
                dino.img = dinoDeadImage;
            }

        }

        score++;
    }

    
    boolean collision(Block a, Block b){
        return (a.x < b.x + b.width && //a top left corner don't reach b top right corner
                a.x + a.width > b.x && //a top right corner passes b top left corner
                a.y < b.y + b.height && //a top left corner doesn't reach b bottom left corner
                a.y + a.height > b.y);  // a bottom left corner passes b top left corner

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            placeCactusTimer.stop();
            gameLoop.stop();
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            // System.out.println("Jump");
            if (dino.y == dinoY){
                velocityY = -17;
                dino.img = dinoJumpImage;
            }

            if(gameOver){
                dino.y = dinoY;
                dino.img = dinoImage;
                velocityY = 0;

                cactusArray.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placeCactusTimer.start();
            }
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {}

    
    @Override
    public void keyReleased(KeyEvent e) {}
}
