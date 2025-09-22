import java.util.Scanner;

public class Quadratic {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        double b = sc.nextDouble();
        double c = sc.nextDouble();
        double discriminant = b * b - 4.0 * c;
        double sqroot = Math.sqrt(discriminant);

        double root1 = (-b + sqroot) / 2.0;
        double root2 = (-b - sqroot) / 2.0;

        System.out.println(root1);
        System.out.println(root2);
    }
}