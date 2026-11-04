import java.io.*;
import java.net.*;


public class SimpleClient {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java SimpleClient <host> <port> <path>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String path = args[2];

        // 1. Connect to the server
        Socket socket = new Socket(host, port);

        // 2. Get the two "pipes": one to send, one to receive
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // 3. Send the HTTP request (each line ends with \r\n, empty line = end)
        out.print("GET " + path + " HTTP/1.1\r\n");
        out.print("Host: " + host + "\r\n");
        out.print("Connection: close\r\n");
        out.print("\r\n");
        out.flush();

        // 4. Read and print the answer line by line
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }

        // 5. Hang up
        socket.close();
    }
}
