package bif3.swe1.seb;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Semaphore;

public class BattleGrounds {
    //teilnehmerliste username+score
    private static final ConcurrentSkipListMap<String, Integer> tournamentList = new ConcurrentSkipListMap<>();
    //startzeit/endzeit
    private LocalDateTime tournamentTime = LocalDateTime.now().minusMinutes(2);
    private String lastLeader;
    private Integer tournamentsStarted;
    private boolean tournamentStatus = false;
    private final Semaphore sem = new Semaphore(1);
    private List<String> log = new ArrayList<>();

    public BattleGrounds() {
        lastLeader = "";
        tournamentsStarted = 0;
        tournamentList.clear();
    }

    public String getStatus() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tournamentTime.isAfter(LocalDateTime.now())) {
            int participants = tournamentList.size();
            setLeader();
            sem.release();
            return "Tournament running, " + participants + " participant(s). " + this.lastLeader + " leads with " + tournamentList.get(lastLeader) + " push-ups.";
        }
        if (tournamentStatus && tournamentTime.isBefore(LocalDateTime.now())) {
            endTournament();
        }
        String returnString = tournamentsStarted + " tournament(s) played.";
        if (!lastLeader.equals("")) {
            returnString += " " + lastLeader + " won the last one";
        }
        sem.release();
        return returnString;
    }

    private void setLeader() {
        var entries = tournamentList.entrySet();
        StringBuilder leading = new StringBuilder();
        int max = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : entries) {
            if (entry.getValue().intValue() > max) {
                max = entry.getValue();
                leading = new StringBuilder(entry.getKey());
            } else if (entry.getValue().intValue() == max) {
                leading.append(" tied with ").append(entry.getKey());
            }
        }
        this.lastLeader = leading.toString();
    }

    //pushups hinzufügen
    public void addPushups(String username, Integer pushups) {
        //check if tournament running
        if (this.tournamentTime.isBefore(LocalDateTime.now())) {
            startTournament();
        }
        //enter tournament
        if (tournamentList.containsKey(username)) {
            Integer allPushups = tournamentList.get(username) + pushups;
            tournamentList.replace(username, allPushups);
        }
        //add pushups to entry
        else {
            tournamentList.put(username, pushups);
        }
        log.add(username + " entered " + pushups + " pushups.\n");

    }

    //start tournament
    public void startTournament() {
        try {
            this.sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tournamentTime.isBefore(LocalDateTime.now())) {
            this.endTournament();
            this.setLeader();
            this.tournamentTime = LocalDateTime.now().plusMinutes(2);
            this.tournamentsStarted++;
            this.tournamentStatus = true;
            log.add("New tournament started." + tournamentsStarted + "tournaments started since server start.\n");
        }
        this.sem.release();

    }

    //end tournament
    private void endTournament() {
        if (!tournamentList.isEmpty()) {
            var tournamentEndList = tournamentList.entrySet();
            bif3.swe1.seb.DBHandler.battleUpdate(tournamentEndList);
            log.add("Tournament ended.\n");
            log.add(lastLeader + "won the tournament\n");
            tournamentList.clear();
            System.out.println(log.toString());
            log.clear();
        }
    }

    protected void finalize() {
        endTournament();
    }


}
