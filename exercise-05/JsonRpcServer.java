import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JsonRpcServer {
    // The database: key -> record. Shared by all client threads!
    static Map<Integer, String> database = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9092);
        System.out.println("JSON-RPC server running on port 9092");

        while (true) {
            Socket client = server.accept();                  // wait for a client
            new Thread(new JsonRpcHandler(client)).start();   // one thread per client
        }
    }

    // Execute ONE request and build the JSON response.
    // synchronized: only one thread at a time touches the database (Exercise 1!)
    static synchronized String execute(String request) {
        String method = JsonHelper.getString(request, "method");
        String id = JsonHelper.getId(request);
        List<String> params = JsonHelper.getParams(request);

        if (method == null) {
            return error(-32700, "Parse error", id);
        }

        try {
            switch (method) {

                case "addRecord": {
                    int key = Integer.parseInt(params.get(0));
                    String record = params.get(1);
                    database.put(key, record);
                    return result("true", id);
                }

                case "getRecord": {
                    int key = Integer.parseInt(params.get(0));
                    if (database.containsKey(key)) {
                        return result("\"" + database.get(key) + "\"", id);
                    } else {
                        return error(-32000, "No record with key " + key, id);
                    }
                }

                case "getSize": {
                    return result(String.valueOf(database.size()), id);
                }

                default:
                    return error(-32601, "Method not found", id);
            }
        } catch (Exception e) {
            // missing or wrong parameters, e.g. getRecord without a key
            return error(-32602, "Invalid params", id);
        }
    }

    static String result(String value, String id) {
        return "{\"jsonrpc\": \"2.0\", \"result\": " + value + ", \"id\": " + id + "}";
    }

    static String error(int code, String message, String id) {
        return "{\"jsonrpc\": \"2.0\", \"error\": {\"code\": " + code
                + ", \"message\": \"" + message + "\"}, \"id\": " + id + "}";
    }
}

// The job of one thread: serve ONE client (one JSON message per line)
class JsonRpcHandler implements Runnable {
    private Socket client;

    JsonRpcHandler(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true);

            String request;
            while ((request = in.readLine()) != null) {      // one request per line
                String response = JsonRpcServer.execute(request);
                out.println(response);                         // one response per line
                System.out.println(Thread.currentThread().getName() + " --> " + request);
                System.out.println(Thread.currentThread().getName() + " <-- " + response);
            }
            client.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

// Very small JSON "parser" using simple string processing
class JsonHelper {

    // Finds "name": "value" and returns value (or null)
    static String getString(String json, String name) {
        int pos = json.indexOf("\"" + name + "\"");
        if (pos < 0) return null;
        int start = json.indexOf("\"", json.indexOf(":", pos) + 1) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    // Finds "id": 5 and returns "5" (or "null" if there is no id)
    static String getId(String json) {
        int pos = json.indexOf("\"id\"");
        if (pos < 0) return "null";
        int start = json.indexOf(":", pos) + 1;
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(start, end).trim();
    }

    // Finds "params": [4101, "Appen"] and returns the list ["4101", "Appen"]
    static List<String> getParams(String json) {
        List<String> list = new ArrayList<>();
        int pos = json.indexOf("\"params\"");
        if (pos < 0) return list;
        int start = json.indexOf("[", pos) + 1;
        int end = json.indexOf("]", start);
        for (String part : json.substring(start, end).split(",")) {
            part = part.trim().replace("\"", "");             // remove spaces and quotes
            if (!part.isEmpty()) list.add(part);
        }
        return list;
    }
}