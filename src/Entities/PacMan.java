package Entities;

import javax.swing.*;
import java.awt.*;
import Logic.*;

public class PacMan extends Entity {
    private boolean isHunting = false;      //--hunt-state
    private boolean treatEaten = false;     //--eats-HP
    private boolean freezed = false;
    private boolean poisoned = false;
    private boolean confused = false;

    public static int refHP = 3;

    private int rowPos, colPos;
    private int maxRow, maxCol;

    private ImageIcon pacSprite;

    //--other-logic-components
    private int scorePoints;
    private static ImageIcon BLUE_GHOST_SPRITE = new ImageIcon("src/res/ghosts/blue_ghost.png");
    private String color;
    private DirectionMovement dm = DirectionMovement.STOP;

    private Model model;

    public PacMan(ImageIcon sprite, int rowPos, int colPos, Model model, int maxRow, int maxCol) {
        this.pacSprite = sprite;
        this.rowPos = rowPos;
        this.colPos = colPos;
        this.model = model;
        this.maxRow = maxRow;
        this.maxCol = maxCol;

        model.setValue(new Tile(sprite, Color.BLACK), rowPos, colPos);
    }

    //--HP-logic
    public static int get_st_HP() {
        return refHP;
    }
    public static void up_st_HP() {
        if(refHP ==3) return;
        refHP += 1;
    }
    public static void lose_st_HP() {
        refHP -= 1;
    }
    public static void reset_st_HP() {refHP = 3;}

    //--states-logic
    public boolean isHunting() {return this.isHunting;}
    public boolean isTreatEaten() {
        return this.treatEaten;
    }
    public boolean isFreezed() {
        return this.freezed;
    }
    public boolean isPoisoned() {return this.poisoned;}
    public boolean isConfused() {return this.confused;}

    public void setHunting(boolean change) {this.isHunting = change;}
    public void setTreatEaten(boolean change) {
        this.treatEaten = change;
    }
    public void setFreezed(boolean change) {
        this.freezed = change;
    }
    public void setPoisoned(boolean change) {this.poisoned = change;}
    public void setConfused(boolean change) {this.confused = change;}

    //--direction
    public DirectionMovement getDirection() {return this.dm;}
    public void setDirection(DirectionMovement dm) {
        this.dm = dm;
    }


    public String checkPowerUp(Color color) {
        if(Color.YELLOW.equals(color)) {
            return "YELLOW";
        }
        if(Color.RED.equals(color)) {
            return "RED";
        }
        if(Color.CYAN.equals(color)) {
            return "CYAN";
        }
        if(Color.GREEN.equals(color)) {
            return "GREEN";
        }
        if(Color.MAGENTA.equals(color)) {
            return "MAGENTA";
        }
        return "...";
    }

    public void move() {
        int nextRow = rowPos;
        int nextCol = colPos;

        switch (dm) {
            case DirectionMovement.UP -> nextRow = (rowPos - 1 + maxRow) % maxRow;
            case DirectionMovement.DOWN -> nextRow = (rowPos + 1) % maxRow;
            case DirectionMovement.RIGHT -> nextCol = (colPos + 1) % maxCol;
            case DirectionMovement.LEFT -> nextCol = (colPos - 1 + maxCol) % maxCol;
            default -> {
                return;
            }
        }

        Tile nextTile = model.getNextValue(rowPos, colPos, dm);
        if (nextTile != null && Color.BLUE.equals(nextTile.getColor())) {
            dm = DirectionMovement.STOP;
            return;
        }

        //--scorePoints-incremental-by-dots
        if(nextTile.getImg() != null && nextTile.getImg().equals(model.getDotIco())) {
            scorePoints += 10;
            if(scorePoints % 100 == 0) System.out.println("[DEBUG]: ScorePoints -> " + scorePoints);
        }

        //--check-powerUps
        color = checkPowerUp(nextTile.getColor());
        switch(color) {
            case "YELLOW":
                isHunting = true;
                break;
            case "RED":
                treatEaten = true;
                break;
            case "CYAN":
                freezed = true;
                break;
            case "GREEN":
                poisoned = true;
                break;
            case "MAGENTA":
                confused = true;
                break;
            default:
                break;
        }

        model.setValue(new Tile(null, Color.BLACK), rowPos, colPos);
        model.setValue(new Tile(pacSprite, Color.BLACK), nextRow, nextCol);

        rowPos = nextRow;
        colPos = nextCol;
    }

    //--score-logic
    public int getScorePoints() {
        return this.scorePoints;
    }
    public void addScorePoint(int points) {
        this.scorePoints += points;
    }

    //-sprites
    public ImageIcon getSprite() {
        return pacSprite;
    }
    public void setPacSprite(ImageIcon sprite) {
        this.pacSprite = sprite;
    }

    //--other
    public int getRowPos() {
        return rowPos;
    }
    public int getColPos() {
        return colPos;
    }
    public void setRowPos(int x) {
        this.rowPos = x;
    }
    public void setColPos(int y) {
        this.colPos = y;
    }
}
