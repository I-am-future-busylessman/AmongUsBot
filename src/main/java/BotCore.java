import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;

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
    Texts texts = new Texts();
    boolean someoneKilled = false;
    boolean sabotageStatus = false;
    HashMap<String, Integer> voteResults = new HashMap<>();
    private Settings settings = new Settings(10, 2, 2, 3, 30, 2);
    boolean redButton = false;
    TaskText taskText = new TaskText();

    String[] subStr;
    int voted = 0;

    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        if(!update.getMessage().hasText()) {
            sendMsg(chatId, "Я понимаю только текст", null);
        }else if(gameStatus.equals("init") && chatId == admin.getChatId()){
            adminBeforeStart(message);
        }else if(gameStatus.equals("init") && !(chatId == admin.getChatId())){
            User user = players.getUser(chatId);
            userBeforeStart(update, message, user);
        }else if(((gameStatus.equals("game")) || (gameStatus.equals("vote"))) && chatId == admin.getChatId()){
            adminInGame(message);
        }else if (gameStatus.equals("game") && players.getUser(chatId).getRole().equals(true)){
            User user = players.getUser(chatId);
            crewMemberInGame(message, user);
        }else if(gameStatus.equals("game") && players.getUser(chatId).getRole().equals(false)){
            User user = players.getUser(chatId);
            imposterInGame(message, user);
        }else if(gameStatus.equals("vote") && !players.getUser(chatId).getVoted()){
            User user = players.getUser(chatId);
            playersVote(message, user);
        }else{
            sendMsg(chatId, "Неизвестная команда", null);
        }
    }

    public void adminBeforeStart(String message){
        if (message.compareTo("/start") == 0) {
            sendMsg(admin.getChatId(), "Здравствуй, администратор", Keyboards.adminStartPanel());
        }else if (message.compareTo("Настройки") == 0){
            sendMsg(admin.getChatId(), "Введите настройки в следующем формате: " +
                    "\n/set количество_игроков " +
                    "количество_простых_заданий " +
                    "количество_средних " +
                    "количество_сложных " +
                    "кд_убийцы " +
                    "количество_убийц", null);
        }else if (message.length() > 4 && message.substring(0, 4).compareTo("/set") == 0){
            subStr = message.split(" ");
            settings.setPlayers(Integer.valueOf(subStr[1]));
            settings.setEasyTasks(Integer.valueOf(subStr[2]));
            settings.setNormalTasks(Integer.valueOf(subStr[3]));
            settings.setTimerTasks(Integer.valueOf(subStr[4]));
            settings.setImposterKD(Integer.valueOf(subStr[5]));
            settings.setImpostersCount(Integer.valueOf(subStr[6]));
            sendMsg(admin.getChatId(), "Настройки сохранены", null);
        }else if (message.compareTo("Покажи настройки") == 0){
            sendMsg(admin.getChatId(), settings.getAllSettings(), null);
        }else if (message.compareTo("Запуск") == 0) {
            gameStatus = "game";
            int impostersCount = 0;
            sendMsg(admin.getChatId(), "Запускаем игру...", Keyboards.adminGamePanel());
            while (impostersCount != settings.getImpostersCount()){
                impostersCount = 0;
                for (int i = 0; i < players.getPlayers().size(); i++) {
                    int randomNumber = (int) (Math.random() * 100);
                    if (randomNumber % 3 == 0 && impostersCount < settings.getImpostersCount()) {
                        players.getPlayers().get(i).setRole(false);

                        impostersCount++;
                    }else{
                        players.getPlayers().get(i).setRole(true);
                    }
                    players.getPlayers().get(i).setEasyTasks(settings.getEasyTasks());
                    players.getPlayers().get(i).setNormalTasks(settings.getNormalTasks());
                    players.getPlayers().get(i).setHardTasks(settings.getTimerTasks());
                    players.getPlayers().get(i).setTotalTasks(settings.getEasyTasks() + settings.getNormalTasks() + settings.getTimerTasks());
                }
            }
            players.getPlayers().stream().filter(User::getRole).forEach(u -> sendMsg(u.getChatId(),
                    "Ты член экипажа, твоя задача выполнять задания и вычислять убийц",
                    Keyboards.rolePanel(true, true)));
            //players.getPlayers().stream().filter(User::getRole).forEach(u -> sendPht(u.getChatId(),
                    //settings.getImpostersCount() == 1? "2.png"
                      //      :settings.getImpostersCount() == 2? "3.png"
                        //    : "4.png",
                    //Keyboards.rolePanel(true, true)));
            players.getPlayers().stream().filter(u -> !u.getRole()).forEach(u -> sendMsg(u.getChatId(),
                    "Ты предатель, тебе доступны такие действия как Убийство и Саботаж." +
                    "\nУничтож их всех или сломай корабль." +
                    "\nНе попадись!", Keyboards.rolePanel(false, true)));
            players.getPlayers().stream().filter(u -> !u.getRole()).forEach(u -> sendPht(u.getChatId(),
                    "1.png", Keyboards.rolePanel(false, true)));

        }else {
            sendMsg(admin.getChatId(), "Неизвестная команда", Keyboards.adminStartPanel());
        }
    }

    public void userBeforeStart(Update update, String message, User user){
        if(message.equals("/start") && players.getUser(update.getMessage().getChatId()) == null){
            players.addPlayer(new User(update.getMessage().getChatId()));
            sendMsg(update.getMessage().getChatId(), "Здравствуй, игрок, Какой у тебя цвет?", null);
        }else if(user.getChatId() != -1 && user.getColor() == null && gameStatus.equals("init")){
            user.setColor(message.toLowerCase());
            user.setAlive(true);
            user.setVoted(false);
            System.out.println("Добален " + user.getColor() + " игрок");
            sendMsg(user.getChatId(), texts.getHelloTexts().get((int)(Math.random()*100)%3) + user.getColor(), null);
            if(players.getPlayers().stream().filter(u -> u.getColor() != null).count() == settings.getPlayers()){
                sendMsg(admin.getChatId(), "Команда укомплектована, можно начинать", Keyboards.adminStartPanel());
            }
        }else {
            sendMsg(update.getMessage().getChatId(), "Неизвестная команда", null);
        }
    }

    public void adminInGame(String message){
        if (message.equals("Голосование")){
            redButton = true;
            report(players.getPlayers().get(0));
        }else if(message.equals("Перезапуск")){
            reboot();
        }else if(message.equals("Воскресить")
        ){
            admin.setMakeAlive(null);
            sendMsg(admin.getChatId(), "Кого хотите воскресить", Keyboards.makeAlive(players));
        }else if(admin.getMakeAlive() == null){
            User user = players.getPlayerByColor(message);
            if (!user.getAlive()) {
                user.setAlive(true);
                sendMsg(user.getChatId(), "Вас воскресили", Keyboards.rolePanel(user.getRole(), user.getAlive()));
                sendMsg(admin.getChatId(), "Успешно", Keyboards.adminGamePanel());
            }else{
                sendMsg(admin.getChatId(), "Он и так жив", Keyboards.adminGamePanel());
            }
            admin.setMakeAlive("Admin");
        }else{
            sendMsg(admin.getChatId(), "Неизвестная команда", null);
        }
    }

    public void crewMemberInGame(String message, User user){
        if(message.equals("Получить задание") && user.getActiveTask() == 0){
            user.getTask();
            sendMsg(user.getChatId(), texts.getGettingTaskTexts().get((int)(Math.random()*100)%3), Keyboards.rolePanel(true, user.getAlive()));
            while (user.getActiveTask() != 0) {
                int finalTask = user.getActiveTask();

                if (players.getPlayers().stream().noneMatch(u -> u.getActiveTask() == finalTask
                        && !u.getChatId().equals(user.getChatId()))
                        && settings.checkAvailableTasks(finalTask)){
                    break;
                }
                user.getTask();
            }
            if (user.getActiveTask() == 0){
                sendMsg(user.getChatId(), "Ты уже всё сделал", Keyboards.rolePanel(true, user.getAlive()));
            }else {
                sendMsg(user.getChatId(),
                        texts.getSendingTaskTexts().get((int)(Math.random()*100)%3) + user.getActiveTask() +"\n" + taskText.getTask().get(user.getActiveTask()),
                        Keyboards.rolePanel(user.getRole(), user.getAlive()));
            }
        }else if (message.equals("Репорт") && user.getAlive()) {
            report(user);
        }else if (user.getActiveTask() != 0 && Integer.parseInt(message) == settings.getTask(user.getActiveTask())){
            user.getComplitedTasks().add(user.getActiveTask());
            if(user.getActiveTask()/10 == 1)
                user.setEasyTasks(user.getEasyTasks() - 1);
            else if(user.getActiveTask()/10 == 2)
                user.setNormalTasks(user.getNormalTasks() - 1);
            else {
                user.setHardTasks(user.getHardTasks() - 1);
                Executors.newCachedThreadPool().submit(() -> {
                    int taskValue = settings.getAvailableTasks().get(user.getActiveTask());
                    int taskKey = user.getActiveTask();
                    settings.removeTask(taskKey);
                    try {
                        Thread.sleep(300000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    settings.addTask(taskKey, taskValue);
                });
            }
            System.out.println(user.getActiveTask());
            user.setActiveTask(0);
            sendMsg(user.getChatId(), texts.getCompletingTaskTexts().get((int)(Math.random()*100)%3), Keyboards.rolePanel(user.getRole(), user.getAlive()));
            checkGameEnd();
        }else if (message.equals("Убить")){
            sendMsg(user.getChatId(), "Ты был избранником!" +
                    "\nПредрекали что ты уничтожишь ситхов, а не примкнёшь к ним." +
                    "\nВосстановишь равновесие силы, а не ввергнешь её во мрак!", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }else if (sabotageStatus && Integer.parseInt(message) > 0){
            checkSabotage(message, user);
        }else{
            sendMsg(user.getChatId(), "Неизвестная команда", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }
    }

    public void imposterInGame(String message, User user){
        Date time = new Date();
        if (message.equals("Убить") && user.getAlive()){
            if (time.getTime() - user.getKillTime() > settings.getImposterKD()*1000) {
                user.setColorToKill(null);
                sendMsg(user.getChatId(), "Кому снести башку?", Keyboards.votePanel(players));
            }else{
                sendMsg(user.getChatId(), "Бластер ещё не перезаряжен", Keyboards.rolePanel(false, true));
            }
        }else if(user.getColorToKill() == null){
            user.setColorToKill(message);
            user.setConfirmColorToKill(null);
            sendMsg(user.getChatId(),texts.getKillRepeatTexts().get((int)(Math.random()*100)%3), Keyboards.votePanel(players));
        }else if(user.getConfirmColorToKill() == null){
            if (user.getColorToKill().equals(message) && players.getPlayerByColor(message) != null){
                if(players.getPlayerByColor(message).getRole()) {
                    someoneKilled = true;
                    players.getPlayerByColor(message).setAlive(false);
                    sendMsg(admin.getChatId(), "Убит " + message, Keyboards.adminGamePanel());
                    sendMsg(user.getChatId(), message + texts.getKillingTexts().get((int)(Math.random()*100)%3), Keyboards.rolePanel(false, true));
                    //sendPht(players.getPlayerByColor(message).getChatId(),"5.png", Keyboards.rolePanel(players.getPlayerByColor(message).getRole(), false));
                    sendMsg(players.getPlayerByColor(message).getChatId(), texts.getDeadTexts().get((int)(Math.random()*100)%3), Keyboards.rolePanel(players.getPlayerByColor(message).getRole(), false));
                    user.setKillTime(time.getTime());
                    checkGameEnd();
                }else{
                    sendMsg(user.getChatId(), "Совсем офигел! Огонь по своим!", Keyboards.rolePanel(user.getRole(), user.getAlive()));
                }
            }else{
                sendMsg(user.getChatId(), "Не попал, салага", Keyboards.rolePanel(false, true));
            }
            user.setConfirmColorToKill(message);
        }else if(message.equals("Саботаж")){
            if (time.getTime() - user.getSabotageTime() > 120000 && !sabotageStatus) {
                sabotage = "Саботаж";
                sendMsg(user.getChatId(), "Что хочешь сломать?", Keyboards.sabotagePanel());
            }else{
                sendMsg(user.getChatId(), "Саботаж не готов",Keyboards.rolePanel(user.getRole(), user.getAlive()));
            }
        }else if(message.equals("Репорт") && user.getAlive()){
            report(user);
        }else if(sabotage.equals("Саботаж")) {
            if (settings.getSabotageSolvers().containsKey(message)) {
                user.setSabotageTime(time.getTime());
                sabotage = message;
                sabotageStatus = true;
                if (message.equals("Реактор")) {
                    Executors.newCachedThreadPool().submit(() -> {
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (sabotageStatus)
                            sendToAlive("До взрыва реактора осталась 1 минута");
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (sabotageStatus)
                            sendToAlive("До взрыва реактора осталось 30 секунд");
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (sabotageStatus)
                            sendToAlive("До взрыва реактора осталось 10 секунд");
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        checkGameEnd();
                    });
                }else if(message.equals("Кислород")){
                    Executors.newCachedThreadPool().submit(() -> {
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (sabotageStatus)
                                sendToAlive("До полной утечки кислорода осталась 1 минута");
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (sabotageStatus)
                                sendToAlive("До полной утечки кислорода осталось 30 секунд");
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (sabotageStatus)
                                sendToAlive("До полной утечки кислорода осталось 10 секунд");
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        checkGameEnd();
                    });
                }
                sendMsg(admin.getChatId(), "Сломай " + message, Keyboards.adminGamePanel());
                sendSabotage(message);
            }else{
                sabotage = message;
                sendMsg(user.getChatId(), "Это сделано в СССР, не ломается", Keyboards.rolePanel(user.getRole(), user.getAlive()));
            }
        }else if (sabotageStatus && Integer.parseInt(message) > 0) {
            checkSabotage(message, user);
        }else{
            sendMsg(user.getChatId(), "Неизвестная команда" ,Keyboards.rolePanel(false, user.getAlive()));
        }
    }

    private void checkSabotage(String message, User user) {
        if (settings.getSabotageSolvers().get(sabotage).toString().equals(message)) {
            sabotageStatus = false;
            System.out.println("Саботаж починен");
            for (int i = 0; i < players.getPlayers().size(); i++) {
                sendMsg(players.getPlayers().get(i).getChatId(), sabotage + " починен!", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
            }
        } else {
            sendMsg(user.getChatId(), "Неверный код", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }
    }

    public void sendToAlive(String text){
        for (int i = 0; i < players.getPlayers().size(); i++){
            if (players.getPlayers().get(i).getAlive()){
                sendMsg(players.getPlayers().get(i).getChatId(), text, Keyboards.rolePanel(players.getPlayers().get(i).getRole(),true));
            }
        }
    }

    public void sendSabotage(String text){
        for (int i = 0; i < players.getPlayers().size(); i++){
            if (players.getPlayers().get(i).getAlive()){
                System.out.println(text);
                sendMsg(players.getPlayers().get(i).getChatId(), "Cломали " + text + "\nКод лежит в месте " + taskText.getSabotage().get(text),
                         Keyboards.rolePanel(players.getPlayers().get(i).getRole(),true));
                System.out.println(taskText.getSabotage());
            }
        }
    }

    public void checkGameEnd(){
        if (sabotageStatus && (sabotage.equals("Реактор") || sabotage.equals("Кислород"))){
            gameEnd(false);
        }else if(players.getPlayers().stream().filter(u -> u.getAlive() && u.getRole()).count() == players.getPlayers().stream().filter(u -> u.getAlive() && !u.getRole()).count()){
            gameEnd(false);
        }else if(players.getPlayers().stream().noneMatch(u -> !u.getRole() && u.getAlive())) {
            gameEnd(true);
        }else if(players.getPlayers().stream().filter(User::getRole).filter(u -> u.getComplitedTasks().size() == u.totalTasks).count() == settings.getPlayers() - settings.getImpostersCount()){
            gameEnd(true);
        }
    }

    public void gameEnd(boolean winners){
        gameStatus = "init";
        for (int i = 0; i < players.getPlayers().size(); i++){
            if(players.getPlayers().get(i).getRole()){
                sendMsg(players.getPlayers().get(i).getChatId(), winners ? "Поздравляем вы победили": "В этот раз победа за предателями", Keyboards.startPanel());
            }else{
                sendMsg(players.getPlayers().get(i).getChatId(), winners ? "В этот раз победа за экипажем": "Корабль захвачен!", Keyboards.startPanel());
            }
        }
        reboot();
    }

    public void reboot(){
        for (int i = 0; i < players.countAlive(); i++){
            sendMsg(players.getPlayers().get(i).getChatId(), "Перезапуск игры, нажмите /start", Keyboards.startPanel());
        }
        sendMsg(admin.getChatId(), "Перезапуск", Keyboards.adminStartPanel());
        players = new PlayersList();
        gameStatus = "init";
        sabotageStatus = false;
        sabotage = "v";
    }

    public void report(User user){
        if (someoneKilled || redButton) {
            sabotageStatus = false;
            gameStatus = "vote";
            for (int i = 0; i < players.getPlayers().size(); i++) {
                if (players.getPlayers().get(i).getAlive()) {
                    if (someoneKilled){
                        //sendPht(players.getPlayers().get(i).getChatId(), "6.png", Keyboards.votePanel(players));
                        sendMsg(players.getPlayers().get(i).getChatId(), texts.getReportTexts().get((int)(Math.random()*100)%3),Keyboards.votePanel(players));
                    }
                    else{
                        //sendPht(players.getPlayers().get(i).getChatId(), "7.png", Keyboards.votePanel(players));
                        sendMsg(players.getPlayers().get(i).getChatId(), texts.getReportTexts().get((int)(Math.random()*100)%2),Keyboards.votePanel(players));
                    }
                } else {
                    sendMsg(players.getPlayers().get(i).getChatId(), "Собрание! \n Но вы мертвы и не голосуете." +
                            "\nПройдите к администратору.", Keyboards.votePanel(players));
                }
            }
            someoneKilled = false;
            redButton = false;
        }else{
            sendMsg(user.getChatId(), "Трупа нет, ты врёшь", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }
    }

    public void playersVote(String message, User user){
        if (voteResults.containsKey(message)){
            voteResults.replace(message, voteResults.get(message) + 1);
        }else{
            voteResults.put(message, 1);
        }
        user.setVoted(true);
        voted++;
        if (voted == players.countAlive()){
            gameStatus = "game";
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
                    sendMsg(admin.getChatId(), "Убит " + killed.get(0), Keyboards.adminGamePanel());
                    for (int i = 0; i < players.getPlayers().size(); i++) {
                        if (!players.getPlayers().get(i).getColor().equals(killed.get(0))) {
                            sendMsg(players.getPlayers().get(i).getChatId(), "Вы выкинули " + killed.get(0), Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                        } else {
                            sendMsg(players.getPlayers().get(i).getChatId(), "К сожалению вас выкинули." +
                                    "\nПройдите к администратору.", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                        }
                    }
                }
            }else{
                sendMsg(admin.getChatId(), "Никто не выкинут голосованием", Keyboards.adminGamePanel());
                for (int i = 0; i < players.getPlayers().size(); i++){
                    sendMsg(players.getPlayers().get(i).getChatId(), "Никто не выкинут.Голосование пропущено" , Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                }
            }
            for (int i = 0; i < players.getPlayers().size(); i++) {
                players.getPlayers().get(i).setVoted(false);
                sendMsg(players.getPlayers().get(i).getChatId(), "Игра продолжается", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
            }
            voted = 0;
            voteResults = new HashMap<>();
            checkGameEnd();
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

    public synchronized void sendPht(Long chatId, String path, ReplyKeyboardMarkup keyboard) {
        SendPhoto sendPhoto = new SendPhoto();

        sendPhoto.setChatId(chatId);
        sendPhoto.setNewPhoto(new File("/Users/out-kolyada-ad/IdeaProjects/AmongUS/src/main/resources", path));
        if (keyboard != null){
            sendPhoto.setReplyMarkup(keyboard);
        }
        try {
            sendPhoto(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
