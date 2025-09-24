public class AllEqual {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Please provide 3 integers as arguments.");
            return;
        }

        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        int c = Integer.parseInt(args[2]);

        if (a == b && a == c) {
            System.out.println("all equal");
        } else {
            System.out.println("not all equal");
        }
    }
}
