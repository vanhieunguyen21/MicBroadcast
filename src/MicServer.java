import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class MicServer extends Thread {
    // TCP Socket to handle clients
    private ServerSocket serverSocket;
    private final int serverPort = 8889;
    // List of streaming clients
    private List<ClientAddress> clients = new ArrayList<>();
    // This server's UDP Port
    private final int udpPort = 9000;
    // This server's UDP Socket
    private DatagramSocket datagramSocket = null;

    public MicServer() {
        try {
            serverSocket = new ServerSocket(serverPort);
            datagramSocket = new DatagramSocket(udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            while (true) {
                // A client connect to server
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted " + clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort());
                new Thread(() -> {
                    ClientAddress clientAddress = null;
                    try {
                        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                        // Receive udp port from client
                        int clientPort = (int) ois.readObject();
                        System.out.println(clientSocket.getInetAddress().toString() + " receive port: " + clientPort);
                        // Add client to streaming client list
                        clientAddress = new ClientAddress(clientSocket.getInetAddress(), clientPort);
                        clients.add(clientAddress);

                        // Waiting for client to disconnect
                        while (true) {
                            int cmd = (int) ois.readObject();
                            if (cmd == 0) {
                                // If a client disconnect, remove client from client streaming list
                                for (ClientAddress address : clients){
                                    if (address.port == clientPort && address.address.equals(clientSocket.getInetAddress())){
                                        clients.remove(address);
                                        break;
                                    }
                                }
                                clientSocket.close();
                            }
                        }
                    } catch (Exception e) {
                        // If a client connection have error, remove client from client streaming list
                        e.printStackTrace();
                        clients.remove(clientAddress);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to send data to streaming clients
    // Called by MicPlayer object
    public void send(byte[] data, int len) {
        // Go through list of clients
        for (ClientAddress address : clients) {
            if (datagramSocket != null) {
                // Send packet to each client
                DatagramPacket packet = new DatagramPacket(data, len, address.address, address.port);
                try {
                    datagramSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ClientAddress {
        public final InetAddress address;
        public final int port;

        public ClientAddress(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }
    }
}
