import javax.sound.sampled.*;

public class MicPlayer extends Thread{
    // Define audio format for mic streaming
    private AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    // Audio source: microphone
    private TargetDataLine microphone;
    // Server to send data to clients
    private MicServer micServer;

    public MicPlayer(MicServer micServer) {
        this.micServer = micServer;
        try {
            // Set up microphone audio source
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        microphone.start();
        // Buffer to write data to
        byte[] buffer = new byte[1024];
        while (true) {
            // Read data from microphone
            int byteRead = microphone.read(buffer, 0, 1024);
            // Send it to clients
            micServer.send(buffer, byteRead);
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        Server server = new Server();
//        server.start();
//        MicPlayer micPlayer = new MicPlayer(server);
//        micPlayer.start();

        MicClient micClient1 = new MicClient(8900);
        micClient1.start();

        MicClient micClient2 = new MicClient(8901);
        micClient2.start();

        MicClient micClient3 = new MicClient(8902);
        micClient3.start();
    }
}
