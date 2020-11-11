import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@NoArgsConstructor
public class BotCore extends TelegramLongPollingBot {
    private final String botToken = "1232615498:AAEH96ct6OLYYZPY2Bd88lFbOhUAD09t_7g";
    private final String botName = "Space_mafia_bot";
    private Admin admin = new Admin();
    PlayersList players = new PlayersList();
    String gameStatus = "init";
    String sabotage = "v";
    boolean sabotageStatus = false;
    HashMap<String, Integer> voteResults = new HashMap<>();
    private Settings settings = new Settings(3, 2, 2, 2, 10, 2);
    String[] subStr;
    int voted = 0;
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        if(!update.getMessage().hasText()){
            sendMsg(update.getMessage().getChatId(), "Я понимаю только текст", null);
        }else if(message.equals("2020")){
            sabotageStatus = false;
            sendMsg(update.getMessage().getChatId(), "ты успешно починил саботаж", Keyboards.rolePanel(players.getUser(update.getMessage().getChatId()).getRole(),players.getUser(update.getMessage().getChatId()).getAlive()));
        }else if(gameStatus.equals("init") && update.getMessage().getChatId().toString().equals(admin.getChatId())) {
            adminBeforeStart(update, message);
        }else if(gameStatus.equals("init") && !update.getMessage().getChatId().toString().equals(admin.getChatId())) {
            playerBeforeStart(update, message);
        }else if(((gameStatus.equals("game")) || (gameStatus.equals("Саботаж")) || (gameStatus.equals("vote"))) && update.getMessage().getChatId().toString().equals(admin.getChatId())){
            adminInGame(update, message);
        }else if(gameStatus.equals("game") && players.getUser(update.getMessage().getChatId()).getRole().equals(false)){
            imposterInGame(update, message);
        }else if(gameStatus.equals("vote") && !players.getUser(update.getMessage().getChatId()).getVoted()){
            playersVote(update, message);
        }else if(gameStatus.equals("init") && update.getMessage().getChatId().toString().equals(admin.getChatId())) {
            playerBeforeStart(update, message);
        }else{
            sendMsg(update.getMessage().getChatId(), "Неизвестная команда", null);
        }
    }

    public void playerInGame(){

    }

    public void imposterInGame(Update update, String message){
        System.out.println(gameStatus);
        Date time = new Date();
        if (message.equals("Убить")){
            if (time.getTime() - players.getUser(update.getMessage().getChatId()).getKillTime() > settings.getImposterKD()*1000) {
                players.getUser(update.getMessage().getChatId()).setColorToKill(null);
                sendMsg(update.getMessage().getChatId(), "Кому снести башку?", Keyboards.votePanel(players));
            }else{
                sendMsg(update.getMessage().getChatId(), "Бластер ещё не перезаряжен", Keyboards.rolePanel(false, true));
            }
        }else if(players.getUser(update.getMessage().getChatId()).getColorToKill() == null){
            players.getUser(update.getMessage().getChatId()).setColorToKill(message);
            players.getUser(update.getMessage().getChatId()).setConfirmColorToKill(null);
            sendMsg(update.getMessage().getChatId(),"Забыл снять предохранитель, повтори", Keyboards.votePanel(players));
        }else if(players.getUser(update.getMessage().getChatId()).getConfirmColorToKill() == null){
            if (players.getUser(update.getMessage().getChatId()).getColorToKill().equals(message) && players.getPlayerByColor(message) != null){
                players.getPlayerByColor(message).setAlive(false);
                sendMsg(update.getMessage().getChatId(), message + " убит, валим", Keyboards.rolePanel(false, true));
                sendMsg(players.getPlayerByColor(message).getChatID(), "Вас убили, какая жалость", Keyboards.rolePanel(players.getPlayerByColor(message).getRole(), false));
                players.getUser(update.getMessage().getChatId()).setKillTime(time.getTime());
            }else{
                sendMsg(update.getMessage().getChatId(), "Не попал, салага", Keyboards.rolePanel(false, true));
            }
            players.getUser(update.getMessage().getChatId()).setConfirmColorToKill(message);
        }else if(message.equals("Саботаж")){
            if (time.getTime() - players.getUser(update.getMessage().getChatId()).getSabotageTime() > 60000 && !sabotageStatus) {
                sabotage = "Саботаж";
                sendMsg(update.getMessage().getChatId(), "Что хочешь сломать?", Keyboards.sabotagePanel());
            }else{
                sendMsg(update.getMessage().getChatId(), "Саботаж не готов",Keyboards.rolePanel(players.getUser(update.getMessage().getChatId()).getRole(), players.getUser(update.getMessage().getChatId()).getAlive()));
            }
        }else if(message.equals("Репорт")){
            gameStatus = "vote";
            report();
        }else if(sabotage.equals("Саботаж")){
            players.getUser(update.getMessage().getChatId()).setSabotageTime(time.getTime());
            sabotage = message;
            sabotageStatus = true;
            CompletableFuture.runAsync(()->{
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sabotageEnd();});
            sendMsg(Long.valueOf(admin.getChatId()), "Сломай " + message, Keyboards.adminGamePanel());
            sendSabotage(message);
        }else{
            sendMsg(update.getMessage().getChatId(), "Неизвестная команда" ,Keyboards.rolePanel(false, players.getUser(update.getMessage().getChatId()).getAlive()));
        }
    }

    public void report(){
        for (int i = 0; i < players.getPlayers().size(); i++) {
            if (players.getPlayers().get(i).getAlive()) {
                sendMsg(players.getPlayers().get(i).getChatID(), "Найден труп! Собрание!", Keyboards.votePanel(players));
            } else {
                sendMsg(players.getPlayers().get(i).getChatID(), "Найден труп! \n Но вы тоже мертвы и не голосуете." +
                        "\nПройдите к администратору.", Keyboards.votePanel(players));
            }
        }
    }

    public void sendSabotage(String text){
        for (int i = 0; i < players.getPlayers().size(); i++){
            if (players.getPlayers().get(i).getAlive()){
                sendMsg(players.getPlayers().get(i).getChatID(), "Cломали " + text +
                        "\nЧтобы починить введи код с выполнения задания починки", Keyboards.rolePanel(players.getPlayers().get(i).getRole(),true));
            }
        }
    }

    public void sabotageEnd(){
        if (sabotageStatus){
            gameEnd(false);
        }
    }

    public void gameEnd(boolean winners){
        gameStatus = "init";
        for (int i = 0; i < players.getPlayers().size(); i++){
            if(players.getPlayers().get(i).getRole()){
                sendMsg(players.getPlayers().get(i).getChatID(), winners ? "Поздравляем вы победили": "В этот раз победа за предателями", Keyboards.startPanel());
            }else{
                sendMsg(players.getPlayers().get(i).getChatID(), winners ? "В этот раз победа за экипажем": "Корабль захвачен!", Keyboards.startPanel());
            }
        }
    }

    public void adminBeforeStart(Update update, String message){
        if (message.compareTo("/start") == 0) {
            sendMsg(update.getMessage().getChatId(), "Здравствуй, администратор", Keyboards.adminStartPanel());
        }else if (message.compareTo("Настройки") == 0){
            sendMsg(update.getMessage().getChatId(), "Введите настройки в следующем формате: '/set количество_игроков количество_простых_заданий количество_средних количество_сложных кд_убийцы'", null);
        }else if (message.length() > 4 && message.substring(0, 4).compareTo("/set") == 0){
            subStr = message.split(" ");
            settings.setPlayers(Integer.valueOf(subStr[1]));
            settings.setEasyTasks(Integer.valueOf(subStr[2]));
            settings.setNormalTasks(Integer.valueOf(subStr[3]));
            settings.setHardTasks(Integer.valueOf(subStr[4]));
            settings.setImposterKD(Integer.valueOf(subStr[5]));
            settings.setImpostersCount(Integer.valueOf(subStr[6]));
            sendMsg(update.getMessage().getChatId(), "Настройки сохранены", null);
        }else if (message.compareTo("Покажи настройки") == 0){
            sendMsg(update.getMessage().getChatId(), settings.getAllSettings(), null);
        }else if (message.compareTo("Запуск") == 0) {
            gameStatus = "game";
            int impostersCount = 0;
            sendMsg(update.getMessage().getChatId(), "Запускаем игру...", Keyboards.adminGamePanel());
            System.out.println(settings.getImpostersCount());
            while (impostersCount != settings.getImpostersCount()) {
                impostersCount = 0;
                for (int i = 0; i < players.getPlayers().size(); i++) {
                    int randomNumber = (int) (Math.random() * 100);
                    if (randomNumber % 3 == 0 && impostersCount < settings.getImpostersCount()) {
                        players.getPlayers().get(i).setRole(false);
                        impostersCount++;
                    }else{
                        players.getPlayers().get(i).setRole(true);
                    }
                }
            }
            for (int i = 0; i < players.getPlayers().size(); i++) {
                if (players.getPlayers().get(i).getRole()){
                    sendMsg(players.getPlayers().get(i).getChatID(), "Ты Мирный", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                }else{
                    sendMsg(players.getPlayers().get(i).getChatID(), "Ты Убийца", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                }
            }
        }else {
            sendMsg(update.getMessage().getChatId(), "Неизвестная команда", Keyboards.adminStartPanel());
        }
    }

    public void reboot(Update update){
        for (int i = 0; i < players.countAlive(); i++){
            sendMsg(players.getPlayers().get(i).getChatID(), "Перезапуск игры, нажмите /start", Keyboards.startPanel());
        }
        sendMsg(update.getMessage().getChatId(), "Перезапуск", Keyboards.adminStartPanel());
        players = new PlayersList();
        gameStatus = "init";
        sabotageStatus = false;
        sabotage = "v";
    }

    public void adminInGame(Update update, String message){
        if (message.equals("Голосование")){
            gameStatus = "vote";
            report();
        }else if(message.equals("Перезапуск")){
            reboot(update);
        }else if(message.equals("Воскресить")
        ){
            admin.setMakeAlive(null);
            sendMsg(update.getMessage().getChatId(), "Кого хотите воскресить", Keyboards.makeAlive(players));
        }else if(admin.getMakeAlive() == null){
            players.getPlayerByColor(message).setAlive(true);
            sendMsg(players.getPlayerByColor(message).getChatID(), "Вас воскресили", Keyboards.rolePanel(players.getPlayerByColor(message).getRole(), players.getPlayerByColor(message).getAlive()));
            sendMsg(Long.valueOf(admin.getChatId()), "Успешно", Keyboards.adminGamePanel());
            admin.setMakeAlive("Admin");
        }else{
            sendMsg(update.getMessage().getChatId(), "Неизвестная команда", null);
        }
    }

    public void playersVote(Update update, String message){
        if (voteResults.containsKey(message)){
            voteResults.replace(message, voteResults.get(message) + 1);
            System.out.println("За " + message + "проголосовало " + voteResults.get(message));
        }else{
            voteResults.put(message, 1);
            System.out.println("За " + message + "проголосовало " + voteResults.get(message));
        }
        players.getUser(update.getMessage().getChatId()).setVoted(true);
        voted++;
        if (voted == players.countAlive()){
            gameStatus = "game";
            //игра может закончится
            int maxValueInMap=(Collections.max(voteResults.values()));
            ArrayList<String> killed = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : voteResults.entrySet()) {
                if (entry.getValue() == maxValueInMap) {
                    killed.add(entry.getKey());
                }
            }
            if (killed.size() == 1 && !killed.get(0).equals("Пропустить")){
                if(players.getPlayerByColor(killed.get(0)) != null){
                    players.getPlayerByColor(killed.get(0)).setAlive(false);
                    System.out.println("Убит " + players.getPlayerByColor(killed.get(0)).getColor());
                    for (int i = 0; i < players.getPlayers().size(); i++) {
                        System.out.println(players.getPlayers().get(i).getColor());
                        System.out.println(killed.get(0));
                        if (!players.getPlayers().get(i).getColor().equals(killed.get(0))) {
                            sendMsg(players.getPlayers().get(i).getChatID(), "Убит " + killed.get(0), Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                        } else {
                            sendMsg(players.getPlayers().get(i).getChatID(), "К сожалению вас убили." +
                                    "\nПройдите к администратору.", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                        }
                    }
                }
            }else{
                System.out.println("Никто не убит");
                for (int i = 0; i < players.getPlayers().size(); i++){
                    sendMsg(players.getPlayers().get(i).getChatID(), "Никто не убит.Голосование пропущено" , Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                }
            }
            for (int i = 0; i < players.getPlayers().size(); i++) {
                players.getPlayers().get(i).setVoted(false);
            }
            voted = 0;
            voteResults = new HashMap<>();
        }
    }

    public void playerBeforeStart(Update update, String message){
        if(message.equals("/start")){
            players.addPlayer(new User(update.getMessage().getChatId()));
            sendMsg(update.getMessage().getChatId(), "Здравствуй, игрок, Какой у тебя цвет?", null);
        }else if(players.getUser(update.getMessage().getChatId()) != null && players.getUser(update.getMessage().getChatId()).getColor() == null && gameStatus.equals("init")){
            players.getUser(update.getMessage().getChatId()).setColor(message.toLowerCase());
            players.getUser(update.getMessage().getChatId()).setAlive(true);
            players.getUser(update.getMessage().getChatId()).setVoted(false);
            System.out.println("Добален " + players.getUser(update.getMessage().getChatId()).getColor() + " игрок");
            sendMsg(update.getMessage().getChatId(), "Привет, " + players.getUser(update.getMessage().getChatId()).getColor(), null);
            if(players.getPlayers().size() == settings.getPlayers())
                sendMsg(Long.valueOf(admin.getChatId()), "Команда укомплектована, можно начинать", Keyboards.adminStartPanel());
        }else {
            sendMsg(update.getMessage().getChatId(), "Неизвестная команда", null);
        }
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botName;
    }

    public synchronized void sendMsg(Long chatId, String s, ReplyKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        if (keyboard != null){
            sendMessage.setReplyMarkup(keyboard);
        }
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
