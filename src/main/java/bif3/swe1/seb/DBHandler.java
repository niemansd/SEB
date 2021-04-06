package bif3.swe1.seb;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBHandler {
    //TODO
    //  connect
    private static Connection connect()
            throws ClassNotFoundException, SQLException {
        /*
         * Register the PostgreSQL JDBC driver.
         * This may throw a ClassNotFoundException.
         */
        Class.forName("org.postgresql.Driver");
        /*
         * Tell the driver manager to connect to the database specified with the URL.
         * This may throw an SQLException.
         */
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "test");
    }

    //  User anlegen
    public static int addUser(String username, String password) {
        if (!username.isBlank() && !password.isBlank()) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement insertUser = connect().prepareStatement(query)) {
                insertUser.setString(1, username);
                insertUser.setString(2, password);
                int returnValue = insertUser.executeUpdate();
                connect().close();
                return returnValue;
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
        return 0;
    }

    //  Login
    public static boolean loginUser(String username, String password) {
        if (!username.isBlank() && !password.isBlank()) {
            String query = "SELECT password FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement login = connect().prepareStatement(query)) {
                login.setString(1, username);
                login.setString(2, password);
                var resultSet = login.executeQuery();
                resultSet.next();
                String dbPW = resultSet.getString("password");
                connect().close();
                return dbPW.equals(password);
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
    public static boolean changeProfile(String username, String name, String bio, String image) {
        if (username != null) {
            if (!username.isBlank()) {
                String query = "UPDATE Users SET name = ?, bio = ?, image = ? WHERE username = ?";
                try (PreparedStatement changeProfile = connect().prepareStatement(query)) {
                    changeProfile.setString(1, name);
                    changeProfile.setString(2, bio);
                    changeProfile.setString(3, image);
                    changeProfile.setString(4, username);
                    var result = changeProfile.executeUpdate();
                    connect().close();
                    return result == 1;
                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                }
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
                result.next();
                int columns = result.getMetaData().getColumnCount();
                for (int i = 0; i < columns; i++) {
                    jResObj.put(result.getMetaData().getColumnLabel(i + 1).toLowerCase(), result.getObject(i + 1));
                }
                connect().close();
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
        List<String> leader = new ArrayList<>();
        List<String> losers = new ArrayList<>();
        Integer maximum = 0;
        for (Map.Entry<String, Integer> stringIntegerEntry : tournamentEndList) {

            if (stringIntegerEntry.getValue() > maximum) {
                for (String member : leader) {
                    losers.add(member);
                    leader.remove(member);
                }
                leader.add(stringIntegerEntry.getKey());
            } else if (stringIntegerEntry.getValue() == maximum) {
                leader.add(stringIntegerEntry.getKey());
            } else {
                losers.add(stringIntegerEntry.getKey());
            }
        }
        for (String username : leader) {
            String query = "UPDATE Users SET elo = elo + ? where username = ?";
            try (PreparedStatement updateELO = connect().prepareStatement(query)) {
                updateELO.setInt(1, 2);
                updateELO.setString(2, username);
                updateELO.executeUpdate();
                connect().close();
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
        for (String username : losers) {
            String query = "UPDATE Users SET elo = elo - ? where username = ?";
            try (PreparedStatement updateELO = connect().prepareStatement(query)) {
                updateELO.setInt(1, 2);
                updateELO.setString(2, username);
                updateELO.executeUpdate();
                connect().close();
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static int addPushups(String username, Long count, Long duration) {
        String query = "INSERT INTO pushups (username, count, duration) VALUES (?, ?, ?)";
        try (PreparedStatement addPushUps = connect().prepareStatement(query)) {
            addPushUps.setString(1, username);
            addPushUps.setLong(2, count);
            addPushUps.setLong(3, duration);
            int returnValue = addPushUps.executeUpdate();
            connect().close();
            return returnValue;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public static String getUserStats(String username) {
        String query1 = "SELECT elo FROM Users WHERE username = ?";
        String query2 = "SELECT count FROM pushups WHERE username = ?";
        try (PreparedStatement getELO = connect().prepareStatement(query1);
             PreparedStatement getCount = connect().prepareStatement(query2)) {
            getELO.setString(1, username);
            getCount.setString(1, username);
            var result = getELO.executeQuery();
            result.next();
            JSONObject jResObj = new JSONObject();
            jResObj.put(result.getMetaData().getColumnLabel(1).toLowerCase(), result.getObject(1));
            result = getCount.executeQuery();
            Integer pushupCount = 0;
            while (result.next()) {
                pushupCount += result.getInt("count");
            }
            jResObj.put("Push-Up count", pushupCount);
            connect().close();
            return MessageHandler.createHttpResponseMessage("200", jResObj.toJSONString());
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return MessageHandler.badRequest();
    }

    public static JSONArray getScoreBoard() {
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
            connect().close();
            return jsonArray;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static JSONArray getUserHistory(String username) {
        //return [pushups, duration]
        JSONArray history = new JSONArray();
        if (!username.isBlank()) {
            String query = "SELECT count, duration FROM pushups WHERE username = ?";
            try (PreparedStatement getHistory = connect().prepareStatement(query)) {
                getHistory.setString(1, username);
                var result = getHistory.executeQuery();
                while (result.next()) {
                    int columns = result.getMetaData().getColumnCount();
                    JSONObject jResObj = new JSONObject();
                    for (int i = 0; i < columns; i++) {
                        jResObj.put(result.getMetaData().getColumnLabel(i + 1).toLowerCase(), result.getObject(i + 1));
                    }
                    history.add(jResObj);
                }
                connect().close();
                return history;
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
        return history;
    }
}
