import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBConnection {
    //TODO
    //  connect
    public static boolean connect(){
        return false;
    }
    //  User anlegen
    public static boolean addUser(String username, String password){
        if (!username.equals("") && !password.equals("")){

        }
        return false;
    }
    //  Login
    public static boolean loginUser(String username, String password){
        return false;
    }
    //  Passwort ändern
    public static boolean changePWD(String username, String oldPass, String newPass){
        return false;
    }
    //  Profil ändern
    public static boolean changeProfile(String username, String bio, String image){
        return false;
    }
    //  User abfragen
    public static String getUser(String username){
        return "";
    }
    //  Alle User abfragen
    public static void getUser(){

    }
    //  Ergebnisse speichern
    public static void battleUpdate(Set<Map.Entry<String, Integer>> tournamentEndList) {

    }

    public static boolean addPushups(String username, Integer count, Integer duration) {
        return false;
    }

    public static String getUserStats(String username) {
        return username;
    }

    public static List<Map.Entry<String, int[]>> getScoreBoard() {
        //return: Username, [ELO, PushUps]
        return null;
    }

    public static List<int[]> getUserHistory(String username) {
        //return format [pushups, duration]
        return null;
    }
}
