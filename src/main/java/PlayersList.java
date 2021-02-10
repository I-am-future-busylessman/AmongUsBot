import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Getter
@Setter
public class PlayersList{
    private ArrayList<User> players = new ArrayList<>();

    public void reboot () {
        ArrayList<User> newPlayers = new ArrayList<>();
        for (User player:players) {
            player = new User(player);
            newPlayers.add(player);
        }
        players = newPlayers;
        System.out.println(newPlayers.toString());
    }

    public long countAlive(){
        int counter = 0;
        for (User player : players) {
            if (player.getAlive()) {
                counter++;
            }
        }
        return counter;
    }

    public User getPlayerByColor(String color){
        if(players.stream().anyMatch(u -> u.getColor().equals(color))) {
            return players.stream().filter(u -> u.getColor().equals(color)).findFirst().orElse(null);
        }
        return null;
    }

    public Boolean findPlayer(User user){
        return players.stream().anyMatch(u -> u.getChatId().equals(user.getChatId()));
    }

    public User getUser(Long chatID){
        if(players.stream().anyMatch(u -> u.getChatId().equals(chatID))){
            return players.stream().filter(u -> u.getChatId().equals(chatID)).findFirst().orElse(null);
        }
        return null;
    }

    public void addPlayer(User user){
        if (!findPlayer(user)) {
            players.add(user);
        }
    }

    public void shuffle(){
        Collections.shuffle(players);
    }
}
