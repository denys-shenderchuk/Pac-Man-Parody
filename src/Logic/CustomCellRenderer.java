package Logic;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomCellRenderer extends DefaultTableCellRenderer{

    public CustomCellRenderer(){
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Tile tile = (Tile) value;

        setIcon(tile.getImg());
        setBackground(tile.getColor());
        setOpaque(true);

        return this;
    }
}
