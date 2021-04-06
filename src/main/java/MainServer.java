import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class MainServer implements Runnable {

    private static ServerSocket _listener = null;
    //arena für Kampfaustragung
    private static final BattleGrounds arena = new BattleGrounds();
    //todo user logedin for Auth-Token

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
                RequestContext requestHeader = new RequestContext();
                String message;
                StringBuilder header = new StringBuilder();
                do {
                    //find method, path and keypairs
                    message = reader.readLine().trim();
                    //KeyPairs zwischenspeichern
                    header.append(message).append("\n");
                    System.out.println("srv: received: " + message);
                } while (!message.isEmpty());
                String response = "";
                if (requestHeader.setHeaderLines(header.toString())) {
                    StringBuilder content = new StringBuilder();
                    while (reader.ready()) {
                        content.append((char) reader.read());
                    }
                    System.out.println("Content:\n" + content + "\ncontent end");
                    RequestHandler requestHandler = new RequestHandler(requestHeader.getMethod(),
                            requestHeader.getPath(), content.toString(), arena);
                    if (requestHeader.getKeyMap().containsKey("authorization")) {
                        requestHandler.setAuthorisation(requestHeader.getKeyMap().get("authorization"));
                    }
                    if (requestHeader.getKeyMap().containsKey("content-type")) {
                        requestHandler.setContentType(requestHeader.getKeyMap().get("content-type"));
                    }
                    response = requestHandler.work();
                    //
                } else
                    response = MessageHandler.createHttpResponseMessage("400 BAD REQUEST");
                System.out.println("responding:");
                System.out.println(response);
                System.out.println("");
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