package Logic;

import java.io.*;
import java.util.*;

public class ScoreList {

    private final String FILE_NAME = "highscores.txt";
    private List<Save> saves = new ArrayList<>();

    public ScoreList() {
        loadScores();
    }

    public void loadScores() {
        saves.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) { //--read
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        String name = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        saves.add(new Save(name, score));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid score format");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveScores() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Save s: saves) {
                pw.println(s.getSaveName() + ":" + s.getResult());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Save> getScores() {
        return new ArrayList<>(saves);
    }

    public void addScore(String name, int score) {
        saves.add(new Save(name, score));
        saves.sort((a, b) -> Integer.compare(b.getResult(), a.getResult()));
//        if (saves.size() > 10) saves = saves.subList(0, 10);
        saveScores();
    }
}