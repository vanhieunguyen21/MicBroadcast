import java.io.IOException;
import java.net.*;

public class Multicaster {
    private DatagramSocket socket = null;
    private final int port = 8888;
    private InetAddress IP;

    public Multicaster() {
        try {
            IP = InetAddress.getByName("224.0.0.4");
            socket = new DatagramSocket();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data, int len){
        if (socket != null){
            DatagramPacket packet = new DatagramPacket(data, len, IP, port);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
