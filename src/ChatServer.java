import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatServer {

    private static final int PORT = 9001;

    // HashSet to store unique client names
    private static HashSet<String> names = new HashSet<String>();

    // HashSet to store PrintWriter objects for broadcasting messages
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String args[]) throws Exception {

        System.out.println("The chat server is running");
        ServerSocket listener = new ServerSocket(PORT);

        try {
            // Continuously listen for incoming client connections
            while (true) {
                Socket socket = listener.accept();
                // Create a new thread to handle each client
                Thread handlerThread = new Thread(new Handler(socket));
                handlerThread.start();
            }
        } finally {
            listener.close();
        }
    }

    // Inner class for handling individual client connections
    private static class Handler implements Runnable {

        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // Initialize input and output streams for communication with the client
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    // Prompt the client to submit a name
                    out.println("SUBMITNAME");
                    name = in.readLine();

                    if (name == null) {
                        return;
                    }

                    // Check if the chosen name is already in use
                    if (!names.contains(name)) {
                        names.add(name);
                        break;
                    }
                }

                // Notify the client that the name has been accepted
                out.println("NAMEACCEPTED");
                // Add the client's PrintWriter to the writers set for broadcasting

                writers.add(out);

                while (true) {
                    // Read messages from the client and broadcast them to all clients
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        // Broadcast the message with the sender's name
                        writer.println("MESSAGE" + name + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // Remove the client's name and PrintWriter upon disconnection
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    // Handle socket closure error
                }
            }
        }
    }
}
