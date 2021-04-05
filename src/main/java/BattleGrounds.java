import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Semaphore;

public class BattleGrounds {
    //teilnehmerliste username+score
    private static final ConcurrentSkipListMap<String, Integer> tournamentList = new ConcurrentSkipListMap<>();
    //startzeit/endzeit
    private LocalDateTime tournamentTime = LocalDateTime.now();
    private String lastLeader = "";
    private Integer tournamentsStarted = 0;
    private final Semaphore sem = new Semaphore(1);

    public String getStatus() {
        if (tournamentTime.isBefore(LocalDateTime.now())) {
            int participants = tournamentList.size();
            setLeader();
            return this.lastLeader + " leads with " + participants + " push-ups.";
        }
        return tournamentsStarted + " tournaments played. " + lastLeader + " won the last one";
    }

    private void setLeader() {
        var entries = tournamentList.entrySet();
        String leading = "";
        int max = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : entries) {
            if (entry.getValue().intValue() > max) {
                max = entry.getValue();
                leading = entry.getKey();
            } else if (entry.getValue().intValue() == max) {
                leading += " tied with " + entry.getKey();
            }
        }
        this.lastLeader = leading;
    }

    //pushups hinzufügen
    public void addPushups(String username, Integer pushups) {
        //check if tournament running
        if (this.tournamentTime.isBefore(LocalDateTime.now())) {
            startTournament();
        }
        //enter tournament
        if (!tournamentList.containsKey(username)) {
            tournamentList.put(username, pushups);
        }
        //add pushups to entry
        else {
            Integer allPushups = tournamentList.get(username) + pushups;
            tournamentList.replace(username, allPushups);
        }
    }

    //start tournament
    public void startTournament() {
        if (tournamentTime.isBefore(LocalDateTime.now())) {
            try {
                this.sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.endTournament();
            this.setLeader();
            this.tournamentTime = LocalDateTime.now().plusMinutes(2);
            this.tournamentsStarted++;
            this.sem.release();
        }

    }

    //end tournament
    private void endTournament() {
        if (!tournamentList.isEmpty()) {
            var tournamentEndList = tournamentList.entrySet();
            DBConnection.battleUpdate(tournamentEndList);
            tournamentList.clear();
        }
    }


}
