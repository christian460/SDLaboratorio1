import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private String server, username;
    private int port;

    Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch(Exception ec) {
            System.out.println("Error connecting: " + ec);
            return false;
        }

        System.out.println("Connected to server");

        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            System.out.println("Error creating streams: " + eIO);
            return false;
        }

        new ListenFromServer().start();

        try {
            sOutput.writeObject(username);
        } catch (IOException eIO) {
            System.out.println("Login error: " + eIO);
            disconnect();
            return false;
        }

        return true;
    }

    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch(IOException e) {
            System.out.println("Error sending message");
        }
    }

    private void disconnect() {
        try { if(sInput != null) sInput.close(); } catch(Exception e) {}
        try { if(sOutput != null) sOutput.close(); } catch(Exception e) {}
        try { if(socket != null) socket.close(); } catch(Exception e) {}
    }

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        System.out.print("Enter username: ");
        String userName = scan.nextLine();

        Client client = new Client("localhost", 1500, userName);

        if(!client.start()) return;

        while(true) {
            System.out.print("> ");
            String msg = scan.nextLine();

            if(msg.equalsIgnoreCase("LOGOUT")) {
                client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
                break;
            } else if(msg.equalsIgnoreCase("WHOISIN")) {
                client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
            } else {
                client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
            }
        }

        client.disconnect();
        scan.close();
    }

    class ListenFromServer extends Thread {
        public void run() {
            while(true) {
                try {
                    String msg = (String) sInput.readObject();
                    System.out.println(msg);
                    System.out.print("> ");
                } catch(Exception e) {
                    System.out.println("Server closed");
                    break;
                }
            }
        }
    }
}