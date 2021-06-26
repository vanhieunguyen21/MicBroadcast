import javax.sound.sampled.*;
import java.net.*;

public class Client extends Thread {
    private AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    private SourceDataLine speaker;

    private MulticastSocket socket = null;
    private int port = 8888;
    private InetAddress IP;

    public Client() {
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speaker.open(format);

            socket = new MulticastSocket(port);
            IP = InetAddress.getByName("224.0.0.4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            speaker.start();
            byte[] data = new byte[1024];
            socket.joinGroup(IP);
            while (true) {
                DatagramPacket packet = new DatagramPacket(data, 1024);
                socket.receive(packet);
                System.out.println(packet.getLength());
                speaker.write(packet.getData(), 0, packet.getLength());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
