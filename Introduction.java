public class Introduction {
    public static void main(String[] args) {
        System.out.println("Phan Duc Hieu\t24020130\tCNTT1\t24020130\tphanduchieu1905@gmail.com");
        for (int i = 9; i >= 1; i--) {
            if (i > 1) {
                System.out.println(i + " bottles of beer on the wall, " + i + " bottles of beer.");
                System.out.println("Take one down, pass it around,");
            } else {
                System.out.println("1 bottle of beer on the wall, 1 bottle of beer.");
                System.out.println("Take one down, pass it around,");
                System.out.println("No more bottles of beer on the wall.");
            }
        }
    }
}