package bif3.swe1.seb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BattleGroundsTest {

    //Get current tournament status ended

    @Test
    void getStatusTest() {
        //Arrange
        BattleGrounds statusTest = new BattleGrounds();
        //Act
        String status1 = statusTest.getStatus();

        //Assert
        assertEquals("0 tournament(s) played.", status1);

        statusTest = null;

    }

    //start tournament
    @Test
    void addPushupsTest() {
        //Arrange
        BattleGrounds statusTest = new BattleGrounds();
        //Act
        statusTest.addPushups("test1", 10);
        String status2 = statusTest.getStatus();
        statusTest.addPushups("test2", 5);
        String status3 = statusTest.getStatus();
        statusTest.addPushups("test3", 15);
        String status4 = statusTest.getStatus();
        statusTest.addPushups("test1", 10);
        String status5 = statusTest.getStatus();

        //Assert
        assertEquals("Tournament running, 1 participant(s). test1 leads with 10 push-ups.", status2);
        assertEquals("Tournament running, 2 participant(s). test1 leads with 10 push-ups.", status3);
        assertEquals("Tournament running, 3 participant(s). test3 leads with 15 push-ups.", status4);
        assertEquals("Tournament running, 3 participant(s). test1 leads with 20 push-ups.", status5);

        statusTest = null;
    }

//    @Test
//    void startTournamentTestEnding() {
//        //Arrange
//        BattleGrounds statusTest = new BattleGrounds();
//
//        //Act
//        String status1 = statusTest.getStatus();
//        try (MockedStatic<LocalDateTime> timeMock = Mockito.mockStatic(LocalDateTime.class)) {
//            timeMock.when(() -> LocalDateTime.now()).thenReturn(LocalDateTime.of(2021, 3, 30, 0, 0));
//            statusTest.addPushups("test", 10);
//            timeMock.when(() -> LocalDateTime.now()).thenReturn(LocalDateTime.of(2021, 3, 30, 0, 3));
//            String status2 = statusTest.getStatus();
//        }
//
//
//        //Assert
//        assertEquals("0 tournament(s) played.", status1);
//
//        assertEquals("1 tournament(s) played. test won the last one", status1);
//    }
}