import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket server;
    private Socket client;
    private final int porta;
    private List<DataOutputStream> clientsStreams;

    public Server() {
        server = null;
        client = null;
        porta = 1234;
        clientsStreams = new ArrayList<DataOutputStream>();
    }
    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        @Override
        public void run () {
            try {
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());

                clientsStreams.add(serverOutput);

                while (true) {
                    String message = clientInput.readLine();
                    System.out.println("Messaggio ricevuto da client: " + message);
                    for (DataOutputStream stream : clientsStreams) {
                        if(stream != serverOutput) {
                            stream.writeBytes(message + "\n");
                        }
                    }
                    serverOutput.flush();
                }
            } catch (IOException e) {
                System.out.println("Comunicazione interrotta con il client.");
            }
        }
    }

    public void attendi() {
        try {
            server = new ServerSocket(porta);
            server.setReuseAddress(true);
            System.out.println("Server in attesa di connessioni.");

            while(true) {
                client = server.accept();
                System.out.println("Nuova connessione: " + client);

                Thread clientHandler = new Thread(new ClientHandler(client));
                clientHandler.start();
            }

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Errore durante l'istanza del server.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.attendi();
    }
}