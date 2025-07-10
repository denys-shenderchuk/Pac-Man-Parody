package Logic;

import javax.swing.*;
    import javax.swing.table.AbstractTableModel;
    import java.awt.*;
    import java.util.Random;

    public class Model extends AbstractTableModel{

        private final int ROWS, COLUMNS;
        private final int LIVES_BAR = 3;

        private Tile[][] objects;

        private ImageIcon dotIco = new ImageIcon("src/res/other/dot.png");

        public Model(int rows, int columns){
            this.ROWS = rows;
            this.COLUMNS = columns;
            objects = new Tile[rows][columns];

            if(rows == LIVES_BAR) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        objects[i][j] = new Tile(null, Color.BLACK);
                    }
                }
                return;
            }
            generateMaze();
        }

        public void generateMaze() {
            Random rand = new Random();

            //--set-all-null-and-black
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    objects[i][j] = new Tile(null, Color.BLACK);
                }
            }

            //--edges
            for (int i = 0; i < ROWS; i++) {
                objects[i][0] = new Tile(null, Color.BLUE);
                objects[i][COLUMNS - 1] = new Tile(null, Color.BLUE);
            }
            for (int j = 0; j < COLUMNS; j++) {
                objects[0][j] = new Tile(null, Color.BLUE);
                objects[ROWS - 1][j] = new Tile(null, Color.BLUE);
            }

            //--setting-blocks
            int space = 4;
            for (int row = space; row < ROWS - space; row += space) {
                for (int col = space; col < COLUMNS - space; col += space) {
                    objects[row][col] = new Tile(null, Color.BLUE);

                    //--stretching-maze
                    boolean stretchNorth = rand.nextBoolean();
                    boolean stretchSouth = rand.nextBoolean();
                    boolean stretchWest = rand.nextBoolean();
//                    boolean stretchEast = rand.nextBoolean();

                    int stretchMod = 3;
                    int controller = rand.nextInt(4);

                    switch(controller) {
                        case 1:
                            if (stretchNorth) {
                                for (int k = 1; k <= stretchMod && row - k > 0; k++) {
                                    objects[row - k][col] = new Tile(null, Color.BLUE);
                                }
                            }
                        case 2:
                            if (stretchSouth) {
                                for (int k = 1; k <= stretchMod && row + k < ROWS - 1; k++) {
                                    objects[row + k][col] = new Tile(null, Color.BLUE);
                                }
                            }
                        case 3:
                            if (stretchWest) {
                                for (int k = 1; k <= stretchMod && col - k > 0; k++) {
                                    objects[row][col - k] = new Tile(null, Color.BLUE);
                                }
                            }
//                        case 4:
//                            if (stretchEast) {
//                                for (int k = 1; k <= stretchMod && col + k < COLUMNS - 1; k++) {
//                                    objects[row][col + k] = new Logic.Tile(null, Color.BLUE);
//                                }
//                            }
                    }
                }

                for (int i = 1; i < ROWS - 1; i++) {
                    for (int j = 1; j < COLUMNS - 1; j++) {
                        if (objects[i][j].getColor().equals(Color.BLACK)) {
                            objects[i][j] = new Tile(dotIco, Color.BLACK);
                        }
                    }
                }
                setPowerUp_Hunt(objects);
            }
        }

        //--Logic.Model-handles-hunting-powerUps
        public void setPowerUp_Hunt(Tile[][] objects) {
            Random rand = new Random();
            Tile tile = objects[rand.nextInt(ROWS - 1)][rand.nextInt(COLUMNS - 1)];
            if(tile.getColor().equals(Color.BLACK) && tile.getImg().equals(dotIco)
                    || (!tile.getColor().equals(Color.BLUE) && tile.getImg() == null)) {
                objects[rand.nextInt(ROWS)][rand.nextInt(COLUMNS)] = new Tile(null, Color.YELLOW);
            }
        }

        public ImageIcon getDotIco() {
            return this.dotIco;
        }

        @Override
        public int getRowCount() {
            return ROWS;
        }

        @Override
        public int getColumnCount() {
            return COLUMNS;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return objects[rowIndex][columnIndex];
        }

        public Tile getNextValue(int rowIndex, int columnIndex, DirectionMovement dm) {
            int nextRow = rowIndex;
            int nextCol = columnIndex;

            switch (dm) {
                case UP -> nextRow = rowIndex - 1;
                case DOWN -> nextRow = rowIndex + 1;
                case LEFT -> nextCol = columnIndex - 1;
                case RIGHT -> nextCol = columnIndex + 1;
            }

            if (nextRow < 0 || nextRow >= ROWS || nextCol < 0 || nextCol >= COLUMNS) {
                return null;
            }

            return objects[nextRow][nextCol];
        }

        public void setValue(Tile object, int row, int column) {
            objects[row][column] = object;
            fireTableCellUpdated(row, column);
        }

        public void setValue(int row, int column, ImageIcon ico, Color c) {
            objects[row][column] = new Tile(ico, c);
            fireTableCellUpdated(row, column);
        }
    }
