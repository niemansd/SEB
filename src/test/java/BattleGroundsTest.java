import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BattleGroundsTest {

    //Get current tournament status ended
    @Test
    void getStatusTest() {
        //Arrange
        BattleGrounds statusTest = null;
        statusTest = new BattleGrounds();
        //Act
        String status1 = statusTest.getStatus();
        statusTest.addPushups("test1", 10);
        String status2 = statusTest.getStatus();
        statusTest.addPushups("test2", 5);
        String status3 = statusTest.getStatus();
        statusTest.addPushups("test3", 15);
        String status4 = statusTest.getStatus();

        //Assert
        assertEquals("0 tournaments played.", status1);

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
        assertEquals("test1 leads with 10 push-ups", status2);
        assertEquals("test1 leads with 10 push-ups", status3);
        assertEquals("test3 leads with 15 push-ups", status4);
        assertEquals("test1 leads with 20 push-ups", status5);
    }


    @Test
    void startTournamentTest() {
    }
}