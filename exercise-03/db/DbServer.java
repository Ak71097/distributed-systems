import java.io.*;
import java.net.*;
import java.util.*;

public class DbServer {
    // The "database": index -> record. Shared by all threads!
    static Map<Integer, String> database = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9091);
        System.out.println("Database server running on port 9091");

        while (true) {
            Socket client = server.accept();
            new Thread(new DbHandler(client)).start();   // one thread per client
        }
    }

    // synchronized: only one thread at a time touches the database (Exercise 1!)
    static synchronized RpcResponse execute(RpcRequest request) {
        switch (request.getOperation()) {

            case ADD_RECORD:
                int addIndex = request.getAddRecordArg().getIndex();
                String record = request.getAddRecordArg().getRecord();
                database.put(addIndex, record);
                return RpcResponse.newBuilder().setSuccess(true).build();

            case GET_RECORD:
                int getIndex = request.getGetRecordArg().getIndex();
                if (database.containsKey(getIndex)) {
                    return RpcResponse.newBuilder()
                            .setSuccess(true)
                            .setRecord(database.get(getIndex))
                            .build();
                } else {
                    return RpcResponse.newBuilder()
                            .setSuccess(false)
                            .setError("No record with index " + getIndex)
                            .build();
                }

            case GET_SIZE:
                return RpcResponse.newBuilder()
                        .setSuccess(true)
                        .setSize(database.size())
                        .build();

            default:
                return RpcResponse.newBuilder()
                        .setSuccess(false)
                        .setError("Unknown operation")
                        .build();
        }
    }
}

class DbHandler implements Runnable {
    private Socket client;

    DbHandler(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            RpcRequest request;

            // Read requests until the client hangs up
            while ((request = RpcRequest.parseDelimitedFrom(in)) != null) {
                RpcResponse response = DbServer.execute(request);
                response.writeDelimitedTo(out);
                out.flush();
                System.out.println(Thread.currentThread().getName() + ": " + request.getOperation()
                        + " -> success=" + response.getSuccess());
            }
            client.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
