import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.imageio.ImageIO;

import javax.swing.*;


public class GuiClient extends Thread {

    private final DatagramSocket socket = new DatagramSocket();
    private final ImagePanel imagePanel = new ImagePanel();
    private boolean stopClient = false;

    public GuiClient() throws SocketException {
        JFrame jFrame = new JFrame();
        jFrame.setSize(768, 480);
        jFrame.add(imagePanel);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    stopClient = true;
                }
            });
    }

    @Override
    public void run() {
        try {
            while (!stopClient) {
                DatagramPacket request = new DatagramPacket(new byte[1], 1, InetAddress.getByName("localhost"), 4445);
                socket.send(request);

                byte[] buffer = new byte[49092];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(packet.getData()));
                imagePanel.setImage(image);
                imagePanel.repaint();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            socket.close();
        }
    }

    public static class ImagePanel extends JPanel {

        private BufferedImage image;

        public void setImage(BufferedImage image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }

    }
}
