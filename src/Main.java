public class Main {

    public static void main(String[] args) throws Exception {
        StreamingServer streamingServer = new StreamingServer();
        streamingServer.start();

        GuiClient guiClient = new GuiClient();
        guiClient.start();

        guiClient.join();
        streamingServer.interrupt();
    }
}
