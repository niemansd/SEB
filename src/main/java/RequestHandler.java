import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Map;

public class RequestHandler {
    //private String requestUser = null;
    private String requestType;
    @Setter
    private String contentType;
    private String requestPath;
    private String requestContent;
    @Setter
    private String authorisation;
    private BattleGrounds arena;
    private String response = null;

    private JSONParser parser = new JSONParser();

    RequestHandler(String rType, String rPath, String rContent, BattleGrounds mainArena) {
        this.requestType = rType.trim();
        //this.contentType = cType.trim();
        this.requestPath = rPath.trim();
        this.requestContent = rContent.trim();
        //this.authorisation = rAuth.trim();
        this.arena = mainArena;
    }

    public String work() {
        //todo handling
        switch (requestType.toUpperCase()) {
            case "POST":
                post();
                break;
            case "PUT":
                put();
                break;
            case "GET":
                get();
                break;
            default:
                return MessageHandler.badRequest();
        }
        return response;
    }

    private void post() {
        //read JSON
        JSONObject jsonObj = null;
        try {
            jsonObj = (JSONObject) parser.parse(requestContent);
        } catch (ParseException e) {
            response = MessageHandler.badRequest();
        }
        //register users
        if (requestPath.equalsIgnoreCase("/users")) {
            //check if content is sent
            if (contentType.equalsIgnoreCase("application/json")) {
                //add user in database + create response
                assert jsonObj != null;
                if (DBConnection.addUser(jsonObj.get("Username").toString(), jsonObj.get("Password").toString())) {
                    response = MessageHandler.createHttpResponseMessage("201", jsonObj.get("Username") + " added.");
                } else {
                    response = MessageHandler.badRequest();
                }
            } else {
                response = MessageHandler.badRequest();
            }
        }
        //login users
        else if (requestPath.equalsIgnoreCase("/sessions")) {
            if (contentType.equalsIgnoreCase("application/json")) {
                //add user in database + create response
                assert jsonObj != null;
                if (DBConnection.loginUser(jsonObj.get("Username").toString(), jsonObj.get("Password").toString())) {
                    //eventuell token zurückgeben
                    response = MessageHandler.createHttpResponseMessage("200", jsonObj.get("Username") + " login success.");
                } else {
                    response = MessageHandler.badRequest();
                }
            } else {
                response = MessageHandler.badRequest();
            }
        }
        //add entry
        else if (requestPath.equalsIgnoreCase("/history")) {
            //check authorisation Authorization: Basic kienboec-sebToken
            if (authorisation.startsWith("Basic ") && authorisation.endsWith("sebToken")) {
                String username = authorisation.replace("Basic", "").replace("-sebToken", "").trim();
                if (contentType.equalsIgnoreCase("application/json")) {
                    //add user in database + create response
                    assert jsonObj != null;
                    //JSON umwandeln "{\"Name\": \"PushUps\",  \"Count\": 40, \"DurationInSeconds\": 60}"
                    //String exerciseType = (String) jsonObj.get("Name");
                    Integer count = (Integer) jsonObj.get("Count");
                    Integer duration = (Integer) jsonObj.get("DurationInSeconds");
                    if (DBConnection.addPushups(username, count, duration)) {
                        response = MessageHandler.createHttpResponseMessage("201", username + " did " + count + " push-ups in " + duration + " seconds.");
                    } else {
                        response = MessageHandler.badRequest();
                    }
                } else {
                    response = MessageHandler.badRequest();
                }
            }
        } else {
            response = MessageHandler.badRequest();
        }
    }

    private void get() {
        //get user data
        if (requestPath.toLowerCase().startsWith("/users")) {
            String username = requestPath.replace("/users/", "");
            //check authorisation "Authorization: Basic kienboec-sebToken"
            String token = "Basic " + username + "-sebToken";
            if (this.authorisation.trim().equals(token.trim())) {
                //DB-Abfrage und Antwort
                response = MessageHandler.createHttpResponseMessage("200", DBConnection.getUser(username));
            }
            else {
                response = MessageHandler.badRequest();
            }
        }
        //get stats of single user(elo,overall pushups)
        else if (requestPath.equalsIgnoreCase("/stats")) {
            //GET http://localhost:10001/stats --header "Authorization: Basic kienboec-sebToken"
            //get username from token:
            String username = authorisation.replace("Basic", "").replace("-sebToken", "").trim();
            //DB Abfrage: elo, total pushups
            response = DBConnection.getUserStats(username);
        }
        //get scoreboard of all users
        else if (requestPath.equalsIgnoreCase("/score")) {
            //GET http://localhost:10001/score --header "Authorization: Basic kienboec-sebToken"
            //todo check Auth-Token
            //return scoreboard
            JSONArray scoreBoard = new JSONArray();
            List<Map.Entry<String, int[]>> scores = DBConnection.getScoreBoard();
            assert scores != null;
            for (Map.Entry<String, int[]> score : scores
            ) {
                JSONObject jEntry = new JSONObject();
                jEntry.put("Username", score.getKey());
                var values = score.getValue();
                jEntry.put("ELO", values[0]);
                jEntry.put("Push-Ups", values[1]);
                scoreBoard.add(jEntry);
            }
            response = scoreBoard.toJSONString();
        }
        //get count and duration of entries
        else if (requestPath.equalsIgnoreCase("/history")) {
            //GET http://localhost:10001/history --header "Authorization: Basic kienboec-sebToken"
            //todo check Auth-Token
            String username = authorisation.replace("Basic", "").replace("-sebToken", "").trim();
            JSONArray history = new JSONArray();
            List<int[]> entries = DBConnection.getUserHistory(username);
            assert entries != null;
            for (int[] entry : entries) {
                JSONObject jEntry = new JSONObject();
                jEntry.put("Push-Ups", entry[0]);
                jEntry.put("duration", entry[1]);
                history.add(jEntry);
            }
            response = history.toJSONString();
        }
        //get tournament status
        else if (requestPath.equalsIgnoreCase("/tournament")) {
            //GET http://localhost:10001/tournament --header "Authorization: Basic kienboec-sebToken"
            //todo check Token
            //needs BattleGrounds
            response = MessageHandler.createHttpResponseMessage("200", arena.getStatus());
        } else {
            response = MessageHandler.badRequest();
        }
    }

    private void put() {
        //change misc user data
        if (requestPath.toLowerCase().startsWith("/users")) {
            //PUT http://localhost:10001/users/kienboec --header "Content-Type: application/json" --header "Authorization: Basic kienboec-sebToken"
            // -d "{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}"
            JSONObject jsonObj = null;
            try {
                jsonObj = (JSONObject) parser.parse(requestContent);
            } catch (ParseException e) {
                response = MessageHandler.badRequest();
            }
            assert jsonObj != null;
            if (DBConnection.changeProfile(jsonObj.get("Name").toString(), jsonObj.get("Bio").toString(), jsonObj.get("Image").toString())) {
                response = MessageHandler.createHttpResponseMessage("200");
            }
        } else {
            response = MessageHandler.badRequest();
        }
    }
}
