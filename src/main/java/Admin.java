import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Admin {
    private String kill = "Admin";
    private String makeAlive = "Admin";
    private long chatId = 394615109;

    public void setAdmin(long chatId){
        this.chatId = chatId;
    }
}
