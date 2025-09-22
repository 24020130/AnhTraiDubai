import java.util.Scanner;

public class RandomInt {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        double r = Math.random();
        int value = (int) (r * n);
        System.out.println(value);
    }
}
