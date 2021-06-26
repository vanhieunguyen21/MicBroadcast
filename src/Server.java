import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Server extends Thread {
    // Socket to handle clients
    private ServerSocket serverSocket;
    private final int serverPort = 8889;
    private final int udpPort = 9000;
    private List<ClientAddress> clients = new ArrayList<>();
    private HashMap<InetAddress, Integer> addressPortMap = new HashMap<>();

    private DatagramSocket socket = null;

    public Server() {
        try {
            serverSocket = new ServerSocket(serverPort);
            socket = new DatagramSocket(udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted " + clientSocket.getInetAddress().toString()+":"+clientSocket.getPort());
                new Thread(() -> {
                    ClientAddress clientAddress = null;
                    try {
                        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                        int clientPort = (int) ois.readObject();
                        System.out.println(clientSocket.getInetAddress().toString()+" receive port: "+clientPort);
                        clientAddress = new ClientAddress(clientSocket.getInetAddress(), clientPort);
                        clients.add(clientAddress);

                        while (true) {
                            int cmd = ois.readInt();
                            if (cmd == 0) {
                                clients.remove(clientAddress);
                                clientSocket.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        clients.remove(clientAddress);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data, int len) {
        for (ClientAddress address : clients) {
            if (socket != null) {
                DatagramPacket packet = new DatagramPacket(data, len, address.address, address.port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ClientAddress{
        public final InetAddress address;
        public final int port;

        public ClientAddress(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientAddress that = (ClientAddress) o;
            return port == that.port && address.equals(that.address);
        }
    }
}
