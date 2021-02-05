import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

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
    Date gameTime;
    String gameStatus = "init";
    String sabotage = "v";
    Texts texts = new Texts();
    boolean someoneKilled = false;
    boolean sabotageStatus = false;
    boolean sabotageBeforeVote = false;
    User starter = null;
    HashMap<String, Integer> voteResults = new HashMap<>();
    private Settings settings = new Settings(9, 2, 2, 1, 60, 2);
    boolean redButton = false;
    volatile boolean redButtonReady = true;
    TaskText taskText = new TaskText();
    HashMap<Integer, Integer> taskCooldown = new HashMap<>();
    boolean taskUpdate = false;
    boolean taskModify = false;

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
        }else if(gameStatus.equals("game") && chatId == admin.getChatId()){
            adminInGame(message);
        }else if (gameStatus.equals("game") && players.getUser(chatId).getRole().equals(true)){
            User user = players.getUser(chatId);
            crewMemberInGame(message, user);
        }else if(gameStatus.equals("game") && players.getUser(chatId).getRole().equals(false)){
            User user = players.getUser(chatId);
            imposterInGame(message, user);
        }else if(gameStatus.equals("vote") && (chatId == admin.getChatId() || !players.getUser(chatId).getVoted())){
            if(chatId != admin.getChatId()) {
                User user = players.getUser(chatId);
                playersVote(message, user);
            }else{
                playersVote(message, null);
            }
        }else{
            sendMsg(chatId, "Неизвестная команда", null);
        }
    }

    public void adminBeforeStart(String message){
        if (message.compareTo("/start") == 0) {
            sendMsg(admin.getChatId(), "Здравствуй, администратор", Keyboards.adminStartPanel());
        }else if (message.compareTo("Настройки") == 0){
            sendMsg(admin.getChatId(), "Введите следующие настройки через пробел: \n" +
                    "/set количество игроков " +
                    "количество простых заданий " +
                    "количество средних " +
                    "количество сложных " +
                    "кд убийцы " +
                    "количество убийц", null);
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
            gameTime = new Date();
            gameStatus = "game";
            int impostersCount = 0;
            sendMsg(admin.getChatId(), "Запускаем игру...", Keyboards.adminGamePanel());
            while (impostersCount != settings.getImpostersCount()){
                impostersCount = 0;
                for (int i = 0; i < players.getPlayers().size(); i++) {
                    Random rand = new Random(new Date().getTime());
                    if (Math.abs(rand.nextInt()) % 6 == 3 && impostersCount < settings.getImpostersCount()) {
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
            ArrayList<String> names = new ArrayList<>();
            final int imposterCount = settings.getImpostersCount();
            players.getPlayers().stream().filter(u -> !u.getRole()).forEach(u -> players.getPlayers().stream().filter(name -> !name.getRole() && !name.getColor().equals(u.getColor())).forEach(p -> names.add(p.getColor())));
            players.getPlayers().stream().filter(u -> !u.getRole()).forEach(u -> sendMsg(u.getChatId(),
                    "Ты предатель, тебе доступны такие действия как Убийство и Саботаж." +
                    "\nУничтожь их всех или сломай корабль." +
                    "\nНе попадись!" + (imposterCount > 1 ? "\nСписок предателей: " + String.join(" ", names) : ""),
                    Keyboards.rolePanel(false, true)));
            //Отправляем сообщения администратору о том, кто предатели в игре
            if (imposterCount > 1)
                sendMsg(admin.getChatId(), "Предатели: " + String.join(" ", names), Keyboards.adminGamePanel());
        }else {
            sendMsg(admin.getChatId(), "Неизвестная команда", Keyboards.adminStartPanel());
        }
    }

    public void userBeforeStart(Update update, String message, User user){
        if(message.equals("/start") && players.getUser(update.getMessage().getChatId()) == null){
            players.addPlayer(new User(update.getMessage().getChatId()));
            sendMsg(update.getMessage().getChatId(), "Здравствуй, игрок, Какой у тебя цвет?", null);
        }else if(user.getChatId() != -1 && user.getColor() == null /* && gameStatus.equals("init")*/){
            user.setColor(message);
            user.setAlive(true);
            user.setVoted(false);
            sendMsg(admin.getChatId(), "Добален " + user.getColor() + " игрок", Keyboards.adminStartPanel());
            sendMsg(user.getChatId(), texts.getHelloTexts().get((int) (Math.random() * 100) % 3) + user.getColor(), null);
            if (players.getPlayers().stream().filter(u -> u.getColor() != null).count() == settings.getPlayers()) {
                sendMsg(admin.getChatId(), "Команда укомплектована, можно начинать", Keyboards.adminStartPanel());
            }
        }else {
            sendMsg(update.getMessage().getChatId(), "Неизвестная команда", null);
        }
    }

    public void adminInGame(String message){
        if (message.equals("Голосование")){
            //проверяем, работает ли кнопка
            if (redButtonReady) {
                redButton = true;
                report(players.getPlayers().get(0));
            } else {
                sendMsg(admin.getChatId(), "Кнопка экстренного собрания еще не готова", Keyboards.adminGamePanel());
            }
        }else if(message.equals("Перезапуск")){
            reboot();
        }else if(message.equals("Убить")){
            admin.setKill(null);
            sendMsg(admin.getChatId(), "Кого хотите убить", Keyboards.votePanel(players));
        }else if(admin.getKill() == null){
            if (!message.equals("Пропустить")) {
                User user = players.getPlayerByColor(message);
                if (user.getAlive()) {
                    user.setAlive(false);
                    sendMsg(user.getChatId(), "Вас убил администратор, подойдите к нему", Keyboards.rolePanel(user.getRole(), user.getAlive()));
                    sendMsg(admin.getChatId(), "Успешно", Keyboards.adminGamePanel());
                } else {
                    sendMsg(admin.getChatId(), "Он и так мёртв", Keyboards.adminGamePanel());
                }
            }
            else
                sendMsg(admin.getChatId(), "Ок, пропускаю", Keyboards.adminGamePanel());
            admin.setKill("Admin");
        }else if(message.equals("Воскресить")){
            admin.setMakeAlive(null);
            sendMsg(admin.getChatId(), "Кого хотите воскресить", Keyboards.makeAlive(players));
        }else if(admin.getMakeAlive() == null){
            if (!message.equals("Пропустить")) {
                User user = players.getPlayerByColor(message);
                if (!user.getAlive()) {
                    user.setAlive(true);
                    sendMsg(user.getChatId(), "Вас воскресили, продолжайте игру", Keyboards.rolePanel(user.getRole(), user.getAlive()));
                    sendMsg(admin.getChatId(), "Успешно", Keyboards.adminGamePanel());
                } else {
                    sendMsg(admin.getChatId(), "Он и так жив", Keyboards.adminGamePanel());
                }
            }
            else
                sendMsg(admin.getChatId(), "Ок, пропускаю", Keyboards.adminGamePanel());
            admin.setMakeAlive("Admin");
        }else{
            sendMsg(admin.getChatId(), "Неизвестная команда", Keyboards.adminGamePanel());
        }
    }

    public void crewMemberInGame(String message, User user){
        if(message.equals("Получить задание") && user.getActiveTaskNum() == 0){
            user.getTask();
            sendMsg(user.getChatId(), texts.getGettingTaskTexts().get((int)(Math.random()*100)%3), Keyboards.rolePanel(true, user.getAlive()));
            while (user.getActiveTaskNum() != 0) {
                int finalTask = user.getActiveTaskNum();

                if (players.getPlayers().stream().noneMatch(u -> u.getActiveTaskNum() == finalTask
                        && !u.getChatId().equals(user.getChatId()))
                        && settings.checkAvailableTasks(finalTask)){
                    break;
                }
                user.getTask();
            }
            if (user.getActiveTaskNum() == 0){
                sendMsg(user.getChatId(), "Ты уже всё сделал", Keyboards.rolePanel(true, user.getAlive()));

                // отправляем сообщение админу о том, что пользователь сделал все задания
                sendMsg(admin.getChatId(), "Игрок " + user.getColor() + " выполнил все задания!", Keyboards.adminGamePanel());

            }else {
                user.setActiveTask(settings.getTask(user.getActiveTaskNum()));
                sendMsg(user.getChatId(),
                        texts.getSendingTaskTexts().get((int)(Math.random()*100)%3) + user.getActiveTaskNum() +"\n" + user.getActiveTask().getTaskText(),
                        Keyboards.rolePanel(user.getRole(), user.getAlive()));
                System.out.println(user.getColor() + " " + user.getActiveTaskNum() + " " + user.getActiveTask().getTaskText());
            }
        }else if (message.equals("Репорт") && user.getAlive()){
            report(user);
        }else if (user.getActiveTaskNum() != 0 &&
                (message.equals(user.getActiveTask().getCode()))){
            user.getComplitedTasks().add(user.getActiveTaskNum());
            if(user.getActiveTaskNum()/10 == 1)
                user.setEasyTasks(user.getEasyTasks() - 1);
            else if(user.getActiveTaskNum()/10 == 2)
                user.setNormalTasks(user.getNormalTasks() - 1);
            else {
                user.setHardTasks(user.getHardTasks() - 1);
            }
            user.setActiveTaskNum(0);
            sendMsg(user.getChatId(), texts.getCompletingTaskTexts().get((int)(Math.random()*100)%3), Keyboards.rolePanel(user.getRole(), user.getAlive()));
            checkGameEndByTasks();
        }else if (message.equals("Убить")){
            sendMsg(user.getChatId(), "Ты был избранником!" +
                    "\nПредрекали что ты уничтожишь ситхов, а не примкнёшь к ним." +
                    "\nВосстановишь равновесие силы, а не ввергнешь её во мрак!", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }else if (sabotageStatus && settings.getSabotageSolvers().get(sabotage).stream().anyMatch(u -> u.equals(message))){
            checkSabotage(message, user);
        }
        else if (user.getActiveTaskNum() != 0 && !message.equals(user.getActiveTask().getCode())){
            sendMsg(user.getChatId(), "Неверный код задания", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }
         else{
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
                starter = user;
                startSabotage(message, user);
            }else{
                sabotage = message;
                sendMsg(user.getChatId(), "Это сделано в СССР, не ломается", Keyboards.rolePanel(user.getRole(), user.getAlive()));
            }
        }else if(message.equals("Получить задание") && user.getAlive()) {
            user.getTask();
            while (true){
                int finalTask = user.getActiveTaskNum();
                if (settings.checkAvailableTasks(finalTask))
                break;
                user.getTask();
            }
            user.setActiveTask(settings.getTask(user.getActiveTaskNum()));
            sendMsg(user.getChatId(), "Задание номер " + user.getActiveTaskNum() + "\n" + user.getActiveTask().getTaskText(), Keyboards.imposterPanel(user.getAlive()));
        }else if (sabotageStatus && Integer.parseInt(message) > 0) {
            checkSabotage(message, user);
        }else{
            sendMsg(user.getChatId(), "Неизвестная команда" ,Keyboards.rolePanel(false, user.getAlive()));
        }
    }

    private void startSabotage(String message, User user) {
        Date time = new Date();
        user.setSabotageTime(time.getTime());
        sabotageBeforeVote = false;
        sabotage = message;
        sabotageStatus = true;
        if (message.equals("Реактор")) {
            Executors.newCachedThreadPool().submit(() -> {
                int counter = 0;
                while (counter <= 90000) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!sabotageStatus)
                        break;
                    else if (counter == 30000)
                        sendToAlive("До взрыва реактора осталась 1 минута");
                    else if (counter == 60000)
                        sendToAlive("До взрыва реактора осталось 30 секунд");
                    else if (counter == 80000)
                        sendToAlive("До взрыва реактора осталось 10 секунд");
                    counter++;
                }
                    checkGameEndBySabotage();
            });
        }else if(message.equals("Кислород")){
            Executors.newCachedThreadPool().submit(() -> {
                int counter = 0;
                while (counter <= 90000) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!sabotageStatus)
                        break;
                    else if (counter == 30000)
                        sendToAlive("До полной утечки кислорода осталась 1 минута");
                    else if (counter == 60000)
                        sendToAlive("До полной утечки кислорода осталось 30 секунд");
                    else if (counter == 80000)
                        sendToAlive("До полной утечки кислорода осталось 10 секунд");
                    counter++;
                }
                checkGameEndBySabotage();
            });
        }
        if (!sabotageBeforeVote)
            sendMsg(admin.getChatId(), "Сломай " + message, Keyboards.adminGamePanel());
        else
            sendMsg(admin.getChatId(), "Саботаж " + message + " продолжается", Keyboards.adminGamePanel());
        sendSabotage(message);
    }

    private void checkSabotage(String message, User user) {
        if (settings.getSabotageSolvers().get(sabotage).stream().anyMatch(u -> u.equals(message))){
            settings.sabotageSolvers.get(sabotage).removeIf(u -> u.equals(message));
            if(settings.sabotageSolvers.get(sabotage).size() == 0)
                if (message.equals("Свет")) {
                    settings.sabotageSolvers.remove("Свет");
                    settings.makeLightSolvers();
                }
                else if (message.equals("Реактор")) {
                    settings.sabotageSolvers.remove("Реактор");
                    settings.makeReactorSolvers();
                }
                else if (message.equals("Кислород")) {
                    settings.sabotageSolvers.remove("Кислород");
                    settings.makeOxygenSolvers();
                }
                else if (message.equals("Связь")) {
                    settings.sabotageSolvers.remove("Связь");
                    settings.makeLightSolvers();
                }
            sabotageStatus = false;
            //Отправляем сообщение админу о том, что саботаж починен
            sendMsg(admin.getChatId(), sabotage + " починен!", Keyboards.adminGamePanel());
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
                sendMsg(players.getPlayers().get(i).getChatId(), "Cломали " + text + "\nКод лежит в месте " + taskText.getSabotage().get(text),
                         Keyboards.rolePanel(players.getPlayers().get(i).getRole(),true));
            }
        }
    }

    public void checkGameEndByTasks() {
        if(players.getPlayers().stream().filter(User::getRole).filter(u -> u.getComplitedTasks().size() == u.totalTasks).count() == settings.getPlayers() - settings.getImpostersCount()){
            gameEnd(true);
        }
    }

    public void checkGameEndBySabotage() {
        if (sabotageStatus && (sabotage.equals("Реактор") || sabotage.equals("Кислород"))){
            sendMsg(admin.getChatId(), (sabotage.equals("Реактор") ? "Реактор взорвался" : "Кислород закончился"), Keyboards.adminStartPanel());
            gameEnd(false);
        }
    }

    public void checkGameEnd(){
        if (players.getPlayers().stream().filter(u -> u.getAlive() && u.getRole()).count() == players.getPlayers().stream().filter(u -> u.getAlive() && !u.getRole()).count()){
            gameEnd(false);
        }else if(players.getPlayers().stream().noneMatch(u -> !u.getRole() && u.getAlive())) {
            gameEnd(true);
        }
    }

    public void gameEnd(boolean winners){
        gameStatus = "init";
        //Отправляем сообщение админу о том, кто выиграл (оставил эту клавиатуру так как далее идет ребут)
        sendMsg(admin.getChatId(), winners ? "Конец игры\nПобедил экипаж" : "Конец игры\nПобедили предатели", Keyboards.adminStartPanel());
        for (int i = 0; i < players.getPlayers().size(); i++){
            if(players.getPlayers().get(i).getRole()){
                sendMsg(players.getPlayers().get(i).getChatId(), winners ? "Поздравляем вы победили": "В этот раз победа за предателями", Keyboards.startPanel());
            }else{
                sendMsg(players.getPlayers().get(i).getChatId(), winners ? "В этот раз победа за экипажем": "Корабль захвачен!", Keyboards.startPanel());
            }
        }
        sendMsg(394615109L, "Время игры: " + ((new Date().getTime() - gameTime.getTime()) / 60000),Keyboards.empty());
        reboot();
    }

    public void reboot(){
        for (int i = 0; i < players.getPlayers().size(); i++){
            sendMsg(players.getPlayers().get(i).getChatId(), "Перезапуск игры, введите имя", Keyboards.empty());
        }
        sendMsg(admin.getChatId(), "Перезапуск", Keyboards.adminStartPanel());
        players.reboot();
        gameStatus = "init";
        sabotage = "v";
        texts = new Texts();
        someoneKilled = false;
        sabotageStatus = false;
        voteResults = new HashMap<>();
        settings = new Settings(settings.getPlayers(), settings.getEasyTasks(), settings.getNormalTasks(), settings.getTimerTasks(), settings.getImposterKD(), settings.getImpostersCount());
        redButton = false;
        redButtonReady = true;
        taskText = new TaskText();
        taskCooldown = new HashMap<>();
        taskUpdate = false;
        taskModify = false;
    }

    public void report(User user){
        //проверяем, работает ли кнопка или кого-то убили
        if (redButtonReady || someoneKilled) {
            if (someoneKilled || redButton) {
                if (sabotageStatus){
                    sabotageBeforeVote = true;
                    sabotageStatus = false;
                }
                gameStatus = "vote";
                for (int i = 0; i < players.getPlayers().size(); i++) {
                    if (players.getPlayers().get(i).getAlive()) {
                        if (someoneKilled) {
                            sendMsg(players.getPlayers().get(i).getChatId(), texts.getReportTexts().get((int) (Math.random() * 100) % 3), Keyboards.votePanel(players));
                        } else {
                            sendMsg(players.getPlayers().get(i).getChatId(), texts.getReportTexts().get((int) (Math.random() * 100) % 2), Keyboards.votePanel(players));
                        }
                    } else {
                        sendMsg(players.getPlayers().get(i).getChatId(), "Собрание! \n Но вы мертвы и не голосуете." +
                                "\nПройдите к администратору.", Keyboards.empty());
                    }
                }
                sendMsg(admin.getChatId(), "Начинается собрание!", Keyboards.adminVotePanel());
                someoneKilled = false;
                redButton = false;
            } else {
                sendMsg(user.getChatId(), "Трупа нет, ты врёшь", Keyboards.rolePanel(user.getRole(), user.getAlive()));
            }
            //если кнопка не работает
        } else {
            sendMsg(user.getChatId(), "Кнопка еще не готова", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }

    }

    public void playersVote(String message, User user) {
        if (user != null) {
            if (players.getPlayers().stream().anyMatch(u -> u.getColor().equals(message) && u.getAlive()) || message.equals("Пропустить")) {
                if (voteResults.containsKey(message)) {
                    voteResults.replace(message, voteResults.get(message) + 1);
                } else {
                    voteResults.put(message, 1);
                }
                user.setVoted(true);
                voted++;
            }
        }
        if (voted == players.countAlive() || message.equals("Завершить голосование")) {
            gameStatus = "game";
            int maxValueInMap = (Collections.max(voteResults.values()));
            ArrayList<String> killed = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : voteResults.entrySet()) {
                if (entry.getValue() == maxValueInMap) {
                    killed.add(entry.getKey());
                }
            }
            if (killed.size() == 1 && !killed.get(0).equals("Пропустить")) {
                if (players.getPlayerByColor(killed.get(0)) != null) {
                    players.getPlayerByColor(killed.get(0)).setAlive(false);
                    sendMsg(admin.getChatId(), "Убит " + killed.get(0), Keyboards.adminGamePanel());
                    for (int i = 0; i < players.getPlayers().size(); i++) {
                        if (!players.getPlayers().get(i).getColor().equals(killed.get(0))) {
                            sendMsg(players.getPlayers().get(i).getChatId(), "Вы выкинули " + killed.get(0), Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                            //Отправляем сообщение админу о том, кто был выкинут в ходе голосования
                        } else {
                            sendMsg(players.getPlayers().get(i).getChatId(), "К сожалению вас выкинули." +
                                    "\nПройдите к администратору.", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                        }
                    }
                    sendMsg(admin.getChatId(), "Голосование завершено\nИгрок " + killed.get(0) + " был выкинут в ходе голосования", Keyboards.adminGamePanel());
                }
            } else {
                sendMsg(admin.getChatId(), "Никто не выкинут голосованием", Keyboards.adminGamePanel());
                for (int i = 0; i < players.getPlayers().size(); i++) {
                    sendMsg(players.getPlayers().get(i).getChatId(), "Никто не выкинут.Голосование пропущено", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                }
            }
            for (int i = 0; i < players.getPlayers().size(); i++) {
                players.getPlayers().get(i).setVoted(false);
                sendMsg(players.getPlayers().get(i).getChatId(), "Игра продолжается", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
            }
            voted = 0;
            voteResults = new HashMap<>();
            Date time = new Date();
            players.getPlayers().stream().filter(u -> !u.getRole()).forEach(u -> {
                u.setKillTime(time.getTime());
                u.setSabotageTime(time.getTime());
            });
            checkGameEnd();
            //кнопка не может использоваться в течение минуты
            redButtonReady = false;
            //начинается поток для кнопки (написан так же, как и в саботаже)
            Executors.newCachedThreadPool().submit(() -> {
                try {
                    //поток ждет минуту
                    Thread.sleep(90000);
                    //делаем кнопку работоспособной
                    redButtonReady = true;
                    //Сообщение админу о начале работы кнопки
                    sendMsg(admin.getChatId(), "Кнопка экстренного собрания активна", Keyboards.adminGamePanel());
                    //Сообщение игрокам о начале работы кнопки
                    for (int i = 0; i < players.getPlayers().size(); i++) {
                        sendMsg(players.getPlayers().get(i).getChatId(), "Кнопка экстренного собрания активна", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Ошибка в потоке кнопки");
                }
            });
            //Конец потока кнопки
            if (sabotageBeforeVote)
                startSabotage(sabotage, players.getUser(starter.getChatId()));
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
