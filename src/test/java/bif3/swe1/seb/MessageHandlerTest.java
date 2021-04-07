package bif3.swe1.seb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class MessageHandlerTest {
    String standardResponseFirst = "HTTP/1.1 ";
    String standardRespnseRest = "Server: Kiste\r\n"
            + "Content-Type: text/plain\r\n"
            + "Accept-Ranges: bytes\r\n"
            + "Content-Length: ";
    String headerEnd = "\r\n\r\n";
    String code200 = "200 OK";
    String code201 = "201 CREATED";
    String code400 = "400 BAD REQUEST";
    String code404 = "404 NOT FOUND";
    MessageHandler test;

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

}