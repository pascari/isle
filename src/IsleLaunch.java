import isle.Isle;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IsleLaunch {
    public static void main(String[] args) throws IOException {
        Properties appProp = IsleSet.load();
        int EventRange = Integer.parseInt(appProp.getProperty("EventRange"));

        System.out.println("Создание модели острова...");
        System.out.println("Количество локаций: " +
                appProp.getProperty("SizeX") + " x " +
                appProp.getProperty("SizeY"));
        System.out.println("Временной круг событый равен " + EventRange + " мс");
        System.out.println("Вы можете кастамизировать настройки острова в файле settings");
        System.out.println();
        System.out.println("Для запуска программы нажми кнопку Enter...");
        System.in.read();

        Isle isle = new Isle(appProp);
        isle.initialize();
        isle.print();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(isle, 0, EventRange, TimeUnit.MILLISECONDS);
    }
}