import java.io.*;
import java.net.*;

public class DbClient {
    static InputStream in;
    static OutputStream out;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9091);
        in = socket.getInputStream();
        out = socket.getOutputStream();

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

    // Send one request, wait for the answer (= one remote procedure call)
    static RpcResponse call(RpcRequest request) throws IOException {
        request.writeDelimitedTo(out);
        out.flush();
        return RpcResponse.parseDelimitedFrom(in);
    }

    static void addRecord(int index, String record) throws IOException {
        RpcRequest request = RpcRequest.newBuilder()
                .setOperation(RpcRequest.Operation.ADD_RECORD)
                .setAddRecordArg(AddRecordRequest.newBuilder()
                        .setIndex(index)
                        .setRecord(record))
                .build();
        RpcResponse response = call(request);
        System.out.println("addRecord(" + index + ", " + record + ") -> success=" + response.getSuccess());
    }

    static void getRecord(int index) throws IOException {
        RpcRequest request = RpcRequest.newBuilder()
                .setOperation(RpcRequest.Operation.GET_RECORD)
                .setGetRecordArg(GetRecordRequest.newBuilder().setIndex(index))
                .build();
        RpcResponse response = call(request);
        if (response.getSuccess()) {
            System.out.println("getRecord(" + index + ") -> " + response.getRecord());
        } else {
            System.out.println("getRecord(" + index + ") -> ERROR: " + response.getError());
        }
    }

    static void getSize() throws IOException {
        RpcRequest request = RpcRequest.newBuilder()
                .setOperation(RpcRequest.Operation.GET_SIZE)
                .setGetSizeArg(GetSizeRequest.newBuilder())
                .build();
        RpcResponse response = call(request);
        System.out.println("getSize() -> " + response.getSize());
    }
}