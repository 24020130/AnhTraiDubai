package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class CharGrid {
        private int rows;
        private int cols;
        private char[][] grid;

    public CharGrid(char[][] c) {
            rows = c.length;
            cols = c[0].length;
            grid = new char[rows][cols];
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++){
                    grid[i][j] = c[i][j];
                }
            }
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        public char getCharAt(int a, int b){
            return grid[a][b];
        }

        public void setCharAt(int a, int b, char c){
            grid[a][b] = c;
        }

        public void print(){
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++){
                    System.out.print(grid[i][j] + " ");
                }
                System.out.println();
            }
        }
    public int charArea(char ch){
        int maxRow = -1, minRow = rows;
        int maxCol = -1, minCol = cols;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(grid[i][j] == ch){
                    if(i < minRow) minRow = i;
                    if(i > maxRow) maxRow = i;
                    if(j < minCol) minCol = j;
                    if(j > maxCol) maxCol = j;
                }
            }
        }
        if(maxRow == -1) return 0;
        return (maxRow - minRow + 1) * (maxCol - minCol + 1);
    }


    public int countPlus() {
            int count = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char ch = grid[i][j];
                    int up = 0, down = 0, left = 0, right = 0;
                    for (int r = i - 1; r >= 0 && grid[r][j] == ch; r--) up++;
                    for (int r = i + 1; r < rows && grid[r][j] == ch; r++) down++;
                    for (int c = j - 1; c >= 0 && grid[i][c] == ch; c--) left++;
                    for (int c = j + 1; c < cols && grid[i][c] == ch; c++) right++;
                    if (up >= 2 && up == down && up == left && up == right) {
                        count++;
                    }
                }
            }
            return count;
        }
}