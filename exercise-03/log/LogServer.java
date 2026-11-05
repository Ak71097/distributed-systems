import java.io.*;
import java.net.*;
import java.time.*;

public class LogServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9090);
        System.out.println("Log server running on port 9090");

        while (true) {
            Socket client = server.accept();                 // wait for a client
            new Thread(new LogHandler(client)).start();      // one thread per client
        }
    }

    // Several threads write to the same file -> only one at a time (Exercise 1!)
    static synchronized void writeToFile(String line) throws IOException {
        FileWriter fw = new FileWriter("log.txt", true);    // true = append
        fw.write(line + "\n");
        fw.close();
    }
}

class LogHandler implements Runnable {
    private Socket client;

    LogHandler(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            InputStream in = client.getInputStream();
            LogMessage msg;

            // Read messages until the client closes the connection (then null)
            while ((msg = LogMessage.parseDelimitedFrom(in)) != null) {
                String time = Instant.ofEpochMilli(msg.getTimestamp()).toString();
                String source = msg.hasSource() ? msg.getSource() : "unknown";
                String line = time + " [" + msg.getSeverity() + "] (" + source + ") " + msg.getText();

                LogServer.writeToFile(line);
                System.out.println(Thread.currentThread().getName() + " logged: " + line);
            }
            client.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}