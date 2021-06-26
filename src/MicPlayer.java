import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.sound.sampled.*;

public class MicPlayer extends Thread{
    private AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    private TargetDataLine microphone;
    //SourceDataLine speaker;
    private Multicaster multicaster;

    public MicPlayer(Multicaster multicaster) {
        this.multicaster = multicaster;
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

//            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
//            speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
//            speaker.open(format);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        microphone.start();
        byte[] buffer = new byte[1024];
//        speaker.start();
        while (true) {
            int byteRead = microphone.read(buffer, 0, 1024);
            multicaster.send(buffer, byteRead);
//            speaker.write(buffer, 0, byteRead);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Multicaster multicaster = new Multicaster();
        MicPlayer micPlayer = new MicPlayer(multicaster);
        micPlayer.start();

        Client client1 = new Client();
        client1.start();
    }
}
