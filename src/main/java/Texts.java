
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Getter
public class Texts {
    private List<String> gettingTaskTexts= new ArrayList<>();
    private List<String> completingTaskTexts = new ArrayList<>();
    private List<String> killingTexts = new ArrayList<>();
    private List<String> sendingTaskTexts = new ArrayList<>();
    private List<String> deadTexts = new ArrayList<>();
    private List<String> killRepeatTexts = new ArrayList<>();
    private List<String> helloTexts = new ArrayList<>();
    private List<String> reportTexts = new ArrayList<>();

    public Texts() {
        makeHelloTexts();
        makeComletingTaskTexts();
        makeGettiingTaskTexts();
        makeKillingTexts();
        makeKillRepeatTexts();
        makeSendingTaskTexts();
        makeDeadTexts();
        makeReportTexts();
    }

    public void makeGettiingTaskTexts() {
        List<String> gettingTaskTexts= new ArrayList<>();
        gettingTaskTexts.add("Я напишу тебе, как будет задание...");
        gettingTaskTexts.add("Посмотрим что есть для тебя...");
        gettingTaskTexts.add("Подбираю самое подходящее задание...");
        this.gettingTaskTexts = gettingTaskTexts;
    }

    public void makeComletingTaskTexts() {
        List<String> competingTaskTexts = new ArrayList<>();
        competingTaskTexts.add("Задание выполнено!");
        competingTaskTexts.add("Принято, может хочешь ещё?");
        competingTaskTexts.add("Да, всё верно");
        this.completingTaskTexts = competingTaskTexts;
    }

    public void makeSendingTaskTexts(){
        List<String> sendingTaskTexts = new ArrayList<>();
        sendingTaskTexts.add("Срочно! задание номер ");
        sendingTaskTexts.add("Тебе надо выполнить задание номер ");
        sendingTaskTexts.add("Мне нужно чтобы ты сделал задание номер ");
        this.sendingTaskTexts = sendingTaskTexts;
    }

    public void makeKillingTexts() {
        List<String> killingTexts = new ArrayList<>();
        killingTexts.add(" убит, валим!");
        killingTexts.add(" был славным членом экипажа, но ты его убил.");
        killingTexts.add(" убит точным выстрелом!");
        this.killingTexts = killingTexts;
    }

    public void makeDeadTexts() {
        List<String> deadTexts = new ArrayList<>();
        deadTexts.add("Вас убили, какая жалость");
        deadTexts.add("Тебя убили. Вечная память.");
        deadTexts.add("Упс! Кажется тебя убили");
        this.deadTexts = deadTexts;
    }

    public void makeKillRepeatTexts() {
        List<String> killRepeatTexts = new ArrayList<>();
        killRepeatTexts.add("Забыл снять предохранитель, повтори");
        killRepeatTexts.add("Не понял, кого убить?");
        killRepeatTexts.add("Бластер перегрелся, повтори");
        this.killRepeatTexts = killRepeatTexts;
    }

    public void makeHelloTexts() {
        List<String> helloTexts = new ArrayList<>();
        helloTexts.add("Привет, ");
        helloTexts.add("Добро пожаловать на борт, ");
        helloTexts.add("Рад тебя видеть, ");
        this.helloTexts = helloTexts;
    }

    public void makeReportTexts() {
        List<String> reportTexts = new ArrayList<>();
        reportTexts.add("Собрание!");
        reportTexts.add("Что-то случилось, срочно собираемся!");
        reportTexts.add("Андрюха, у нас труп! Возможно криминал. По коням!");
        this.reportTexts = reportTexts;
    }

    public String getRandomGetTaskText(){
        return sendingTaskTexts.get((int)(Math.random()*100)%3);
    }
}
