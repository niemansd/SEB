import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageHandler {
    static final ConcurrentSkipListMap<Integer, String> messageMap = new ConcurrentSkipListMap<>();
    static final AtomicInteger messageCount = new AtomicInteger(0);

    public MessageHandler() {
    }

    private static String postMessage(String content) {
        String httpCode;
        String messageCountString = "";
        if (content.isEmpty())
            httpCode = "204 NO CONTENT";
        else {
            httpCode = "201 CREATED";
            final int newMessageCount = messageCount.addAndGet(1);
            messageCountString = newMessageCount + "\r\n";
            messageMap.put(newMessageCount, content);
        }
        return createHttpResponseMessage(httpCode, messageCountString);
    }

    private static String getMessage(String path) {
        StringBuilder response = new StringBuilder();
        if (messageMap.isEmpty()) {
            return createHttpResponseMessage("404 NOT FOUND");
        } else if (path.equals("/messages")) {
            //alle messages auslesen
            Integer i = messageMap.firstKey();
            while (i != null) {
                response.append("Message ").append(i).append(": ").append(messageMap.get(i)).append("\r\n");
                i = messageMap.higherKey(i);
            }
        } else {
            String messageNumber = path.replace("/messages/", "").trim();
            try {
                Integer entry = Integer.parseInt(messageNumber);
                //get message # X
                if (messageMap.get(entry) != null)
                    response.append("Message ").append(entry).append(": ").append(messageMap.get(entry)).append("\r\n");
                else
                    return createHttpResponseMessage("404 NOT FOUND");
            } catch (NumberFormatException e) {
                return createHttpResponseMessage("400 BAD REQUEST");
            }
        }
        return createHttpResponseMessage("200 OK", response.toString());
    }

    private static String putMessage(String content, String path) {
        if (path.startsWith("/messages/") && !path.replace("/messages/", "").isEmpty()) {
            int messageNumber = Integer.parseInt(path.replace("/messages/", ""));
            if (messageMap.containsKey(messageNumber)) {
                messageMap.put(messageNumber, content);
            } else
                return createHttpResponseMessage("404 NOT FOUND");
        } else
            return createHttpResponseMessage("400 BAD REQUEST");
        return createHttpResponseMessage("201 CREATED");
    }

    private static String deleteMessage(String path) {
        if (path.startsWith("/messages/") && !path.replace("/messages/", "").isEmpty()) {
            int messageNumber = Integer.parseInt(path.replace("/messages/", ""));
            if (messageMap.containsKey(messageNumber)) {
                messageMap.remove(messageNumber);
            } else
                return createHttpResponseMessage("404 NOT FOUND");
        } else
            return createHttpResponseMessage("404 NOT FOUND");
        return createHttpResponseMessage("200 OK");
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

    public String handleMessages(String requestType, String requestContent, String requestPath) {
        String reply;
        if (requestPath.startsWith("/messages"))
            switch (requestType) {
                case "POST":
                    reply = postMessage(requestContent);
                    break;
                case "GET":
                    reply = getMessage(requestPath);
                    break;
                case "PUT":
                    reply = putMessage(requestContent, requestPath);
                    break;
                case "DELETE":
                    reply = deleteMessage(requestPath);
                    break;
                default:
                    reply = createHttpResponseMessage("400 BAD REQUEST");
            }
        else reply = createHttpResponseMessage("400 BAD REQUEST");
        return reply;
    }
}
