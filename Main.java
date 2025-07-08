import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {

    public static void main(String[] args) {
        // Пути к архиву и папке для распаковки
        String zipPath = "C://savegames/saves.zip";
        String unpackPath = "C://savegames/";

        openZip(zipPath, unpackPath);

        // Файл для десериализации
        String saveFile = unpackPath + "save1.dat";

        // Десериализуем и выводим состояние игры
        GameProgress progress = openProgress(saveFile);
        if (progress != null) {
            System.out.println("Состояние сохраненной игры:");
            System.out.println(progress);
        }
    }

    public static void openZip(String zipPath, String unpackPath) {
        // Проверяем существование папки для распаковки
        File unpackDir = new File(unpackPath);
        if (!unpackDir.exists()) {
            unpackDir.mkdirs();
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String filePath = unpackPath + entry.getName();
                File file = new File(filePath);

                // Создаем родительские директории
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    System.out.println("Файл распакован: " + filePath);
                } catch (IOException e) {
                    System.err.println("Ошибка при распаковке файла: " + e.getMessage());
                }
                zis.closeEntry();
            }
            System.out.println("Архив успешно распакован в: " + unpackPath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении архива: " + e.getMessage());
        }
    }

    public static GameProgress openProgress(String filePath) {
        GameProgress progress = null;
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            progress = (GameProgress) ois.readObject();
            System.out.println("Сохранение успешно загружено из: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла сохранения: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Класс GameProgress не найден: " + e.getMessage());
        }
        return progress;
    }
}
