package org.example;

public class TetrisGrid {
    private boolean[][] grid;

    public TetrisGrid(boolean[][] g) {
        int cols = g.length;
        int rows = g[0].length;
        grid = new boolean[cols][rows];
        for (int c = 0; c < cols; c++) {
            System.arraycopy(g[c], 0, grid[c], 0, rows);
        }
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public void clearRows() {
        int cols = grid.length;
        int rows = grid[0].length;

        int writeRow = 0;
        for (int row = 0; row < rows; row++) {
            if (!isFullRow(row)) {
                if (writeRow != row) {
                    for (int c = 0; c < cols; c++) {
                        grid[c][writeRow] = grid[c][row];
                    }
                }
                writeRow++;
            }
        }

        for (int row = writeRow; row < rows; row++) {
            for (int c = 0; c < cols; c++) {
                grid[c][row] = false;
            }
        }
    }

    private boolean isFullRow(int row) {
        for (int c = 0; c < grid.length; c++) {
            if (!grid[c][row]) return false;
        }
        return true;
    }
}
