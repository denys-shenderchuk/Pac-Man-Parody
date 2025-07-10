package Entities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import Logic.*;

public class Blinky extends Entity{

    private boolean hit = false;                //--hit-confirmed
    private boolean scared = false;             //--can-be-eaten

    private int rowPos, colPos;
    private int maxRow, maxCol;

    private ImageIcon bSprite;                  //--main-sprite
    private final ImageIcon DEFAULT_SPRITE;     //--reserves-basic-sprite

    //--other-logic-components
    private int prevPowerUp = 0;
    private Tile keepPrev;
    private DirectionMovement dm = DirectionMovement.RIGHT;

    //--keep-info-about-pacman
    private PacMan pacMan;

    private Model model;

    private ArrayList<Tile> powerUps;

    public Blinky(PacMan pacMan, ImageIcon sprite, int rowPos, int colPos, Model model, int maxRow, int maxCol) {
        this.pacMan = pacMan;
        DEFAULT_SPRITE = sprite;
        this.bSprite = sprite;
        this.rowPos = rowPos;
        this.colPos = colPos;
        this.model = model;
        this.maxRow = maxRow;
        this.maxCol = maxCol;

        this.keepPrev = (Tile) model.getValueAt(rowPos, colPos);

        powerUps = new ArrayList<>();
        Tile heal = new Tile(null, Color.RED);
        powerUps.add(heal);
        Tile freeze = new Tile(null, Color.CYAN);
        powerUps.add(freeze);
        Tile poise = new Tile(null, Color.GREEN);
        powerUps.add(poise);
        Tile confuse = new Tile(null, Color.MAGENTA);
        powerUps.add(confuse);
        Tile nullTile = new Tile(null, Color.LIGHT_GRAY);
        powerUps.add(nullTile);
    }

    public void move() {
        Random random = new Random();

        int pacRow = pacMan.getRowPos();
        int pacCol = pacMan.getColPos();

        model.setValue(keepPrev, rowPos, colPos);

        int nextRow = rowPos;
        int nextCol = colPos;

        switch (dm) {
            case DirectionMovement.UP -> nextRow = (rowPos - 1 + maxRow) % maxRow;
            case DirectionMovement.DOWN -> nextRow = (rowPos + 1) % maxRow;
            case DirectionMovement.LEFT -> nextCol = (colPos - 1 + maxCol) % maxCol;
            case DirectionMovement.RIGHT -> nextCol = (colPos + 1) % maxCol;
            default -> {
                model.setValue(new Tile(bSprite, Color.BLACK), rowPos, colPos);
                return;
            }
        }

        //--setting-movement
        Tile nextTile = model.getNextValue(rowPos, colPos, dm);
        if (nextTile == null || Color.BLUE.equals(nextTile.getColor())) {
            DirectionMovement[] directions = DirectionMovement.values();
            ArrayList<DirectionMovement> possibleWays = new ArrayList<>();

            for (DirectionMovement dir : directions) {

                if (dir == DirectionMovement.STOP) continue;

                Tile testTile = model.getNextValue(rowPos, colPos, dir);
                if (testTile != null && !Color.BLUE.equals(testTile.getColor())) {
                    possibleWays.add(dir);
                }
            }

            if (!possibleWays.isEmpty()) {
                dm = possibleWays.get(random.nextInt(possibleWays.size()));

                nextRow = rowPos;
                nextCol = colPos;
                switch (dm) {
                    case DirectionMovement.UP -> nextRow = (rowPos - 1 + maxRow) % maxRow;
                    case DirectionMovement.DOWN -> nextRow = (rowPos + 1) % maxRow;
                    case DirectionMovement.LEFT -> nextCol = (colPos - 1 + maxCol) % maxCol;
                    case DirectionMovement.RIGHT -> nextCol = (colPos + 1) % maxCol;
                }
            } else {
                dm = DirectionMovement.STOP;
            }
        }

        if (nextRow == pacRow && nextCol == pacCol) {
            if(pacMan.isHunting() == false) {
                if (hit == true) {
                    PacMan.lose_st_HP();
                    System.out.println("[DEBUG] Entities.Blinky: Hit | HP: " + PacMan.get_st_HP());
                    hit = false;
                }
            }
            if(pacMan.isHunting() == true) {
                this.eaten();
            }
        }

        Tile originalTile = (Tile) model.getValueAt(nextRow, nextCol);
        if (prevPowerUp >= 33) { //--random-powerUp-every-5-seconds(33-clock-ticks)
            if (random.nextInt(100) <= 25) {
                keepPrev = powerUps.get(random.nextInt(powerUps.size()));
                prevPowerUp = 0;
            } else {
                keepPrev = originalTile;
                prevPowerUp++;
            }
        } else {
            keepPrev = originalTile;
            prevPowerUp++;
        }

        model.setValue(keepPrev, rowPos, colPos);
        model.setValue(new Tile(bSprite, Color.BLACK), nextRow, nextCol);

        rowPos = nextRow;
        colPos = nextCol;
    }

    //--scare-logic
    public boolean isScared() {return this.scared;}
    public void setScared(boolean change) {this.scared = change;}
    public void eaten() {
        Tile pos = (Tile) model.getValueAt(this.getRowPos(), this.getColPos());
        pos = new Tile(null, Color.BLACK);
        this.dm = DirectionMovement.STOP;
        this.setSprite(null);
    }

    //--sprites
    public ImageIcon getSprite() {
        return bSprite;
    }
    public void setSprite(ImageIcon sprite) {
        this.bSprite = sprite;
    }
    public void setDefaultSprite() {
        this.bSprite = DEFAULT_SPRITE;
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
