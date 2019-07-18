package ru.glazkov.task;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

//17:58 Старт
//18:28 Логика задания решина не оптимально.
//19:05 Проведен рефакторинг, изучение многопоточности.
//19:13 Использован метод для паррарельного запуска.
//19:13 Переход к стадии анализа.
//      Последовательный проход оказался быстрее 3 мс(последовательный) vs 5 мс(параллельный) на распарралеливании, увиличим количество файлов
//19:22 Увеличив до 6 файлов показатели изменились, скорость паралельного стрима стала меньше. 6 мс(последовательны) vs 4 мс(параллельный)
//19:24 Подготовительная часть к сдаче задания.

/**
 * Created by Glazkov Anatole (anatoleglazkov@gmail.com) on 18.07.2019.
 */
@DisplayName("Парсинг CSV файла")
class ParseCSV {

    private static final String PATH_TEMPLATE = ".\\src\\main\\resources";
    private List<String> pathFile;
    private Map<String, List<String>> result;

    @BeforeEach
    final void setUp() {
        pathFile = Arrays.asList(
                PATH_TEMPLATE + File.separator + "input1.csv",
                PATH_TEMPLATE + File.separator + "input2.csv",
                PATH_TEMPLATE + File.separator + "input3.csv",
                PATH_TEMPLATE + File.separator + "input4.csv",
                PATH_TEMPLATE + File.separator + "input5.csv",
                PATH_TEMPLATE + File.separator + "input6.csv");
        result = new HashMap<>();
    }

    @AfterEach
    final void clear() {
        result = Collections.emptyMap();
    }

    @Test
    @DisplayName("Процесс парсинга файла")
    void parseCSVOfFiles() {
        long startTime = System.currentTimeMillis();
        pathFile.forEach(path -> {
            try {
                final Scanner fileCSV = new Scanner(new FileReader(path));
                final String[] headers = fileCSV.nextLine().split(";");

                List<String> bodies = new ArrayList<>();
                while (fileCSV.hasNext()) {
                    bodies.add(fileCSV.nextLine());
                }
                int countPos = 0;
                for (String head : headers) {
                    List<String> headValue = new ArrayList<>();
                    for (String body : bodies) {
                        headValue.add(Arrays.asList(body.split(";")).get(countPos));
                    }
                    result.put(head, headValue);
                    countPos++;
                }
                fileCSV.close();
            } catch (FileNotFoundException e) {
                e.getStackTrace();
            }
        });

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
        System.out.println(result);
        Assertions.assertEquals(result.get("nameBox").get(0), "ричард");
    }

    @Test
    @DisplayName("Процесс пррарельного запуска")
    void parseCSVparallel() {
        long startTime = System.currentTimeMillis();
        pathFile.stream().parallel().unordered()
                .forEach(path -> {
                    try {
                        final Scanner fileCSV = new Scanner(new FileReader(path));
                        final String[] headers = fileCSV.nextLine().split(";");
                        List<String> bodies = new ArrayList<>();
                        while (fileCSV.hasNext()) {
                            bodies.add(fileCSV.nextLine());
                        }
                        int countPos = 0;
                        for (String head : headers) {
                            List<String> headValue = new ArrayList<>();
                            for (String body : bodies) {
                                headValue.add(Arrays.asList(body.split(";")).get(countPos));
                            }
                            result.put(head, headValue);
                            countPos++;
                        }
                        fileCSV.close();
                    } catch (FileNotFoundException e) {
                        e.getStackTrace();
                    }
                });
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
        System.out.println(result);
        Assertions.assertEquals(result.get("nameBox").get(0), "ричард");
    }
}
