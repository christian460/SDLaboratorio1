import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {

    private ArrayList<ClientThread> clients;
    private int port;
    private boolean running;
    private SimpleDateFormat sdf;

    public Server(int port) {
        this.port = port;
        clients = new ArrayList<>();
        sdf = new SimpleDateFormat("HH:mm:ss");
    }

    public void start() {
        running = true;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server running on port " + port);

            while(running) {
                Socket socket = serverSocket.accept();
                ClientThread ct = new ClientThread(socket);
                clients.add(ct);
                ct.start();
            }

        } catch(IOException e) {
            System.out.println("Server error: " + e);
        }
    }

    private synchronized void broadcast(String message) {
        String time = sdf.format(new Date());
        String msg = time + " " + message;

        System.out.println(msg);

        for(int i = clients.size()-1; i >= 0; i--) {
            ClientThread ct = clients.get(i);
            if(!ct.writeMsg(msg)) {
                clients.remove(i);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(1500);
        server.start();
    }

    class ClientThread extends Thread {

        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        String username;

        ClientThread(Socket socket) {
            this.socket = socket;

            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());

                username = (String) sInput.readObject();
                broadcast(username + " joined");

            } catch(Exception e) {
                System.out.println("Error creating thread");
            }
        }

        public void run() {
            boolean keepGoing = true;

            while(keepGoing) {
                try {
                    ChatMessage cm = (ChatMessage) sInput.readObject();

                    switch(cm.getType()) {
                        case ChatMessage.MESSAGE:
                            broadcast(username + ": " + cm.getMessage());
                            break;

                        case ChatMessage.LOGOUT:
                            keepGoing = false;
                            break;

                        case ChatMessage.WHOISIN:
                            writeMsg("Connected users:");
                            for(ClientThread ct : clients) {
                                writeMsg(ct.username);
                            }
                            break;
                    }

                } catch(Exception e) {
                    break;
                }
            }

            close();
        }

        private void close() {
            try { socket.close(); } catch(Exception e) {}
        }

        private boolean writeMsg(String msg) {
            try {
                sOutput.writeObject(msg);
                return true;
            } catch(IOException e) {
                return false;
            }
        }
    }
}