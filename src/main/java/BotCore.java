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
    private Settings settings = new Settings(9, 2, 2, 1, 60,50 , 40 , 1);
    Sabotage sabotage = new Sabotage();
    Texts texts = new Texts();
    Date gameTime;
    String gameStatus = "init";
    boolean someoneKilled = false;
    User starter = null;
    HashMap<String, Integer> voteResults = new HashMap<>();
    boolean redButton = false;
    volatile boolean redButtonReady = true;
    boolean settingsReady = false;
    int voted = 0;

    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        if(!update.getMessage().hasText()) {
            sendMsg(chatId, "Я понимаю только текст", null);
        }else if(message.startsWith("/startAdmin") && gameStatus.equals("init")){
            if (message.endsWith("2190")) {
                sendToAdmins("У вас забрали роль администратора");
                admin.setAdmin(chatId);
                sendMsg(chatId, "Вы теперь администратор", Keyboards.adminStartPanel());
            }
            else
                sendMsg(chatId, "Неверный пароль администратора", Keyboards.empty());
        }else if(message.startsWith("/addAdmin") && gameStatus.equals("init")){
            if (message.endsWith("2190")) {
                sendToAdmins("Добавлен новый администратор");
                admin.addAdmin(chatId);
                sendMsg(chatId, "Вы теперь администратор", Keyboards.adminStartPanel());
            }
            else
                sendMsg(chatId, "Неверный пароль администратора", Keyboards.empty());
        }else if(gameStatus.equals("init") && admin.getChatId().contains(chatId)){
            adminBeforeStart(message, chatId);
        }else if(gameStatus.equals("init") && !admin.getChatId().contains(chatId)){
            User user = players.getUser(chatId);
            userBeforeStart(update, message, user);
        }else if(gameStatus.equals("game") && admin.getChatId().contains(chatId)){
            adminInGame(message, chatId);
        }else if (gameStatus.equals("game") && players.getUser(chatId).getRole().equals(true)){
            User user = players.getUser(chatId);
            crewMemberInGame(message, user);
        }else if(gameStatus.equals("game") && players.getUser(chatId).getRole().equals(false)){
            User user = players.getUser(chatId);
            imposterInGame(message, user);
        }else if(gameStatus.equals("vote") && (admin.getChatId().contains(chatId) || !players.getUser(chatId).getVoted())){
            if(!admin.getChatId().contains(chatId)) {
                User user = players.getUser(chatId);
                playersVote(message, user);
            }else{
                playersVote(message, null);
            }
        }else{
            sendMsg(chatId, "Неизвестная команда", null);
        }
    }


    //Управление перед началом игры
    public void adminBeforeStart(String message, long chatId){
        if (message.equals("/start")) {
            sendMsg(chatId, "Приветствую вас, администратор", Keyboards.adminStartPanel());
        }else if (message.equals("Настройки")){
            sendMsg(chatId, "Введите следующие настройки через пробел: \n" +
                    "/set количество игроков \n" +
                    "количество простых заданий \n" +
                    "количество средних \n" +
                    "количество сложных \n" +
                    "перезарядка бластера у убийцы \n" +
                    "перезарядка саботажей \n" +
                    "количество убийц\n" +
                    "перезарядка кнопки\n" +
                    "какая локация недоступна (хранилище, гараж, оружейная, штаб)\n" +
                    "Пример: /set 10 2 2 1 60 60 2 30 штаб", Keyboards.empty()
            );
        }else if(message.equals("Повторить настройки")){
            settingsReady = true;
            sendMsg(chatId, "Повторены настройки предыдущей игры", Keyboards.adminStartPanel());
        }else if (message.length() > 4 && message.substring(0, 4).compareTo("/set") == 0){
            String[] subStr = message.split(" ");
            if (subStr.length != 10)
                sendMsg(chatId, "Неправильный ввод настроек", Keyboards.adminStartPanel());
            else {
                settings.setPlayers(Integer.valueOf(subStr[1]));
                settings.setEasyTasks(Integer.valueOf(subStr[2]));
                settings.setNormalTasks(Integer.valueOf(subStr[3]));
                settings.setTimerTasks(Integer.valueOf(subStr[4]));
                settings.setImposterKD(Integer.valueOf(subStr[5]));
                settings.setSabotageKD(Integer.valueOf(subStr[6]));
                settings.setImpostersCount(Integer.valueOf(subStr[7]));
                settings.setRedButtonKD(Integer.valueOf(subStr[8]));
                sabotage.setUnavailable(subStr[9]);
                sabotage.makeSabotage();
                if(settings.getRedButtonKD() <= settings.getImposterKD())
                    settings.setRedButtonKD(settings.getImposterKD() + 10);
                if(settings.getSabotageKD() >= settings.getRedButtonKD())
                    settings.setSabotageKD(settings.getRedButtonKD() - 10);
                if(settings.getSabotageKD() > settings.getImposterKD())
                    settings.setSabotageKD(settings.getImposterKD() - 10);
                settingsReady = true;
                sendToAdmins("Настройки изменены");
            }
        }else if(message.equals("Сменить цвет")){
            admin.setChangeColor("Admin");
            sendMsg(chatId, "Какой цвет заменить?", Keyboards.changeName(players));
        }else if(admin.getChangeColor().equals("Admin")){
            admin.setChangeColor("null");
            sendMsg(chatId, "Пользователю отправлено сообщение о замене цвета.", Keyboards.adminStartPanel());
            sendMsg(players.getPlayerByColor(message).getChatId(), "Введите свой цвет снова", Keyboards.empty());
            players.getPlayerByColor(message).setColor(null);
        }else if (message.equals("Покажи настройки")){
            sendMsg(chatId, settings.getAllSettings() + sabotage.getSabotageLocations(), Keyboards.adminStartPanel());
        }else if (message.equals("Запуск")) {
            gameStart();
        }else {
            sendMsg(chatId, "Неизвестная команда", Keyboards.adminStartPanel());
        }
    }

    public void userBeforeStart(Update update, String message, User user){
        if(message.equals("/start") && players.getUser(update.getMessage().getChatId()) == null){
            players.addPlayer(new User(update.getMessage().getChatId()));
            sendMsg(update.getMessage().getChatId(), "Здравствуй, игрок, Какой у тебя цвет?", null);
        }else if(players.getPlayers().stream().anyMatch(u -> u.getChatId().equals(user.getChatId())) && user.getColor() == null){
            if (message.equals("/start"))
                sendMsg(user.getChatId(), "Неподходящее имя", Keyboards.empty());
            else if(!settingsReady)
                sendMsg(user.getChatId(), "Инициализация не завершена, подожди немного...", Keyboards.empty());
            else{
                    user.setColor(message);
                    user.setAlive(true);
                    user.setVoted(false);
                    sendToAdmins("Добален " + user.getColor() + " игрок");
                    sendMsg(user.getChatId(), texts.getHelloTexts().get((int) (Math.random() * 100) % 3) + user.getColor(), null);
                    if (players.getPlayers().stream().filter(u -> u.getColor() != null).count() == settings.getPlayers()) {
                        sendToAdmins("Команда укомплектована, можно начинать");
                    }
                }
        }else {
            sendMsg(update.getMessage().getChatId(), "Неизвестная команда", Keyboards.empty());
        }
    }


    //Управление во время игры
    public void adminInGame(String message, long chatId){
        if (message.equals("Голосование")){
            adminStartVote(chatId);
        }else if(message.equals("Перезапуск")){
            reboot();
        }else if(message.equals("Убить")){
            admin.setKill(null);
            sendMsg(chatId, "Кого хотите убить", Keyboards.votePanel(players));
        }else if(admin.getKill() == null){
            adminConfirmKill(message, chatId);
        }else if(message.equals("Воскресить")){
            admin.setMakeAlive(null);
            sendMsg(chatId, "Кого хотите воскресить", Keyboards.makeAlive(players));
        }else if(admin.getMakeAlive() == null){
            adminMakeAlive(message, chatId);
        }else{
            sendMsg(chatId, "Неизвестная команда", Keyboards.adminGamePanel());
        }
    }

    public void crewMemberInGame(String message, User user){
        if(message.equals("Получить задание")){
            if (user.getActiveTaskNum() == 0)
                getTask(user);
            else
                sendMsg(user.getChatId(),
                        texts.getRandomGetTaskText() + user.getActiveTaskNum() + "\n"
                                + user.getActiveTask().getTaskText(),
                        Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }else if (user.getActiveTaskNum() != 0 && message.equals(user.getActiveTask().getCode())){
            completeTask(user);
        }else if (message.equals("Репорт") && user.getAlive()){
            report(user);
        }else if (sabotage.isStatus() && sabotage.getSabotageSolvers().get(sabotage.getType()).stream().anyMatch(u -> u.equals(message))){
            checkSabotage(message, user);
        }else if (message.equals("Убить")){
            sendMsg(user.getChatId(), "Ты был избранником!" +
                    "\nПредрекали что ты уничтожишь ситхов, а не примкнёшь к ним." +
                    "\nВосстановишь равновесие силы, а не ввергнешь её во мрак!", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }else if (user.getActiveTaskNum() != 0 && !message.equals(user.getActiveTask().getCode())){
            sendMsg(user.getChatId(), "Неверный код задания", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }else{
            sendMsg(user.getChatId(), "Неизвестная команда", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }
    }

    public void imposterInGame(String message, User user){
        Date time = new Date();
        if (message.equals("Убить") && user.getAlive()){
            imposterAskKill(user, time);
        }else if(user.getColorToKill() == null){
            imposterConfirmKill(message, user);
        }else if(user.getConfirmColorToKill() == null){
            imposterKill(message, user, time);
        }else if(message.equals("Саботаж")){
            askSabotage(user, time);
        }else if(message.equals("Репорт") && user.getAlive()){
            report(user);
        }else if(sabotage.getType().equals("Саботаж")) {
            checkSabotageStart(message, user);
        }else if(message.equals("Получить задание") && user.getAlive()) {
            imposterGetTask(user);
        }else if (sabotage.isStatus() && Integer.parseInt(message) > 0) {
            checkSabotage(message, user);
        }else{
            sendMsg(user.getChatId(), "Неизвестная команда" ,Keyboards.rolePanel(false, user.getAlive()));
        }
    }


    //Блок голосования
    public void adminStartVote(long chatId){
        if (redButtonReady) {
            redButton = true;
            report(players.getPlayers().get(0));
        } else {
            sendMsg(chatId, "Кнопка экстренного собрания еще не готова", Keyboards.adminGamePanel());
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
                    sendToAdmins("Убит " + killed.get(0));
                    for (int i = 0; i < players.getPlayers().size(); i++) {
                        if (!players.getPlayers().get(i).getColor().equals(killed.get(0))) {
                            sendMsg(players.getPlayers().get(i).getChatId(), "Вы выкинули " + killed.get(0), Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                            //Отправляем сообщение админу о том, кто был выкинут в ходе голосования
                        } else {
                            sendMsg(players.getPlayers().get(i).getChatId(), "К сожалению вас выкинули." +
                                    "\nПройдите к администратору.", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
                        }
                    }
                    sendToAdmins("Голосование завершено\nИгрок " + killed.get(0) + " был выкинут в ходе голосования");
                }
            } else {
                sendToAdmins("Никто не выкинут голосованием");
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
            redButtonReady = false;
            Executors.newCachedThreadPool().submit(() -> {
                try {
                    Thread.sleep(90000);
                    redButtonReady = true;
                    if (gameStatus.equals("game")) {
                        sendToAdmins("Кнопка для начала голосования готова");
                        sendToAlive("Кнопка для начала голосования готова");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            });
            if (sabotage.isBeforeVote())
                startSabotage(sabotage.getType(), players.getUser(starter.getChatId()));
        }
    }


    //Блок админа
    public void adminMakeAlive(String message, long chatId){
        if (!message.equals("Пропустить")) {
            User user = players.getPlayerByColor(message);
            if (!user.getAlive()) {
                user.setAlive(true);
                sendMsg(user.getChatId(), "Вас воскресили, продолжайте игру", Keyboards.rolePanel(user.getRole(), user.getAlive()));
                sendToAdmins("Игрок " + message + " воскрешён администратором");
            } else {
                sendMsg(chatId, "Он и так жив", Keyboards.adminGamePanel());
            }
        }
        else
            sendMsg(chatId, "Ок, пропускаю", Keyboards.adminGamePanel());
        admin.setMakeAlive("Admin");
    }

    public void adminConfirmKill(String message, long chatId){
        if (!message.equals("Пропустить")) {
            User user = players.getPlayerByColor(message);
            if (user.getAlive()) {
                user.setAlive(false);
                sendMsg(user.getChatId(), "Вас убил администратор, подойдите к нему", Keyboards.rolePanel(user.getRole(), user.getAlive()));
                sendToAdmins("Игрок " + message + " убит администратором");
            } else {
                sendMsg(chatId, "Он и так мёртв", Keyboards.adminGamePanel());
            }
        }
        else
            sendMsg(chatId, "Ок, пропускаю", Keyboards.adminGamePanel());
        admin.setKill("Admin");
    }


    //Блок импостера
    public void imposterAskKill(User user, Date time){
        if (time.getTime() - user.getKillTime() > settings.getImposterKD()*1000) {
            user.setColorToKill(null);
            sendMsg(user.getChatId(), "Кому снести башку?", Keyboards.votePanel(players));
        }else{
            sendMsg(user.getChatId(), "Бластер ещё не перезаряжен", Keyboards.rolePanel(false, true));
        }
    }

    public void imposterConfirmKill(String message, User user){
        user.setColorToKill(message);
        user.setConfirmColorToKill(null);
        sendMsg(user.getChatId(),texts.getKillRepeatTexts().get((int)(Math.random()*100)%3), Keyboards.votePanel(players));
    }

    public void imposterKill(String message, User user, Date time){
        if (user.getColorToKill().equals(message) && players.getPlayerByColor(message) != null){
            if(players.getPlayerByColor(message).getRole()) {
                someoneKilled = true;
                players.getPlayerByColor(message).setAlive(false);
                sendToAdmins("Убит " + message);
                sendMsg(user.getChatId(), message + texts.getKillingTexts().get((int)(Math.random()*100)%3), Keyboards.rolePanel(false, true));
                sendMsg(players.getPlayerByColor(message).getChatId(), texts.getDeadTexts().get((int)(Math.random()*100)%3), Keyboards.rolePanel(players.getPlayerByColor(message).getRole(), false));
                user.setKillTime(time.getTime());
                Executors.newCachedThreadPool().submit(() -> {
                    try {
                        Thread.sleep(settings.getImposterKD() * 1000);
                        if (user.getAlive()) {
                            sendMsg(user.getChatId(), "Бластер перезаряжен!", Keyboards.imposterPanel(user.getAlive()));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                });
                user.setTotalKills(user.getTotalKills() + 1);
                checkGameEnd();
            }else{
                sendMsg(user.getChatId(), "Совсем офигел! Огонь по своим!", Keyboards.rolePanel(user.getRole(), user.getAlive()));
            }
        }else{
            sendMsg(user.getChatId(), "Не попал, салага", Keyboards.rolePanel(false, true));
        }
        user.setConfirmColorToKill(message);
    }

    public void askSabotage(User user, Date time){
        if (time.getTime() - user.getSabotageTime() > settings.getSabotageKD()*1000 && !sabotage.isStatus()) {
            sabotage.setType("Саботаж");
            sendMsg(user.getChatId(), "Что хочешь сломать?", Keyboards.sabotagePanel());
        }else{
            sendMsg(user.getChatId(), "Саботаж не готов",Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }
    }

    public void imposterGetTask(User user){
        user.getTask();
        while (true){
            int finalTask = user.getActiveTaskNum();
            if (settings.checkAvailableTasks(finalTask))
                break;
            user.getTask();
        }
        user.setActiveTask(settings.getTask(user.getActiveTaskNum()));
        sendMsg(user.getChatId(), "Задание номер " + user.getActiveTaskNum() + "\n" + user.getActiveTask().getTaskText(), Keyboards.imposterPanel(user.getAlive()));
    }

    public void checkSabotageStart(String message, User user){
        if (sabotage.getSabotageSolvers().containsKey(message)) {
            starter = user;
            user.setTotalSabotages(user.getTotalSabotages() + 1);
            startSabotage(message, user);
        }else{
            sabotage.setType(message);
            sendMsg(user.getChatId(), "Это сделано в СССР, не ломается", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }
    }

    private void startSabotage(String message, User user) {
        Date time = new Date();
        user.setSabotageTime(time.getTime());
        sabotage.setBeforeVote(false);
        sabotage.setType(message);
        sabotage.setStatus(true);
        Executors.newCachedThreadPool().submit(() -> {
            try {
                Thread.sleep(settings.getSabotageKD() * 1000);
                sendMsg(user.getChatId(), "Саботаж перезаряжен!", Keyboards.imposterPanel(user.getAlive()));
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        });
        if (message.equals("Реактор")) {
            Executors.newCachedThreadPool().submit(() -> {
                int counter = 0;
                while (counter <= 90000) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!sabotage.isStatus())
                        break;
                    else if (counter == 30000) {
                        sendToAdmins("До взрыва реактора осталась 1 минута");
                        sendToAlive("До взрыва реактора осталась 1 минута");
                    }
                    else if (counter == 60000) {
                        sendToAdmins("До взрыва реактора осталось 30 секунд");
                        sendToAlive("До взрыва реактора осталось 30 секунд");
                    }
                    else if (counter == 80000) {
                        sendToAdmins("До взрыва реактора осталось 10 секунд");
                        sendToAlive("До взрыва реактора осталось 10 секунд");
                    }
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
                    if (!sabotage.isStatus())
                        break;
                    else if (counter == 30000) {
                        sendToAdmins("До полной утечки кислорода осталась 1 минута");
                        sendToAlive("До полной утечки кислорода осталась 1 минута");
                    }
                    else if (counter == 60000) {
                        sendToAdmins("До полной утечки кислорода осталось 30 секунд");
                        sendToAlive("До полной утечки кислорода осталось 30 секунд");
                    }
                    else if (counter == 80000) {
                        sendToAdmins("До полной утечки кислорода осталось 10 секунд");
                        sendToAlive("До полной утечки кислорода осталось 10 секунд");
                    }
                    counter++;
                }
                checkGameEndBySabotage();
            });
        }
        if (!sabotage.isBeforeVote())
            sendToAdmins("Сломай " + message);
        else
            sendToAdmins("Саботаж " + message + " продолжается");
        sendSabotage(message);
    }


    //Блок экипажа
    public void getTask(User user){
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
            sendToAdmins("Игрок " + user.getColor() + " выполнил все задания!");
        }else {
            user.setActiveTask(settings.getTask(user.getActiveTaskNum()));
            sendToAdmins("Игрок " + user.getColor() + " получил задание " + user.getActiveTaskNum());
            sendMsg(user.getChatId(),
                    texts.getSendingTaskTexts().get((int)(Math.random()*100)%3) + user.getActiveTaskNum() +"\n" + user.getActiveTask().getTaskText(),
                    Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }
    }

    public void completeTask(User user) {
        user.getComplitedTasks().add(user.getActiveTaskNum());
        if (user.getActiveTaskNum() / 10 == 1)
            user.setEasyTasks(user.getEasyTasks() - 1);
        else if (user.getActiveTaskNum() / 10 == 2)
            user.setNormalTasks(user.getNormalTasks() - 1);
        else {
            user.setHardTasks(user.getHardTasks() - 1);
        }
        //sendToAdmins("Игрок " + user.getColor() + " выполнил задание " + user.getActiveTaskNum());
        user.setActiveTaskNum(0);
        sendMsg(user.getChatId(), texts.getCompletingTaskTexts().get((int) (Math.random() * 100) % 3), Keyboards.rolePanel(user.getRole(), user.getAlive()));
        checkGameEndByTasks();
    }


    //Общий блок
    public void report(User user){
        if (redButtonReady || someoneKilled) {
            if (someoneKilled || redButton) {
                if (sabotage.isStatus()){
                    sabotage.setBeforeVote(true);
                    sabotage.setStatus(false);
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
                sendToAdmins("Начинается собрание!");
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

    private void checkSabotage(String message, User user) {
        sabotage.getSabotageSolvers().get(sabotage.getType()).forEach(System.out::println);
        System.out.println(message);
        if (sabotage.getSabotageSolvers().get(sabotage.getType()).stream().anyMatch(u -> u.equals(message))){
            sabotage.sabotageSolvers.get(sabotage.getType()).removeIf(u -> u.equals(message));
            if(sabotage.sabotageSolvers.get(sabotage.getType()).size() == 0)
                if (message.equals("Свет")) {
                    sabotage.sabotageSolvers.remove("Свет");
                    sabotage.makeLightSolvers();
                }
                else if (message.equals("Реактор")) {
                    sabotage.sabotageSolvers.remove("Реактор");
                    sabotage.makeReactorSolvers();
                }
                else if (message.equals("Кислород")) {
                    sabotage.sabotageSolvers.remove("Кислород");
                    sabotage.makeOxygenSolvers();
                }
                else if (message.equals("Связь")) {
                    sabotage.sabotageSolvers.remove("Связь");
                    sabotage.makeLightSolvers();
                }
            sabotage.setStatus(false);
            sendToAdmins(sabotage.getType() + " починен!");
            for (int i = 0; i < players.getPlayers().size(); i++) {
                sendMsg(players.getPlayers().get(i).getChatId(), sabotage.getType() + " починен!", Keyboards.rolePanel(players.getPlayers().get(i).getRole(), players.getPlayers().get(i).getAlive()));
            }
        } else {
            sendMsg(user.getChatId(), "Неверный код", Keyboards.rolePanel(user.getRole(), user.getAlive()));
        }
    }

    public void sendSabotage(String text){
        for (int i = 0; i < players.getPlayers().size(); i++){
            if (players.getPlayers().get(i).getAlive()){
                sendMsg(players.getPlayers().get(i).getChatId(), "Cломали " + text + "\nКод лежит в локации \"" + sabotage.getSabotage().get(text) + "\"",
                         Keyboards.rolePanel(players.getPlayers().get(i).getRole(),true));
            }
        }
    }


    //Начало и конец игры
    public void gameStart(){
        gameTime = new Date();
        gameStatus = "game";
        int impostersCount = 0;
        sendToAdmins("Запускаем игру...");
        while (impostersCount != settings.getImpostersCount()){
            impostersCount = 0;
            players.shuffle();
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
        if (imposterCount > 0)
            sendToAdmins("Предатели: " + String.join(" ", names));
    }

    public void checkGameEndByTasks() {
        if(players.getPlayers().stream().filter(User::getRole).filter(u -> u.getComplitedTasks().size() == u.totalTasks).count() == settings.getPlayers() - settings.getImpostersCount()){
            gameEnd(true);
        }
    }

    public void checkGameEndBySabotage() {
        if (sabotage.isStatus() && (sabotage.getType().equals("Реактор") || sabotage.getType().equals("Кислород"))){
            sendToAdmins((sabotage.getType().equals("Реактор") ? "Реактор взорвался" : "Кислород закончился"));
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
        sendToAdmins( winners ? "Конец игры\nПобедил экипаж" : "Конец игры\nПобедили предатели");
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
        makeStatistics();
        players.reboot();
        sabotage.reboot();
        gameStatus = "init";
        texts = new Texts();
        someoneKilled = false;
        voteResults = new HashMap<>();
        settings = new Settings(settings.getPlayers(),
                settings.getEasyTasks(),
                settings.getNormalTasks(),
                settings.getTimerTasks(),
                settings.getImposterKD(),
                settings.getSabotageKD(),
                settings.getRedButtonKD(),
                settings.getImpostersCount());
        redButton = false;
        redButtonReady = true;
        settingsReady = false;
        for (int i = 0; i < players.getPlayers().size(); i++){
            sendMsg(players.getPlayers().get(i).getChatId(), "Перезапуск игры, введите имя", Keyboards.empty());
        }
        sendToAdmins("Перезапуск");
    }

    public void makeStatistics(){
        int totalTasksComplited = 0;
        for (User player: players.getPlayers()) {
            totalTasksComplited += player.getComplitedTasks().size();
            if(player.getRole())
            sendMsg(player.getChatId(),
                    "Выполнено заданий: " + player.getComplitedTasks().size() + " из " + player.getTotalTasks()
                    , Keyboards.empty());
            else
                sendMsg(player.getChatId(), "Количество убийств: " + player.getTotalKills() + "\n"
                        + "Количество саботажей:" + player.getTotalSabotages(), Keyboards.empty());
        }
        for (User player: players.getPlayers()) {
            sendMsg(player.getChatId(),
                    "Выполнено заданий всем экипажем вместе: " + totalTasksComplited + " из " +
                            player.getTotalTasks()*(settings.getPlayers() - settings.getImpostersCount()),
                    Keyboards.empty());
        }
    }


    //Системное
    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botName;
    }

    public void sendToAlive(String text){
        for (int i = 0; i < players.getPlayers().size(); i++){
            if (players.getPlayers().get(i).getAlive()){
                sendMsg(players.getPlayers().get(i).getChatId(), text, Keyboards.rolePanel(players.getPlayers().get(i).getRole(),true));
            }
        }
    }

    public void sendToAdmins(String text){
        for (int i = 0; i < admin.getChatId().size(); i++){
            sendMsg(admin.getChatId().get(i), text, Keyboards.admiPanel(gameStatus));
        }
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
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
