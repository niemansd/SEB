import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class MainServer implements Runnable {

    private static ServerSocket _listener = null;
    //MessageHandler verwaltet Nachrichten und erstellt Antworten
    private static final MessageHandler messageOPs = new MessageHandler();

    public MainServer() {
    }

    public static void main(String[] args) {
        System.out.println("start server");

        try {
            _listener = new ServerSocket(10001, 5);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new MainServer()));

        try {
            while (true) {
                Socket client = _listener.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                RequestContext keySafe = new RequestContext();
                String message;
                StringBuilder header = new StringBuilder();
                do {
                    //find method, path and keypairs
                    message = reader.readLine().trim();
                    //KeyPairs zwischenspeichern
                    header.append(message).append("\n");
                    System.out.println("srv: received: " + message);
                } while (!message.isEmpty());
                String response;
                if (keySafe.setHeaderLines(header.toString())) {
                    StringBuilder content = new StringBuilder();
                    while (reader.ready()) {
                        content.append((char) reader.read());
                    }
                    System.out.println(content + "\ncontent end");
                    response = messageOPs.handleMessages(keySafe.getMethod(), content.toString(), keySafe.getPath());
                } else
                    response = MessageHandler.createHttpResponseMessage("400 BAD REQUEST");
                System.out.println("responding");
                writer.write(response);
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            _listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        _listener = null;
        System.out.println("close server");
    }
}