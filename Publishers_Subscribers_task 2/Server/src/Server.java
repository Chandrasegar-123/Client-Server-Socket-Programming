import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Please provide the server PORT as a command-line argument.");
            return;
        }

        int serverPort = Integer.parseInt(args[0]);

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Server started. Listening on port " + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new client handler for the connected client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);

                // Start the client handler thread
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    // Broadcasts a message from a publisher to all subscribers
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender && client.isSubscriber()) {
                client.sendMessage(message);
            }
        }
    }

    // Removes a client handler from the list
    public static void removeClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("Client disconnected: " + clientHandler.getClientSocket());
    }

    // ClientHandler class to handle individual client connections
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private boolean isSubscriber;

        public ClientHandler(Socket socket) {
            try {
                clientSocket = socket;
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        public boolean isSubscriber() {
            return isSubscriber;
        }

        public void sendMessage(String message) {
            try {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Read the client type (PUBLISHER or SUBSCRIBER)
                String clientType = bufferedReader.readLine();

                if (clientType.equalsIgnoreCase("PUBLISHER")) {
                    isSubscriber = false;
                } else if (clientType.equalsIgnoreCase("SUBSCRIBER")) {
                    isSubscriber = true;
                }

                while (true) {
                    String message = bufferedReader.readLine();

                    if (message != null) {
                        System.out.println("Received message from " + clientSocket + ": " + message);

                        if (isSubscriber) {
                            // Subscribers do not send messages
                            System.out.println("Client is a subscriber. Ignoring the message.");
                        } else {
                            // Publishers' messages are broadcasted to all subscribers
                            broadcastMessage(message, this);
                        }
                    } else {
                        // Null message indicates client disconnection
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Remove the client handler from the list
                removeClientHandler(this);
            }
        }
    }
}
