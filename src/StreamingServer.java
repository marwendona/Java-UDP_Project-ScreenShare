import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.imageio.ImageIO;


public class StreamingServer extends Thread {

    private final DatagramSocket socket = new DatagramSocket(4445);

    public StreamingServer() throws Exception {
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                BufferedImage image = takeScreenshot();
                ByteArrayOutputStream outputStream = writeScreenshotInOutputStream(image);

                DatagramPacket request = new DatagramPacket(new byte[1], 1);
                socket.receive(request);

                DatagramPacket packet = new DatagramPacket(outputStream.toByteArray(), outputStream.size(), request.getAddress(), request.getPort());
                socket.send(packet);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void interrupt() {
        socket.close();
    }

    private static ByteArrayOutputStream writeScreenshotInOutputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        return outputStream;
    }

    private static BufferedImage takeScreenshot() throws AWTException {
        Robot robot = new Robot();
        Rectangle rectangle = new Rectangle(new Dimension(768, 480));
        return robot.createScreenCapture(rectangle);
    }
}
