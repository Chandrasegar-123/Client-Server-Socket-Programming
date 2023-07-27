import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Please provide the server PORT as a command-line argument.");
            return;
        }

        int serverPort = Integer.parseInt(args[0]);

        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        ServerSocket serverSocket = null;

        serverSocket = new ServerSocket(serverPort);

        while (true) { // accept new client connection
            try {
                socket = serverSocket.accept();
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                while (true) { // read message from client
                    String message = bufferedReader.readLine();
                    System.out.println("Client: " + message);
                    bufferedWriter.write("Message Received. ");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    if (message.equalsIgnoreCase("terminate")) {
                        break;
                    }
                }

                socket.close();
                inputStreamReader.close();
                outputStreamWriter.close();
                bufferedReader.close();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}