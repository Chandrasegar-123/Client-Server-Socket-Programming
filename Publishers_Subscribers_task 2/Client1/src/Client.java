import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Please provide the server IP, PORT, and client type (PUBLISHER or SUBSCRIBER) as command-line arguments.");
            return;
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String clientType = args[2];

        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            socket = new Socket(serverIP, serverPort);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            // Send the client type to the server
            bufferedWriter.write(clientType);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            if (clientType.equalsIgnoreCase("SUBSCRIBER")) {
                // Start a separate thread to listen for server messages
                Thread messageListenerThread = new Thread(new MessageListener(bufferedReader));
                messageListenerThread.start();
            }

            Scanner scanner = new Scanner(System.in);

            while (true) {
                String message = scanner.nextLine();
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                if (message.equalsIgnoreCase("terminate")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Separate thread to listen for server messages
    private static class MessageListener implements Runnable {
        private BufferedReader bufferedReader;

        public MessageListener(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = bufferedReader.readLine();

                    if (message != null) {
                        System.out.println("Publisher: " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
