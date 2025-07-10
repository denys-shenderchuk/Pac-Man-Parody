import Entities.*;
import Logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener {

    private Thread gameThread;

    private boolean isColliding = false;    //--collision-with-objects
    private boolean huntEnabled = false;    //--hunt-scared-ghosts
    private boolean isTerminated = false;   //--thread-ended/game-finished
    private boolean wasHit = false;         //--pacman-gets-hit
    private boolean startMoving = false;    //--invoke-movement-for-all

    private final int SIZE = 16;
    private int rows, columms;

    private ImageIcon pacIco;
    private ImageIcon bIco;
    private ImageIcon pIco;
    private ImageIcon inIco;
    private ImageIcon cIco;

    private int scoreNum;
    private JLabel scoreText = new JLabel("Score: 0");

    private JTable scoreTable;
    private JTable playgrndTable;
    private JTable livesTable;

    private Model model;
    ScoreList sl = new ScoreList();

    private PacMan pacMan;
    private Blinky blinky;
    private Pinky pinky;
    private Inky inky;
    private Clyde clyde;

    public GamePanel(Main main, int rows, int cols) {
        this.rows = rows;
        this.columms = cols;

        this.setLayout(new BorderLayout());
        model = new Model(rows, cols);

        scoreTable = new JTable(new Model(3, cols));
        playgrndTable = new JTable(model);
        livesTable = new JTable(new Model(3, cols));

        scoreText.setOpaque(true);
        scoreText.setFont(new Font("Arial", Font.BOLD, 18));
        scoreText.setBackground(Color.BLACK);
        scoreText.setForeground(Color.WHITE);

        playgrndTable.setDefaultRenderer(Object.class, new CustomCellRenderer());
        livesTable.setDefaultRenderer(Object.class, new CustomCellRenderer());
        scoreTable.setDefaultRenderer(Object.class, new CustomCellRenderer());

        playgrndTable.setRowHeight(SIZE);
        livesTable.setRowHeight(SIZE);

        playgrndTable.setShowGrid(true);
        playgrndTable.setGridColor(Color.BLACK);
        playgrndTable.setTableHeader(null);
        playgrndTable.setEnabled(false);
        playgrndTable.setFocusable(false);

        livesTable.setBackground(Color.BLACK);
        livesTable.setShowGrid(false);

        for (int i = 0; i < columms; i++) {
            playgrndTable.getColumnModel().getColumn(i).setPreferredWidth(SIZE);
            livesTable.getColumnModel().getColumn(i).setPreferredWidth(SIZE);
            scoreTable.getColumnModel().getColumn(i).setPreferredWidth(SIZE);
        }

        pacIco = new ImageIcon("src/res/pacman-RIGHT/RIGHT_1.png");
        pacMan = new PacMan(pacIco, rows/2, cols/2, model, rows, cols);

        bIco = new ImageIcon("src/res/ghosts/blinky.png");
        blinky = new Blinky(pacMan, bIco, rows/3, cols/3, model, rows, cols);

        pIco = new ImageIcon("src/res/ghosts/pinky.png");
        pinky = new Pinky(pacMan, pIco, rows - 5, cols/2, model, rows, cols);

        inIco = new ImageIcon("src/res/ghosts/inky.png");
        inky = new Inky(pacMan, inIco, 3, cols/2, model, rows, cols);

        cIco = new ImageIcon("src/res/ghosts/clyde.png");
        clyde = new Clyde(pacMan, cIco, 5, cols/2, model, rows, cols);

        add(scoreText, BorderLayout.NORTH);
        add(playgrndTable, BorderLayout.CENTER);
        add(livesTable, BorderLayout.SOUTH);
        setFocusable(true);
        addKeyListener(this);

        List<Entity> ghosts = List.of(blinky, pinky, inky, clyde);

        PacMan.reset_st_HP();
        gameThread = new Thread(
                () -> {
                    while (!isTerminated) {
                        isColliding = false;
                        assignLives();
                        if(startMoving) {
                            pacMan.move();
                            scoreNum = pacMan.getScorePoints();
                            updateScore();

                            if(pacMan.isHunting()) {
                                if (huntEnabled == false) {
                                    huntEnabled = true;

                                    for (Entity e: ghosts) {
                                        e.setScared(true);
                                        e.setSprite(new ImageIcon("src/res/ghosts/blue_ghost.png"));
                                        model.setValue(new Tile(e.getSprite(), Color.BLACK), e.getRowPos(), e.getColPos());
                                    }

                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(10000);
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                        }
                                        pacMan.setHunting(false);
                                        huntEnabled = false;

                                        for (Entity e: ghosts) {
                                            e.setDefaultSprite();
                                            e.setScared(false);
                                            model.setValue(new Tile(e.getDefaultSprite(), Color.BLACK), e.getRowPos(), e.getColPos());
                                        }
                                    }).start();
                                }
                            }
                            if(pacMan.isTreatEaten()) {
                                System.out.println("[DEBUG]: HP BEF -> " + PacMan.get_st_HP());
                                PacMan.up_st_HP();
                                System.out.println("[DEBUG]: HP AFT -> " + PacMan.get_st_HP());
                                pacMan.setTreatEaten(false);
                                assignLives();
                            }
                            if(pacMan.isFreezed()) {
                                pacMan.setDirection(DirectionMovement.STOP);
                                pacMan.setFreezed(false);
                            }
                            if(pacMan.isPoisoned()) {
                                PacMan.lose_st_HP();
                                if(PacMan.get_st_HP() <= 0) {
                                    isTerminated = true;

                                    invokeSaving(main);
                                    break;
                                }
                                pacMan.setPoisoned(false);
                            }
                            if(pacMan.isConfused()) {
                                Random random = new Random();
                                DirectionMovement[] directions = DirectionMovement.values();
                                DirectionMovement chaotic = directions[random.nextInt(directions.length)];
                                pacMan.setDirection(chaotic);

                                new Thread(() -> {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    pacMan.setConfused(false);
                                    pacMan.setDirection(DirectionMovement.STOP);
                                }).start();
                            }

                            blinky.move();
                            pinky.move();
                            inky.move();
                            clyde.move();

                            // --check-for-collision
                            for (Entity ghost : ghosts) {
                                if (pacMan.getRowPos() == ghost.getRowPos() && pacMan.getColPos() == ghost.getColPos()) {
                                    if (ghost.isScared() == false) {
                                        isColliding = true;
                                        if (!wasHit) { //--pacman-gets-hit
                                            PacMan.lose_st_HP();
                                            System.out.println("[DEBUG] Entities.PacMan was hit | HP: " + PacMan.get_st_HP());
                                            wasHit = true;
                                            startMoving = false;

                                            // --game-over
                                            if (PacMan.get_st_HP() <= 0) {
                                                System.out.println("[DEBUG] Game Over!");
                                                isTerminated = true;

                                                invokeSaving(main);
                                                break;
                                            }

                                            try {
                                                Thread.sleep(3000);
                                            } catch (InterruptedException e) {
                                                gameThread.interrupt();
                                            }
                                            reset(); //--restart-game-field
                                            break;
                                        }
                                    } else { //--pacman-eats-ghost
                                        isColliding = true;

                                        model.setValue(new Tile(null, Color.BLACK), ghost.getRowPos(), ghost.getColPos());

                                        ghost.setRowPos(rows / 3);
                                        ghost.setColPos(columms / 2);
                                        ghost.setDirection(DirectionMovement.STOP);

                                        ghost.setSprite(ghost.getDefaultSprite());
                                        model.setValue(new Tile(ghost.getSprite(), Color.BLACK), ghost.getRowPos(), ghost.getColPos());

                                        pacMan.addScorePoint(200);
                                        System.out.println("[DEBUG] Ghost eaten by Pac-Man");
                                    }
                                }
                            }
                            if (!isColliding) {
                                wasHit = false;
                            }
                            repaint();
                        }
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            gameThread.interrupt();
                        }
                    }
                    goToMenu(main);
                    gameThread.interrupt();
                }
        );
        gameThread.start();
    }

    private void assignLives() {
        ImageIcon hpIco = new ImageIcon("src/res/pacman-right/RIGHT_1.png"); // better life icon here
        int health = PacMan.get_st_HP();

        Model livesModel = (Model) livesTable.getModel();

        for (int i = 0; i < columms; i++) {
            livesModel.setValue(1, i, null, Color.BLACK);
        }

        for (int i = 0; i < health && i < columms; i++) {
            livesModel.setValue(1, i, hpIco, Color.BLACK);
        }
    }

    public void updateScore() {
        scoreText.setText("Score " + scoreNum);
        repaint();
    }

    private void reset() {
        assignLives();
        model.setValue(new Tile(null, Color.BLACK), pacMan.getRowPos(), pacMan.getColPos());
        model.setValue(new Tile(null, Color.BLACK), blinky.getRowPos(), blinky.getColPos());
        model.setValue(new Tile(null, Color.BLACK), pinky.getRowPos(), pinky.getColPos());
        model.setValue(new Tile(null, Color.BLACK), inky.getRowPos(), inky.getColPos());
        model.setValue(new Tile(null, Color.BLACK), clyde.getRowPos(), clyde.getColPos());

        pacMan.setPacSprite(new ImageIcon("src/res/pacman-right/RIGHT_1.png"));
        pacMan.setRowPos(rows / 2);
        pacMan.setColPos(columms / 2);
        pacMan.setDirection(DirectionMovement.STOP);

        blinky.setRowPos(rows / 3);
        blinky.setColPos(columms / 2);
        blinky.setDirection(DirectionMovement.RIGHT);

        pinky.setRowPos(rows - 5);
        pinky.setColPos(columms / 2);
        pinky.setDirection(DirectionMovement.STOP);

        inky.setRowPos(3);
        inky.setColPos(columms / 2);
        inky.setDirection(DirectionMovement.STOP);

        clyde.setRowPos(5);
        clyde.setColPos(columms / 2);
        clyde.setDirection(DirectionMovement.STOP);

        blinky.move();
        pinky.move();
        inky.move();
        clyde.move();

        model.setValue(new Tile(pacMan.getSprite(), Color.BLACK), pacMan.getRowPos(), pacMan.getColPos());
        model.setValue(new Tile(blinky.getSprite(), Color.BLACK), blinky.getRowPos(), blinky.getColPos());
        model.setValue(new Tile(pinky.getSprite(), Color.BLACK), pinky.getRowPos(), pinky.getColPos());
        model.setValue(new Tile(inky.getSprite(), Color.BLACK), inky.getRowPos(), inky.getColPos());
        model.setValue(new Tile(clyde.getSprite(), Color.BLACK), clyde.getRowPos(), clyde.getColPos());
    }

    private void invokeSaving(Main main) {
        String playerName = JOptionPane.showInputDialog(main,"Game Over! Enter your name:",
                "Register New Score", JOptionPane.PLAIN_MESSAGE);

        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "unknown_score";
        }

        int finalScore = pacMan.getScorePoints();
        Save save = new Save(playerName, finalScore);
        sl.addScore(save.getSaveName(), save.getResult());
        sl.saveScores();

        goToHighScores(main);
    }

    public void goToHighScores(Main main) {
        main.remove(this);
        main.add(new HighScoresPanel(main));

        main.revalidate();
        main.repaint();
    }

    public void goToMenu(Main main) {
        main.getContentPane().removeAll();
        main.add(new MenuPanel(main));

        main.revalidate();
        main.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:
                startMoving = true;
                pacMan.setPacSprite(new ImageIcon("src/res/pacman-UP/UP_1.png"));
                pacMan.setDirection(DirectionMovement.UP);
                break;
            case KeyEvent.VK_DOWN:
                startMoving = true;
                pacMan.setDirection(DirectionMovement.DOWN);
                pacMan.setPacSprite(new ImageIcon("src/res/pacman-down/DOWN_1.png"));
                break;
            case KeyEvent.VK_RIGHT:
                startMoving = true;
                pacMan.setDirection(DirectionMovement.RIGHT);
                pacMan.setPacSprite(new ImageIcon("src/res/pacman-RIGHT/RIGHT_1.png"));
                break;
            case KeyEvent.VK_LEFT:
                startMoving = true;
                pacMan.setDirection(DirectionMovement.LEFT);
                pacMan.setPacSprite(new ImageIcon("src/res/pacman-left/LEFT_1.png"));
                break;
            default: pacMan.setDirection(DirectionMovement.STOP); break;
        }
        if(e.getKeyCode() == KeyEvent.VK_Q && e.isControlDown() && e.isShiftDown()) {
            isTerminated = true;
            System.out.println("[DEBUG]: Ctrl+Shift+Q initialised -> exit to the Menu");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
