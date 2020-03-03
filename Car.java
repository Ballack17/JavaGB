import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Car implements Runnable {
    static Semaphore CarsToPass2 = new Semaphore(1); //для определения места, чтоб не было несколько на одном месте, хотя в жизни результаты могут быть равны
    static AtomicInteger Cars_Place = new AtomicInteger(1); //можно было и обычную Инт взять, но здесь чтоб показать, что наверняка! :)
    private static int CARS_COUNT;

    static {
        CARS_COUNT = 0;
    }

    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            MainClass.cdlStart.countDown(); //отсчёт пока все не будут готовы
            try {
                MainClass.cdlFlag.await();   //ожидаем взмаха флага - гонка началась
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        try {
            try {
                CarsToPass2.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(Cars_Place.get() == 1) {System.out.println("THE WINNER IS: " + this.name);}
            else {System.out.println(this.name + " закончил гонку на " + Cars_Place + " МЕСТЕ");}
            Cars_Place.getAndAdd(1);
            MainClass.cdlFinish.countDown();
        } finally {
            CarsToPass2.release();
        }
    }
}



