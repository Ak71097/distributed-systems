import java.io.*;
import java.net.*;
import java.nio.file.*;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server running on port " + port);

        while (true) {
            Socket client = server.accept();                    // wait for a visitor
            Thread t = new Thread(new RequestHandler(client));  // one new thread per request
            t.start();
        }
    }
}

// The job of one thread: answer ONE request
class RequestHandler implements Runnable {
    private Socket client;

    RequestHandler(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream();

            // First line of the request, e.g. "GET /index.html HTTP/1.1"
            String requestLine = in.readLine();
            System.out.println(Thread.currentThread().getName() + " got: " + requestLine);

            if (requestLine == null) {
                client.close();
                return;
            }

            String path = requestLine.split(" ")[1];   // the middle part: "/index.html"
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("www" + path);

            if (!path.contains("..") && file.isFile()) {
                byte[] content = Files.readAllBytes(file.toPath());
                String header = "HTTP/1.1 200 OK\r\n"
                              + "Content-Type: text/html\r\n"
                              + "Content-Length: " + content.length + "\r\n"
                              + "Connection: close\r\n"
                              + "\r\n";
                out.write(header.getBytes());
                out.write(content);
            } else {
                String body = "<h1>404 Not Found</h1>";
                String header = "HTTP/1.1 404 Not Found\r\n"
                              + "Content-Type: text/html\r\n"
                              + "Content-Length: " + body.length() + "\r\n"
                              + "Connection: close\r\n"
                              + "\r\n";
                out.write(header.getBytes());
                out.write(body.getBytes());
            }

            out.flush();
            client.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}