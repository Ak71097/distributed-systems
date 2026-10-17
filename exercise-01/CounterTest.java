// The Counter from the exercise sheet
class Counter {
    private int c = 0;

    public void increment() {
        int b = c;
        b = b + 1;
        c = b;
    }

    public void decrement() {
        int d = c;
        d = d - 1;
        c = d;
    }

    public int value() {
        return c;
    }
}

// Job 1: add 1, many times
class Adder implements Runnable {
    private Counter counter;

    Adder(Counter counter) {
        this.counter = counter;
    }

    public void run() {
        for (int i = 0; i < 100000; i++) {
            counter.increment();
        }
    }
}

// Job 2: subtract 1, many times
class Subtractor implements Runnable {
    private Counter counter;

    Subtractor(Counter counter) {
        this.counter = counter;
    }

    public void run() {
        for (int i = 0; i < 100000; i++) {
            counter.decrement();
        }
    }
}

public class CounterTest {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        Thread t1 = new Thread(new Adder(counter));
        Thread t2 = new Thread(new Subtractor(counter));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Expected: 0");
        System.out.println("Actual:   " + counter.value());
    }
}