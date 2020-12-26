import lombok.Getter;

import java.util.*;

@Getter
public class TaskText {
    Map<Integer, String> task = new HashMap<>();
    Map<String, String> sabotage = new HashMap<>();
    String imposterTasks = "";

    public TaskText() {
        makeTasks();
        makeSabotage();
        List<Integer> keysList = new ArrayList<>(task.keySet());
        for (int i = 0; i  < 3; i++) {
            int randomIndex = new Random().nextInt(keysList.size());
            imposterTasks += keysList.get(randomIndex) + " " + task.get(keysList.get(randomIndex)) + "\n";
        }
    }


    private void makeSabotage(){
        Map<String, String> sabotage = new HashMap<>();
        sabotage.put("Свет", "В столовой");
        sabotage.put("Реактор", "В штабе");
        sabotage.put("Связь", "В хранилище");
        sabotage.put("Кислород", "В Лаборатории");
        this.sabotage = sabotage;
    }
    private void makeTasks(){
        Map<Integer, String> task = new HashMap<>();
        task.put(32, "Собери код по территории");
        task.put(33, "Найди цифру");
        task.put(34, "Каким наименьшем количеством линий можно отделить всех игроков друг от друга всех игроков");
        task.put(35, "Взвесь инфентарь\nНапиши вес самого тяжелого, округляй до десятков");
        task.put(38, "Рассортируй мусор\nНайди все металлические предметы\nВведи их колличество");
        task.put(20, "Собери квадрат, затем верни все в исходное положение");
        task.put(21, "Расшифруй сигнал из космоса");
        task.put(22, "Реши пример Among Us");
        task.put(23, "Вычисли координаты");
        task.put(25, "Построить планеты в порядке удаления от солнца");
        task.put(26, "Отгадай пароль");
        task.put(27, "Посчитай количество треугольников");
        task.put(28, "Найди все цифры в тексте");
        task.put(10, "Что-то не так с порядком букв...");
        task.put(11, "Сколько цифр на картинке?");
        task.put(12, "Проверь работу защитных щитов\nАктивируй, а затем дезактивируй их");
        task.put(13, "Расставь папки в алфавитном порядке, затем верни все в исходное положение");
        task.put(14, "Расположи цифры в правильном порядке");
        task.put(15, "Измерь длинну снаряда попавшего в корабль\nОтвет напиши в см");
        task.put(16, "Напиши номер игрока, который первым добежит до кнопки");
        this.task = task;
    }
}
