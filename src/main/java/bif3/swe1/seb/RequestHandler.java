package bif3.swe1.seb;

import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RequestHandler {
    //private String requestUser = null;
    private String requestType = "";
    @Setter
    private String contentType = "";
    private String requestPath = "";
    private String requestContent = "";
    @Setter
    private String authorisation = "";
    private bif3.swe1.seb.BattleGrounds arena;
    private String response = "";
    private bif3.swe1.seb.LoginHandler loginHandler;

    private JSONParser parser = new JSONParser();

    RequestHandler(String rType, String rPath, String rContent, bif3.swe1.seb.BattleGrounds mainArena, bif3.swe1.seb.LoginHandler loginHandler) {
        this.requestType = rType.trim();
        this.requestPath = rPath.trim();
        this.requestContent = rContent.trim();
        this.arena = mainArena;
        this.loginHandler = loginHandler;
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
                return bif3.swe1.seb.MessageHandler.badRequest();
        }
        return response;
    }

    private void post() {
        //read JSON
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj = (JSONObject) parser.parse(requestContent);
        } catch (ParseException e) {
            response = bif3.swe1.seb.MessageHandler.badRequest();
        }
        //register users
        if (requestPath.equalsIgnoreCase("/users")) {
            //check if content is sent
            if (contentType.equalsIgnoreCase("application/json")) {
                //add user in database + create response
                assert jsonObj != null;
                if (bif3.swe1.seb.DBHandler.addUser(jsonObj.get("Username").toString(), jsonObj.get("Password").toString()) != 0) {
                    response = bif3.swe1.seb.MessageHandler.createHttpResponseMessage("201", jsonObj.get("Username") + " added.");
                } else {
                    response = bif3.swe1.seb.MessageHandler.badRequest();
                }
            } else {
                response = bif3.swe1.seb.MessageHandler.badRequest();
            }
        }
        //login users
        else if (requestPath.equalsIgnoreCase("/sessions")) {
            if (contentType.equalsIgnoreCase("application/json")) {
                //add user in database + create response
                assert jsonObj != null;
                if (loginHandler.loginUser(jsonObj.get("Username").toString(), jsonObj.get("Password").toString())) {
                    //eventuell token zurückgeben
                    response = bif3.swe1.seb.MessageHandler.createHttpResponseMessage("200", jsonObj.get("Username") + " login success.");
                } else {
                    response = bif3.swe1.seb.MessageHandler.badRequest();
                }
            } else {
                response = bif3.swe1.seb.MessageHandler.badRequest();
            }
        }
        //add entry
        else if (requestPath.equalsIgnoreCase("/history")) {
            //check authorisation Authorization: Basic kienboec-sebToken
            if (loginHandler.getAuthStatus(authorisation)) {
                String username = loginHandler.getAuthorizedUser(authorisation);
                if (contentType.equalsIgnoreCase("application/json")) {
                    //add user in database + create response
                    assert jsonObj != null;
                    //JSON umwandeln "{\"Name\": \"PushUps\",  \"Count\": 40, \"DurationInSeconds\": 60}"
                    //String exerciseType = (String) jsonObj.get("Name");
                    Long count = (Long) jsonObj.get("Count");
                    Long duration = (Long) jsonObj.get("DurationInSeconds");
                    if (bif3.swe1.seb.DBHandler.addPushups(username, count, duration) != 0) {
                        response = bif3.swe1.seb.MessageHandler.createHttpResponseMessage("201", username + " did " + count + " push-ups in " + duration + " seconds.");
                    } else {
                        response = bif3.swe1.seb.MessageHandler.badRequest();
                    }
                } else {
                    response = bif3.swe1.seb.MessageHandler.badRequest();
                }
            }
        } else {
            response = bif3.swe1.seb.MessageHandler.badRequest();
        }
    }

    private void get() {
        //get user data
        if (requestPath.toLowerCase().startsWith("/users")) {
            String username = requestPath.replace("/users/", "");
            //check authorisation "Authorization: Basic kienboec-sebToken"
            if (username == loginHandler.getAuthorizedUser(authorisation)) {
                //DB-Abfrage und Antwort
                response = bif3.swe1.seb.MessageHandler.createHttpResponseMessage("200", bif3.swe1.seb.DBHandler.getUser(username));
            } else {
                response = bif3.swe1.seb.MessageHandler.badRequest();
            }
        }
        //get stats of single user(elo,overall pushups)
        else if (requestPath.equalsIgnoreCase("/stats")) {
            //GET http://localhost:10001/stats --header "Authorization: Basic kienboec-sebToken"
            //get username from token:
            if (loginHandler.getAuthStatus(authorisation)) {
                String username = loginHandler.getAuthorizedUser(authorisation);
                //DB Abfrage: elo, total pushups
                response = bif3.swe1.seb.DBHandler.getUserStats(username);
            } else response = bif3.swe1.seb.MessageHandler.badRequest();
        }
        //get scoreboard of all users
        else if (requestPath.equalsIgnoreCase("/score")) {
            //GET http://localhost:10001/score --header "Authorization: Basic kienboec-sebToken"
            //todo check Auth-Token

            //return scoreboard
            JSONArray scoreBoard = new JSONArray();
            JSONArray scores = bif3.swe1.seb.DBHandler.getScoreBoard();
            if (scores != null) {
                scoreBoard = scores;
            }
            response = scoreBoard.toJSONString();
        }
        //get count and duration of entries
        else if (requestPath.equalsIgnoreCase("/history")) {
            //GET http://localhost:10001/history --header "Authorization: Basic kienboec-sebToken"
            //todo check Auth-Token
            String username = loginHandler.getAuthorizedUser(authorisation);
            JSONArray history = new JSONArray();
            history = bif3.swe1.seb.DBHandler.getUserHistory(username);
            response = history.toJSONString();
        }
        //get tournament status
        else if (requestPath.equalsIgnoreCase("/tournament") && loginHandler.getAuthStatus(authorisation)) {
            //GET http://localhost:10001/tournament --header "Authorization: Basic kienboec-sebToken"
            //needs BattleGrounds
            response = bif3.swe1.seb.MessageHandler.createHttpResponseMessage("200", arena.getStatus());
        } else {
            response = bif3.swe1.seb.MessageHandler.badRequest();
        }
    }

    private void put() {
        //change misc user data
        if (requestPath.toLowerCase().startsWith("/users")) {
            //PUT http://localhost:10001/users/kienboec --header "Content-Type: application/json" --header "Authorization: Basic kienboec-sebToken"
            // -d "{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}"
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj = (JSONObject) parser.parse(requestContent);
            } catch (ParseException e) {
                response = bif3.swe1.seb.MessageHandler.badRequest();
            }
            if (bif3.swe1.seb.DBHandler.changeProfile(loginHandler.getAuthorizedUser(authorisation), jsonObj.get("Name").toString(), jsonObj.get("Bio").toString(), jsonObj.get("Image").toString())) {
                response = bif3.swe1.seb.MessageHandler.createHttpResponseMessage("200");
            }
        } else {
            response = bif3.swe1.seb.MessageHandler.badRequest();
        }
    }
}
