import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class JsonRpcClient {
    static BufferedReader in;
    static PrintWriter out;
    static int nextId = 1;   // every request gets its own id

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9092);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

        // 1. Write the records
        addRecord(4101, "Appen");
        addRecord(4102, "Ahrensburg");
        addRecord(4103, "Wedel");
        addRecord(4104, "Aumühle");
        addRecord(4105, "Seevetal");
        addRecord(4106, "Quickborn");

        // 2. Read two records (4107 does not exist!)
        getRecord(4103);
        getRecord(4107);

        // 3. How many records are stored?
        getSize();

        socket.close();
    }

    // One remote procedure call: send the request line, wait for the response line
    static String call(String method, String params) throws IOException {
        String request = "{\"jsonrpc\": \"2.0\", \"method\": \"" + method + "\", \"params\": "
                + params + ", \"id\": " + nextId + "}";
        nextId++;
        System.out.println("--> " + request);
        out.println(request);
        String response = in.readLine();
        System.out.println("<-- " + response);
        System.out.println();
        return response;
    }

    static void addRecord(int key, String record) throws IOException {
        call("addRecord", "[" + key + ", \"" + record + "\"]");
    }

    static void getRecord(int key) throws IOException {
        call("getRecord", "[" + key + "]");
    }

    static void getSize() throws IOException {
        call("getSize", "[]");
    }
}