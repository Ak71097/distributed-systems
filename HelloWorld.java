public class HelloWorld {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java HelloWorld <name>");
            return;
        }
        System.out.println("Hello, world! Parameter: " + args[0]);
    }
}