import Logic.Save;
import Logic.ScoreList;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class HighScoresPanel extends JPanel {

    private final ScoreList highScoreManager;
    private JList<String> highScoreList;
    private JButton returnToMenuButton;

    public HighScoresPanel(Main main) {
        this.highScoreManager = new ScoreList();

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        loadScoresIntoModel(listModel);

        highScoreList = new JList<>(listModel);
        highScoreList.setFont(new Font("Arial", Font.BOLD, 40));
        highScoreList.setForeground(Color.YELLOW);
        highScoreList.setBackground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(highScoreList);
        add(scrollPane, BorderLayout.CENTER);

        returnToMenuButton = new JButton("Return to Menu");
        returnToMenuButton.setBackground(Color.WHITE);
        returnToMenuButton.addActionListener(e -> {
            goToMenu(main);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(returnToMenuButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadScoresIntoModel(DefaultListModel<String> model) {
        List<Save> scores = highScoreManager.getScores();

        if (scores.isEmpty()) {
            model.addElement("No high written. Play a bit!");
            return;
        }

        int rank = 1;
        for (Save entry : scores) {
            String line = rank + ". " + entry.getSaveName() + " | " + entry.getResult();
            model.addElement(line);
            rank++;
        }
    }

    public void goToMenu(Main main) {
        main.remove(this);
        main.add(new MenuPanel(main));

        main.revalidate();
        main.repaint();
    }
}
