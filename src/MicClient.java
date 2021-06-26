import javax.sound.sampled.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class MicClient extends Thread {
    // Define audio format for mic streaming
    private AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    // Speaker to play audio
    private SourceDataLine speaker;

    // TCP Socket to send and receive some data
    private Socket tcp;
    private int serverPort = 8889;

    // UDP Socket to receive stream audio
    private DatagramSocket udp;
    private int udpPort;

    public MicClient(int udpPort) {
        this.udpPort = udpPort;
        try {
            // Set up speaker
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speaker.open(format);

            // Set up tcp port and send udp port
            tcp = new Socket(InetAddress.getByName("192.168.12.8"), serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(tcp.getOutputStream());
            oos.writeObject(udpPort);

            // Set up udp port to receive stream data
            udp = new DatagramSocket(udpPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Start streaming
            speaker.start();
            // Buffer to read into
            byte[] data = new byte[1024];
            while (true) {
                // Read buffer and send it to speaker
                DatagramPacket packet = new DatagramPacket(data, 1024);
                udp.receive(packet);
                speaker.write(packet.getData(), 0, packet.getLength());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
