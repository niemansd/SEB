import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class RequestContextTest {

    @Test
    void testMapKeys() {
        //Arrange
        var testERR = new RequestContext();
        var testERR2 = new RequestContext();
        var testCorrectInput = new RequestContext();
        var testString = "Server: Kiste\r\n"
                + "Content-Type: text/plain\r\n"
                + "Accept-Ranges: bytes \r\n"
                + "Content-Length: 0 \r\n\r\n";
        //Act
        //Falsche Anzahl an Argumenten
        testERR.setHeaderLines("POST HTTP \r\n" + testString);
        //Ungültige Methode
        testERR.setHeaderLines("PUSH / HTTP/1.1\r\n" + testString);
        testCorrectInput.setHeaderLines("POST /test HTTP/1.1\r\n" + testString);


        //Assert
        var correctMap = new HashMap<String, String>();
        correctMap.put("server", "Kiste");
        correctMap.put("content-type", "text/plain");
        correctMap.put("accept-ranges", "bytes");
        correctMap.put("content-lenght", "0");
        Assertions.assertEquals(correctMap.size(), testERR.getKeyMap().size());
        Assertions.assertEquals(correctMap.size(), testCorrectInput.getKeyMap().size());
        Assertions.assertEquals("Kiste", testERR.getKeyMap().get("server"));
        Assertions.assertEquals("text/plain", testERR.getKeyMap().get("content-type"));
        Assertions.assertEquals("bytes", testERR.getKeyMap().get("accept-ranges"));
        Assertions.assertEquals("0", testERR.getKeyMap().get("content-length"));
        Assertions.assertEquals("Kiste", testCorrectInput.getKeyMap().get("server"));
        Assertions.assertEquals("text/plain", testCorrectInput.getKeyMap().get("content-type"));
        Assertions.assertEquals("bytes", testCorrectInput.getKeyMap().get("accept-ranges"));
        Assertions.assertEquals("0", testCorrectInput.getKeyMap().get("content-length"));
        Assertions.assertEquals("/test", testCorrectInput.getPath());
        Assertions.assertEquals("POST", testCorrectInput.getMethod());
        Assertions.assertEquals("HTTP/1.1", testCorrectInput.getProtocol());
        Assertions.assertEquals(null, testERR2.getPath());
        Assertions.assertEquals(null, testERR2.getMethod());
        Assertions.assertEquals(null, testERR2.getProtocol());
        Assertions.assertEquals("ERR", testERR.getPath());
        Assertions.assertEquals("ERR", testERR.getMethod());
        Assertions.assertEquals("ERR", testERR.getProtocol());

    }

    @Test
    void constructorTest() {
        //Arrange
        //Act
        RequestContext constructTester = new RequestContext("POST /messages HTTP/1.1\ntest: true\n test2: false");
        //Assert
        Assertions.assertEquals("POST",constructTester.getMethod());
        Assertions.assertEquals("/messages", constructTester.getPath());
        Assertions.assertEquals("HTTP/1.1", constructTester.getProtocol());
        Assertions.assertEquals("true", constructTester.getKeyMap().get("test"));
        Assertions.assertEquals("false", constructTester.getKeyMap().get("test2"));

    }
}