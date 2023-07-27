import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static List<ClientHandler> clientHandlers = new ArrayList<>();
    private static Map<String, List<ClientHandler>> topicSubscribersMap = new HashMap<>();

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
    public static void broadcastMessageByTopic(String topic, String message, ClientHandler sender) {
        List<ClientHandler> subscribers = topicSubscribersMap.get(topic);
        if (subscribers != null) {
            for (ClientHandler client : subscribers) {
                if (client != sender) {
                    client.sendMessage(message);
                }
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
        private String topic;
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

        public String getTopic() {
            return topic;
        }

        @Override
        public void run() {
            try {
                // Read the client type (PUBLISHER or SUBSCRIBER)
                String clientType = bufferedReader.readLine();
                this.topic = bufferedReader.readLine();

                if (clientType.equalsIgnoreCase("SUBSCRIBER")) {
                    // Add this subscriber to the topicSubscribersMap
                    List<ClientHandler> subscribers = topicSubscribersMap.getOrDefault(topic, new ArrayList<>());
                    subscribers.add(this);
                    topicSubscribersMap.put(topic, subscribers);
                    isSubscriber = true;
                }

                while (true) {
                    String message = bufferedReader.readLine();

                    if (message != null) {
                        System.out.println("Received message from " + clientSocket + ": "+topic + message);

                        if (isSubscriber) {
                            // Subscribers do not send messages
                            System.out.println("Client is a subscriber. Ignoring the message.");
                        } else {
                            // Publishers' messages are broadcasted to all subscribers
                            broadcastMessageByTopic(topic,message, this);
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
