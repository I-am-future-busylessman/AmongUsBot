import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
public class PlayersList{
    private ArrayList<User> players = new ArrayList<>();

    public long countAlive(){
        int counter = 0;
        for (int i = 0; i < players.size(); i++){
            if(players.get(i).getAlive()){
                counter++;
            }
        }
        return counter;
    }

    public User getPlayerByColor(String color){
        color = color.toLowerCase();
        String finalColor = color;
        if(players.stream().anyMatch(u -> u.getColor().equals(finalColor))) {
            String finalColor1 = color;
            return players.stream().filter(u -> u.getColor().equals(finalColor1)).findFirst().get();
        }
        return null;
    }

    public Boolean findPlayer(User user){
        boolean isMath = players.stream().anyMatch(u -> u.getChatID().equals(user.getChatID()));
        return isMath;
    }

    public User getUser(Long chatID){
        if(players.stream().anyMatch(u -> u.getChatID().equals(chatID))){
            User user = players.stream().filter(u -> u.getChatID().equals(chatID)).findFirst().get();
            return user;
        }
        return null;
    }

    public void addPlayer(User user){
        if (!findPlayer(user)) {
            players.add(user);
        }
    }
}
