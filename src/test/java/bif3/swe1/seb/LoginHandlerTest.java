package bif3.swe1.seb;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LoginHandlerTest {

    //test valid Login
    @Test
    void loginUserTestValid() {
        //Arrange
        LoginHandler testHandler = new LoginHandler();
        try (MockedStatic<DBHandler> mock = Mockito.mockStatic(DBHandler.class)) {
            mock.when(() -> DBHandler.loginUser("true", "pwd"))
                    .thenReturn(true);
            //Act + Assert
            assertTrue(testHandler.loginUser("true", "pwd"));
        }
    }

    //test invalid login
    @Test
    void loginUserTestFail() {
        //Arrange
        LoginHandler testHandler = new LoginHandler();
        try (MockedStatic<DBHandler> mock = Mockito.mockStatic(DBHandler.class)) {
            mock.when(() -> DBHandler.loginUser("true", "pwd"))
                    .thenReturn(true);
            //Act + Assert
            assertFalse(testHandler.loginUser("true", "pwds"));
            assertFalse(testHandler.loginUser("false", "pwd"));
        }
    }

    //get logged in user
    @Test
    void getAuthorizedUserTestValid() {
        //Arrange
        LoginHandler testHandler = new LoginHandler();
        try (MockedStatic<DBHandler> mock = Mockito.mockStatic(DBHandler.class)) {
            mock.when(() -> DBHandler.loginUser("true", "pwd"))
                    .thenReturn(true);
            testHandler.loginUser("true", "pwd");
            //Act
            String test1 = testHandler.getAuthorizedUser("Basic true-sebToken");
            boolean test2 = testHandler.getAuthStatus("Basic true-sebToken");
            //Assert
            assertTrue(test2);
            assertEquals("true", test1);
        }
    }

    //user not logged in
    @Test
    void getAuthorizedUserTestFail() {
        //Arrange
        LoginHandler testHandler = new LoginHandler();
        try (MockedStatic<DBHandler> mock = Mockito.mockStatic(DBHandler.class)) {
            mock.when(() -> DBHandler.loginUser("true", "pwd"))
                    .thenReturn(true);
            testHandler.loginUser("true", "pwd");
            //Act
            String test1 = testHandler.getAuthorizedUser("Basic false-sebToken");
            boolean test2 = testHandler.getAuthStatus("Basic false-sebToken");
            //Assert
            assertFalse(test2);
            assertNull(test1);
        }
    }
}