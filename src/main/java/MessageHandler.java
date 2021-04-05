import java.nio.charset.StandardCharsets;

public class MessageHandler {
    //TODO umbau zu Nachrichtschreiber

    public MessageHandler() {
    }

    public static String createHttpResponseMessage(String httpCode) {
        return createHttpResponseMessage(httpCode, "");
    }

    public static String createHttpResponseMessage(String httpCode, String payload) {
        byte[] s = payload.getBytes(StandardCharsets.UTF_8);
        return "HTTP/1.1 " + httpCode + "\r\n"
                + "Server: Kiste\r\n"
                + "Content-Type: text/plain\r\n"
                + "Accept-Ranges: bytes\r\n"
                + "Content-Length: " + s.length + "\r\n\r\n"
                + payload;
    }

    public static String badRequest() {
        return createHttpResponseMessage("400 BAD REQUEST");
    }
}
