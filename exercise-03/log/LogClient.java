import java.io.*;
import java.net.*;

public class LogClient {
    public static void main(String[] args) throws IOException {
        // 1. Connect to the log server
        Socket socket = new Socket("localhost", 9090);
        OutputStream out = socket.getOutputStream();

        // 2. Build a log message with the generated builder
        LogMessage msg1 = LogMessage.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setSource("LogClient@" + InetAddress.getLocalHost().getHostName())
                .setSeverity(LogMessage.Severity.INFO)
                .setText("Client started")
                .build();

        LogMessage msg2 = LogMessage.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setSeverity(LogMessage.Severity.ERROR)      // no source: it is optional
                .setText("Something went wrong")
                .build();

        // 3. Send them (serialize to bytes, with the length in front)
        msg1.writeDelimitedTo(out);
        msg2.writeDelimitedTo(out);
        out.flush();

        System.out.println("Sent 2 log messages.");
        socket.close();
    }
}