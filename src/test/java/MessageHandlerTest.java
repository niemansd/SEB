import lombok.var;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class MessageHandlerTest {
    String standardResponseFirst = "HTTP/1.1 ";
    String standardRespnseRest = "Server: Kiste\r\n"
            + "Content-Type: text/plain\r\n"
            + "Accept-Ranges: bytes \r\n"
            + "Content-Length: ";
    String headerEnd = "\r\n\r\n";
    String code200 = "200 OK";
    String code201 = "201 CREATED";
    String code400 = "400 BAD REQUEST";
    String code404 = "404 NOT FOUND";
    MessageHandler test;


    @BeforeEach
    void setUp() {
        test = new MessageHandler();
        test.messageMap.clear();
        test.messageMap.put(1, "TEST");
        test.messageMap.put(3, "TEST3");
        test.messageMap.put(4, "TEST4");
        test.messageMap.put(5, "TEST5");
        test.messageCount.set(6);
    }

    @Test
    void createHttpResponseMessage() {
        //Act
        var actualResponse = MessageHandler.createHttpResponseMessage("123 TEST");
        //Assert
        var correctResponse = standardResponseFirst + "123 TEST\r\n"
                + standardRespnseRest + "0\r\n\r\n";
        Assertions.assertEquals(correctResponse, actualResponse);
    }

    @Test
    void testCreateHttpResponseMessage() {
        //Arrange
        int length = "Das ist eine Testnachricht".getBytes(StandardCharsets.UTF_8).length;
        var correctResponse = standardResponseFirst + "123 TEST\r\n"
                + standardRespnseRest + length + headerEnd
                + "Das ist eine Testnachricht";
        //Act
        var actualResponse = MessageHandler.createHttpResponseMessage("123 TEST", "Das ist eine Testnachricht");

        //Assert
        Assertions.assertEquals(correctResponse, actualResponse);

    }

    @Test
    void testPostMessage() {
        //Arrange
        var testString = "test";
        var length = "7\r\n".getBytes(StandardCharsets.UTF_8).length;
        var correctResponse = standardResponseFirst + code201 + "\r\n"
                + standardRespnseRest + length + headerEnd
                + "7\r\n";
        //Act
        var actualResponse = test.handleMessages("POST", testString, "/messages");
        //Assert
        Assertions.assertEquals(correctResponse, actualResponse);
        Assertions.assertEquals(7, test.messageCount.get());
        Assertions.assertEquals(testString, test.messageMap.get(7));
    }

    @Test
    void testGetMessage() {
        //Arrange
        var testString = "test";
        var length = "Message 3: TEST3\r\n".getBytes(StandardCharsets.UTF_8).length;
        var correctResponse = standardResponseFirst + code200 + "\r\n"
                + standardRespnseRest + length + headerEnd
                + "Message 3: TEST3\r\n";
        var correctResponse2 = standardResponseFirst + code404 + "\r\n"
                + standardRespnseRest + 0 + headerEnd;
        //Act
        var actualResponse = test.handleMessages("GET", testString, "/messages/3");
        var actualResponseERR = test.handleMessages("GET", testString, "/messages/2");
        //Assert
        Assertions.assertEquals(correctResponse, actualResponse);
        Assertions.assertEquals(correctResponse2, actualResponseERR);
    }

    @Test
    void testGetAllMessages() {
        //Arrange
        var testString = "test";
        var responsePayload = "Message 1: TEST\r\n"
                + "Message 3: TEST3\r\n"
                + "Message 4: TEST4\r\n"
                + "Message 5: TEST5\r\n";
        var length = responsePayload.getBytes(StandardCharsets.UTF_8).length;
        var correctResponse = standardResponseFirst + code200 + "\r\n"
                + standardRespnseRest + length + headerEnd
                + responsePayload;
        //Act
        var actualResponse = test.handleMessages("GET", testString, "/messages");
        //Assert
        Assertions.assertEquals(null, test.messageMap.get(7));
        Assertions.assertEquals(correctResponse, actualResponse);
    }

    @Test
    void testPutMessage() {
        //Arrange
        var testString = "test";
        var correctResponse = standardResponseFirst + code201 + "\r\n"
                + standardRespnseRest + 0 + headerEnd;
        var correctResponseERR = standardResponseFirst + code404 + "\r\n"
                + standardRespnseRest + 0 + headerEnd;
        var correctResponseERR2 = standardResponseFirst + code400 + "\r\n"
                + standardRespnseRest + 0 + headerEnd;
        //Act
        var actualResponse = test.handleMessages("PUT", testString, "/messages/1");
        var actualResponseERR = test.handleMessages("PUT", testString, "/messages/2");
        var actualResponseERR2 = test.handleMessages("PUT", testString, "/messages/");
        //Assert
        Assertions.assertEquals(correctResponse, actualResponse);
        Assertions.assertEquals(correctResponseERR, actualResponseERR);
        Assertions.assertEquals(correctResponseERR2, actualResponseERR2);
    }

    @Test
    void testDeleteMessage() {
        //Arrange
        var testString = "test";
        var length = 0;
        var correctResponse = standardResponseFirst + code200 + "\r\n"
                + standardRespnseRest + length + headerEnd;
        var correctResponseERR = standardResponseFirst + code404 + "\r\n"
                + standardRespnseRest + length + headerEnd;
        //Act
        var actualResponse = test.handleMessages("DELETE", testString, "/messages/1");
        var actualResponseERR = test.handleMessages("DELETE", testString, "/messages/1");
        var actualResponseERR2 = test.handleMessages("DELETE", testString, "/messages");
        //Assert
        Assertions.assertEquals(correctResponse, actualResponse);
        Assertions.assertEquals(correctResponseERR, actualResponseERR);
        Assertions.assertEquals(correctResponseERR, actualResponseERR2);
    }

    @Test
    void handleMessagesERR() {
        //Arrange
        var testString = "test";
        var length = 0;
        var correctResponse = standardResponseFirst + code400+"\r\n"
                + standardRespnseRest + length + headerEnd;
        //Act
        //Teste fehlerhaften Pfad
        var actualResponse = test.handleMessages("GET", testString, "/");
        var actualResponse2 = test.handleMessages("PUT", testString, "/");
        var actualResponse3 = test.handleMessages("POST", testString, "/");
        var actualResponse4 = test.handleMessages("DELETE", testString, "/");
        //Teste falschen Befehl
        var actualResponse5 = test.handleMessages("DEL", testString, "/messages");
        //Assert
        Assertions.assertEquals(correctResponse, actualResponse);
        Assertions.assertEquals(correctResponse, actualResponse2);
        Assertions.assertEquals(correctResponse, actualResponse3);
        Assertions.assertEquals(correctResponse, actualResponse4);
    }

}