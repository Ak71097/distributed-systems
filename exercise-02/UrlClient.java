import java.io.*;
import java.net.*;

public class UrlClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java UrlClient <url>");
            return;
        }

        URL url = URI.create(args[0]).toURL();

        // Java builds the socket and the HTTP request for us
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
    }
}