import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBConnection {
    //TODO
    //  connect
    private static java.sql.Connection connect()
            throws ClassNotFoundException, java.sql.SQLException {
        /*
         * Register the PostgreSQL JDBC driver.
         * This may throw a ClassNotFoundException.
         */
        Class.forName("org.postgresql.Driver");
        /*
         * Tell the driver manager to connect to the database specified with the URL.
         * This may throw an SQLException.
         */
        return java.sql.DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "test");
    }

    //  User anlegen
    public static boolean addUser(String username, String password) {
        if (!username.isBlank() && !password.isBlank()) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement insertUser = connect().prepareStatement(query)) {
                insertUser.setString(1, username);
                insertUser.setString(2, password);
                return insertUser.execute();
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    //  Login
    public static boolean loginUser(String username, String password) {
        if (!username.isBlank() && !password.isBlank()) {
            String query = "SELECT password FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement login = connect().prepareStatement(query)) {
                login.setString(1, username);
                login.setString(2, password);
                var resultSet = login.executeQuery();
                return resultSet.getString("password").equals(password);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    //  Passwort ändern
    public static boolean changePWD(String username, String oldPass, String newPass) {
        //TODO
        return false;
    }

    //  Profil ändern
    public static boolean changeProfile(String username, String bio, String image) {
        if (!username.isBlank()) {
            String query = "UPDATE Users SET bio = ?, image = ? WHERE username = ?";
            try (PreparedStatement changeProfile = connect().prepareStatement(query)) {
                changeProfile.setString(1, bio);
                changeProfile.setString(2, image);
                changeProfile.setString(3, username);
                var result = changeProfile.executeUpdate();
                return result > 1;
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    //  User abfragen
    public static String getUser(String username) {
        if (!username.isBlank()) {
            String query = "SELECT image, bio FROM Users WHERE username = ?";
            try (PreparedStatement changeProfile = connect().prepareStatement(query)) {
                changeProfile.setString(1, username);
                var result = changeProfile.executeQuery();
                JSONObject jResObj = new JSONObject();
                int columns = result.getMetaData().getColumnCount();
                for (int i = 0; i < columns; i++) {
                    jResObj.put(result.getMetaData().getColumnLabel(i + 1).toLowerCase(), result.getObject(i + 1));
                }
                return MessageHandler.createHttpResponseMessage("200", jResObj.toJSONString());
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
        return MessageHandler.badRequest();
    }

    //  Alle User abfragen
    public static void getUser() {

    }

    //  Ergebnisse speichern
    public static void battleUpdate(Set<Map.Entry<String, Integer>> tournamentEndList) {

    }

    public static boolean addPushups(String username, Integer count, Integer duration) {
        return false;
    }

    public static String getUserStats(String username) {
        if (!username.isBlank()) {
            String query1 = "SELECT elo FROM Users WHERE username = ?";
            String query2 = "SELECT count FROM pushups WHERE username = ?";
            try (PreparedStatement getELO = connect().prepareStatement(query1);
                 PreparedStatement getCount = connect().prepareStatement(query2)) {
                getELO.setString(1, username);
                getCount.setString(1, username);
                var result = getELO.executeQuery();
                JSONObject jResObj = new JSONObject();
                jResObj.put(result.getMetaData().getColumnLabel(1).toLowerCase(), result.getObject(1));
                result = getCount.executeQuery();
                Integer pushupCount = null;
                while (result.next()) {
                    pushupCount += result.getInt("count");
                }
                jResObj.put("Push-Up count", result);
                return MessageHandler.createHttpResponseMessage("200", jResObj.toJSONString());
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
        return MessageHandler.badRequest();
    }

    public static List<Map.Entry<String, int[]>> getScoreBoard() {
        //return: Username, [ELO, PushUps]
        String query1 = "select username, elo, from users";
        String query2 = "select sum(count) from users";
        try (PreparedStatement userStatSelect = connect().prepareStatement(query1);
             PreparedStatement userTotalCountSelect = connect().prepareStatement(query2)) {
            var result = userStatSelect.executeQuery();
            JSONArray jsonArray = new JSONArray();

            while (result.next()) {
                int columns = result.getMetaData().getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i = 0; i < columns; i++) {
                    obj.put(result.getMetaData().getColumnLabel(i + 1).toLowerCase(), result.getObject(i + 1));
                }
                userTotalCountSelect.setString(1, (String) obj.get("username"));
                var resultCount = userTotalCountSelect.executeQuery();
                obj.put(result.getMetaData().getColumnLabel(1).toLowerCase(), result.getObject(1));
                jsonArray.add(obj);
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static List<int[]> getUserHistory(String username) {
        //return format [pushups, duration]
        return null;
    }
}
