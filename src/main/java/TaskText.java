import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TaskText {
    Map<Integer, String> task = new HashMap<>();
    Map<String, String> sabotage = new HashMap<>();

    public TaskText() {
        makeTasks();
        makeSabotage();
    }


    private void makeSabotage(){
        Map<String, String> sabotage = new HashMap<>();
        sabotage.put("Свет", "Бэтмэн под красным");
        sabotage.put("Реактор", "Под камнем в маньяке");
        sabotage.put("Связь", "Гостинная");
        sabotage.put("Кислород", "Лабиринт на входе");
        this.sabotage = sabotage;
    }
    private void makeTasks(){
        Map<Integer, String> task = new HashMap<>();
        task.put(30, "Поставь вентили в положение 1375");
        task.put(31, "Выбей 55 очков в тире за 30 секунд. После этого введи количество вертикальных слов");
        task.put(33, "Расшифруй сигнал из космоса");
        task.put(29, "Собери код по территории корабля");
        task.put(18, "Проверь работу защитных щитов \n Активируй, а затем верни в исходное положение");
        task.put(28, "Проверь работу защитных щитов \n Активируй, а затем верни в исходное положение");
        task.put(38, "Проверь работу защитных щитов \n Активируй, а затем верни в исходное положение");
        task.put(17, "Собери квадрат, затем верни все в исходное положение");
        task.put(27, "Собери квадрат, затем верни все в исходное положение");
        task.put(37, "Собери квадрат, затем верни все в исходное положение");
        task.put(16, "Реши пример");
        task.put(26, "Реши пример");
        task.put(36, "Реши пример");
        task.put(15, "Расположи цифры в правильном порядке, затем верни в исходное положение");
        task.put(25, "Расположи цифры в правильном порядке, затем верни в исходное положение");
        task.put(35, "Расположи цифры в правильном порядке, затем верни в исходное положение");
        this.task = task;
    }
}
