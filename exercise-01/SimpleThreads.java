// The job each thread will do
class Printer implements Runnable {
    private String name;

    Printer(String name) {
        this.name = name;
    }

    public void run() {
        for (int i = 1; i <= 20; i++) {
            System.out.println("Thread " + name + ": " + i);
        }
    }
}

public class SimpleThreads {
    public static void main(String[] args) {
        Thread t1 = new Thread(new Printer("A"));
        Thread t2 = new Thread(new Printer("B"));
        Thread t3 = new Thread(new Printer("C"));

        t1.start();
        t2.start();
        t3.start();

        System.out.println("main is finished");
    }
}