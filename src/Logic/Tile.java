package Logic;

import javax.swing.*;
import java.awt.*;

public class Tile {

    private final ImageIcon img;
    private final Color c;

    public Tile(ImageIcon img, Color c) {
        this.img = img;
        this.c = c;
    }

    public ImageIcon getImg() {
        return img;
    }

    public Color getColor() {
        return c;
    }
}
