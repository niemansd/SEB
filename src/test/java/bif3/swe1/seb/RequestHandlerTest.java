package bif3.swe1.seb;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestHandlerTest {
    private String requestType = "";
    private String contentType = "";
    private String requestPath = "";
    private String requestContent = "";
    private String authorisation = "";
    private bif3.swe1.seb.BattleGrounds arena = null;
    private String response = "";

    @Mock
    LoginHandler mockLogin;

    //Add user
    @Test
    void addUserTest() {
        //Arrange
        requestType = "POST";
        requestPath = "/users";
        contentType = "application/json";
        JSONObject helper = new JSONObject();
        helper.put("Username", "tester");
        helper.put("Password", "12345");
        requestContent = helper.toJSONString();
        RequestHandler testHandler = new RequestHandler(requestType, requestPath, requestContent, null, null);
        testHandler.setContentType("application/json");
        try (MockedStatic<DBHandler> mock = Mockito.mockStatic(DBHandler.class)) {
            try (MockedStatic<MessageHandler> mockMSG = Mockito.mockStatic(MessageHandler.class)) {
                mockMSG.when(() -> MessageHandler.createHttpResponseMessage("201 CREATED", "tester added.")).thenReturn("success");
                mock.when(() -> DBHandler.addUser("tester", "12345")).thenReturn(1);
                //Act
                String testOutput = testHandler.work();
                //Assert

                assertEquals("success", testOutput);
            }
        }
    }

//    //Change User
//    @Test
//    void changeUserTest() {
//        //Arrange
//        requestType = "PUT";
//        requestPath = "/users/tester";
//        contentType = "application/json";
//        JSONObject helper = new JSONObject();
//        helper.put("Name", "TEST");
//        helper.put("Bio", "BAM");
//        helper.put("Image", ":D");
//        requestContent = helper.toJSONString();
//        RequestHandler testHandler = new RequestHandler(requestType, requestPath, requestContent, null, mockLogin);
//        testHandler.setContentType("application/json");
//        testHandler.setAuthorisation("TOKEN");
//        try (MockedStatic<DBHandler> mock = Mockito.mockStatic(DBHandler.class)) {
//            try (MockedStatic<MessageHandler> mockMSG = Mockito.mockStatic(MessageHandler.class)) {
//                when(mockLogin.getAuthorizedUser("TOKEN")).thenReturn("tester");
//                mockMSG.when(() -> MessageHandler.createHttpResponseMessage("200")).thenReturn("success");
//                mock.when(() -> DBHandler.changeProfile("tester", "TEST", "BAM", ":D")).thenReturn(true);
//                //Act
//                String testOutput = testHandler.work();
//                //Assert
//
//                assertEquals("success", testOutput);
//            }
//        }
//    }

//    //Get User Data
//    @Test
//    void getUserDataTest() {
//    }
//
//    //Get Statistics
//    @Test
//    void getUserStats() {
//    }
//
//    //Get Tournament Info
//    @Test
//    void getTournamentInfo() {
//    }
}