package bif3.swe1.seb;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class RequestContext {
    private final AtomicReference<String> headerLines = new AtomicReference<>();
    @Getter
    private HashMap<String, String> keyMap;
    @Getter
    @Setter
    private String method;
    @Getter
    @Setter
    private String path;
    @Getter
    private String protocol;

    public RequestContext() {
    }

    public RequestContext(String input) {
        this.headerLines.set(input);
        mapKeys();
    }

    public boolean setHeaderLines(String headerLines) {
        this.headerLines.set(headerLines);
        mapKeys();
        return !method.equals("ERR") && !path.equals("ERR") && !protocol.equals("ERR");
    }

    public void mapKeys() {
        String[] lines = headerLines.get().split("\n");
        this.keyMap = new HashMap<>();
        for (String line : lines) {
            if (line.contains(":")) {
                String[] pair = line.split(":");
                this.keyMap.put(pair[0].toLowerCase().trim(), pair[1].trim());
            }
            if (line.startsWith("POST") || line.startsWith("GET") || line.startsWith("PUT") || line.startsWith("DELETE")) {
                String[] request = line.trim().split(" ");
                if (request.length == 3) {
                    this.method = request[0].trim();
                    this.path = request[1].trim();
                    this.protocol = request[2].trim();
                } else {
                    this.method = "ERR";
                    this.path = "ERR";
                    this.protocol = "ERR";
                }
            }
        }
    }
}
