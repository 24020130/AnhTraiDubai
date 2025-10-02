public class RandomWalk {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide an integer n as argument.");
            return;
        }
        int n = Integer.parseInt(args[0]);
        StdDraw.setScale(-n - 0.5, n + 0.5);
        StdDraw.clear(StdDraw.GRAY);
        StdDraw.enableDoubleBuffering();
        int x = 0, y = 0;
        int steps = 0;
        int[][] directions = { {1,0}, {0,1}, {-1,0}, {0,-1} };

        int dir = 0;
        int len = 1;
        boolean increase = false;

        while (Math.abs(x) < n && Math.abs(y) < n) {
            for (int i = 0; i < len; i++) {
                // Vẽ ô hiện tại
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.filledSquare(x, y, 0.45);
                x += directions[dir][0];
                y += directions[dir][1];
                steps++;
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.filledSquare(x, y, 0.45);
                StdDraw.show();
                StdDraw.pause(50);

                if (Math.abs(x) >= n || Math.abs(y) >= n) break;
            }
            dir = (dir + 1) % 4;
            if (increase) {
                len++;
            }
            increase = !increase;
        }

        StdOut.println("Total steps = " + steps);
    }
}
