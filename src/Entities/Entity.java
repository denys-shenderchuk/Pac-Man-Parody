package Entities;

import javax.swing.*;
import Logic.*;

public class Entity{

    private boolean scared = false;             //--can-be-eaten

    private int rowPos, colPos;

    private ImageIcon sprite;                  //--main-sprite
    private final ImageIcon DEFAULT_SPRITE;     //--reserves-basic-sprite

    //--direction
    private DirectionMovement dm;

    public Entity() {
        DEFAULT_SPRITE = sprite;
    }

    //--scare-logic
    public boolean isScared() {return this.scared;}
    public void setScared(boolean change) {this.scared = change;}

    //--sprites
    public ImageIcon getSprite() {
        return sprite;
    }
    public void setSprite(ImageIcon sprite) {
        this.sprite = sprite;
    }
    public void setDefaultSprite() {
        this.sprite = DEFAULT_SPRITE;
    }
    public ImageIcon getDefaultSprite() {
        return DEFAULT_SPRITE;
    }


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
    public void setDirection(DirectionMovement dm) {
        this.dm = dm;
    }
}
