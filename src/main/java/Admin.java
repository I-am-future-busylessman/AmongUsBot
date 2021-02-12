import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Admin {
    private String kill = "Admin";
    private String makeAlive = "Admin";
    private ArrayList<Long> chatId = new ArrayList<>();
    private String changeColor = "null";

    public Admin(){
        this.chatId.add(394615109L);
    }
    public void setAdmin(long chatId){
        this.chatId.clear();
        this.chatId.add(chatId);
    }

    public void addAdmin(long chatId){
        this.chatId.add(chatId);
    }
}
