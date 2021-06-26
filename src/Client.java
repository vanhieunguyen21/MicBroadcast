import javax.sound.sampled.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class Client extends Thread {
    private AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    private SourceDataLine speaker;

    private Socket tcp;
    private int serverPort = 8889;

    private DatagramSocket udp;
    private int udpPort;

    public Client(int udpPort) {
        this.udpPort = udpPort;
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speaker.open(format);

            tcp = new Socket(InetAddress.getByName("localhost"), serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(tcp.getOutputStream());
            System.out.println("Sending data port");
            oos.writeObject(udpPort);

            udp = new DatagramSocket(udpPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            speaker.start();
            byte[] data = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(data, 1024);
                udp.receive(packet);
                speaker.write(packet.getData(), 0, packet.getLength());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
