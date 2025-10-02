public class UseArgument {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Hi, stranger. You didn't give me any arguments!");
        } else {
            System.out.print("Hi");
            for (String arg : args) {
                System.out.print(", " + arg);
            }
            System.out.println(". How are you?");
        }
    }
}
