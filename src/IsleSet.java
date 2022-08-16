import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class IsleSet {
    public static final String PATH = Objects.requireNonNull(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(""))
            .getPath();
    private static final String SET_PATH = PATH + "settings";

    public static Properties load() {
        Properties islandSet = new Properties();

        try (FileInputStream fis = new FileInputStream(SET_PATH)) {
            islandSet.load(fis);
        } catch (IOException e) {
            System.out.println("Ошибка загрузки файла настроек. Проверь файл settings" +
                    " в корневой директории приложения.");
            e.printStackTrace();
        }
        return islandSet;
    }
}