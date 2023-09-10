// Import necessary libraries
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Define the ChatClient class
public class ChatClient {

    // Declare instance variables
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter App");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);

    // Constructor for ChatClient
    public ChatClient() {
        // Set text field and message area as non-editable initially
        textField.setEditable(false);
        messageArea.setEditable(false);

        // Add text field to the North and message area with scrolling to the Center of the frame
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");

        // Pack the frame to set its size
        frame.pack();

        // Define an ActionListener for the text field
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Send the text in the text field to the server and clear the text field
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    // Method to get the server address from the user
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter Server Address:",
                "Server Address",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    // Method to get the user's name
    private String getName() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter Your Name:",
                "Name",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    // Method to run the chat client
    private void run() throws IOException {
        // Get the server address from the user
        String serverAddress = getServerAddress();

        // Create a socket to connect to the server
        Socket socket = new Socket(serverAddress, 9001);

        // Initialize input and output streams for communication with the server
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Continuous loop for handling incoming server messages
        while (true) {
            // Read a line from the server
            String line = in.readLine();

            // Check the type of message received from the server
            if (line.startsWith("SUBMITNAME")) {
                // If the server requests a name, send the user's name to the server
                out.println(getName());
            } else if (line.startsWith("NAMEACCEPTED")) {
                // If the server accepts the name, enable text field for user input
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                // If the server sends a message, append it to the message area
                messageArea.append(line.substring(8) + "\n");
            }
        }
    }

    // Main method to create and run the ChatClient
    public static void main(String args[]) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
