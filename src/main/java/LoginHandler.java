import java.util.HashMap;
import java.util.Map;

public class LoginHandler {
    private final Map<String, String> userList;


    public LoginHandler() {
        userList = new HashMap<>();
    }

    public boolean loginUser(String username, String password) {
        if (DBHandler.loginUser(username, password)) {
            if (!userList.containsValue(username)) {
                userList.put("Basic " + username + "-sebToken", username);
            }
            return true;
        }
        return false;
    }

    public String getAuthorizedUser(String authToken) {
        return userList.get(authToken.trim());
    }

    public boolean getAuthStatus(String authToken) {
        return userList.containsKey(authToken.trim());
    }
}
