import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MenuPanel extends JPanel{

    private JButton newGame, exit, highScore;
    private GamePanel gamePanel;
    private Graphics[] ovals = new Graphics[20];

    public MenuPanel(Main main) {
        this.setVisible(true);
        this.setLayout(new FlowLayout());

        this.setBackground(Color.BLACK);
        this.setSize(800, 600);

        newGame = new JButton("New Game");
        newGame.setBackground(Color.WHITE);
        highScore = new JButton("High Scores");
        highScore.setBackground(Color.WHITE);
        exit = new JButton("Exit");
        exit.setBackground(Color.WHITE);

        this.add(newGame);
        this.add(highScore);
        this.add(exit);

        newGame.addActionListener(
                e -> {
                    startGame(main);
                }
        );

        highScore.addActionListener(
                e -> {
                    goToHighScores(main);
                }
        );

        exit.addActionListener(
                e -> {
                    main.dispose();
                }
        );
    }

    public void goToHighScores(Main main) {
        main.remove(this);
        main.add(new HighScoresPanel(main));

        main.revalidate();
        main.repaint();
    }

    public void startGame(Main main) {
        int rows = Integer.parseInt(JOptionPane.showInputDialog("Enter the width of field: "));
        int columns = Integer.parseInt(JOptionPane.showInputDialog("Enter the height of field: "));

        main.remove(this);
        main.add(gamePanel = new GamePanel(main, rows, columns));
        main.pack();
        gamePanel.requestFocusInWindow();

        main.revalidate();
        main.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Random random = new Random();
        super.paintComponent(g);

        int rSize = random.nextInt(100) + 50;

        g.setColor(Color.YELLOW);
        for (int i = 0; i < ovals.length; i++) {
            g.fillOval(random.nextInt(800), random.nextInt(800), rSize, rSize);
        }
    }
}
