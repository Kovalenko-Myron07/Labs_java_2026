import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

public class Application {
    public static void main(String[] args) {
        Scanner consoleInput = new Scanner(System.in);

        System.out.println("=== БЛОК 1: Пошук рядка з найбільшою кількістю слів ===");
        TextAnalyzerModule.runAnalysis(consoleInput);

        System.out.println("\n=== БЛОК 2: ООП колекція (Збереження та Читання) ===");
        ObjectCollectionModule.runMenu(consoleInput);

        System.out.println("\n=== БЛОК 3: Шифрування символів ===");
        CryptographyModule.runCryptoOperations(consoleInput);

        System.out.println("\n=== БЛОК 4: Парсинг HTML тегів ===");
        WebParserModule.runParser(consoleInput);

        consoleInput.close();
    }
}
class TextAnalyzerModule {
    public static void runAnalysis(Scanner inputReader) {
        System.out.print("Введіть шлях до файлу з текстом (наприклад, data.txt): ");
        String targetPath = inputReader.nextLine().trim();

        try (BufferedReader textStream = new BufferedReader(new FileReader(targetPath))) {
            String currentStr;
            String longestStr = "";
            int maxTokens = 0;
            int currentRow = 0;
            int bestRowIndex = 0;

            while ((currentStr = textStream.readLine()) != null) {
                currentRow++;
                String cleanedStr = currentStr.trim();
                String[] tokens = cleanedStr.isEmpty() ? new String[0] : cleanedStr.split("\\s+");

                if (tokens.length > maxTokens) {
                    maxTokens = tokens.length;
                    longestStr = currentStr;
                    bestRowIndex = currentRow;
                }
            }

            System.out.println("\n[Результат перевірки]");
            if (maxTokens > 0) {
                System.out.println("Рядок №" + bestRowIndex + " містить рекордну кількість слів: " + maxTokens);
                System.out.println("Текст: " + longestStr);
            } else {
                System.out.println("Текст відсутній або файл порожній.");
            }
        } catch (IOException ex) {
            System.out.println("Збій при читанні: " + ex.getMessage());
        }
    }
}
class ObjectCollectionModule {
    public static void runMenu(Scanner inReader) {
        List<EntityModel> records = new ArrayList<>();
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n--- МЕНЮ УПРАВЛІННЯ ОБ'ЄКТАМИ ---");
            System.out.println("1. Додати новий запис");
            System.out.println("2. Показати всі записи");
            System.out.println("3. Зберегти набір об'єктів у файл");
            System.out.println("4. Завантажити набір об'єктів з файлу");
            System.out.println("5. Завершити роботу з меню");
            System.out.print("Ваша дія: ");
            
            String choice = inReader.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Введіть назву сутності: ");
                    String nameVal = inReader.nextLine();
                    System.out.print("Введіть числовий ідентифікатор: ");
                    int idVal = Integer.parseInt(inReader.nextLine().trim());
                    records.add(new EntityModel(nameVal, idVal));
                    System.out.println("Запис додано.");
                    break;
                case "2":
                    System.out.println("Поточний набір даних:");
                    records.forEach(System.out::println);
                    break;
                case "3":
                    System.out.print("Введіть ім'я файлу для запису (наприклад, list.dat): ");
                    String outPath = inReader.nextLine().trim();
                    StorageManager.exportData(outPath, records);
                    break;
                case "4":
                    System.out.print("Введіть ім'я файлу для читання: ");
                    String inPath = inReader.nextLine().trim();
                    Object retrieved = StorageManager.importData(inPath);
                    if (retrieved instanceof List) {
                        // Безпечне приведення типів
                        @SuppressWarnings("unchecked")
                        List<EntityModel> loadedList = (List<EntityModel>) retrieved;
                        records = loadedList;
                        System.out.println("Колекцію успішно оновлено з файлу.");
                    }
                    break;
                case "5":
                    isRunning = false;
                    break;
                default:
                    System.out.println("Невідома команда.");
            }
        }
    }
}

class EntityModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String entityName;
    private final int entityCode;

    public EntityModel(String entityName, int entityCode) {
        this.entityName = entityName;
        this.entityCode = entityCode;
    }

    @Override
    public String toString() {
        return "Сутність [Назва=" + entityName + ", Код=" + entityCode + "]";
    }
}

class CryptographyModule {
    public static void runCryptoOperations(Scanner inputReader) {
        System.out.print("Введіть рядок для кодування: ");
        String rawContent = inputReader.nextLine();
        
        System.out.print("Введіть символ-ключ: ");
        char offsetChar = inputReader.nextLine().charAt(0);
        
        System.out.print("Файл для збереження шифру: ");
        String cryptoFile = inputReader.nextLine().trim();

        performEncoding(rawContent, offsetChar, cryptoFile);
        performDecoding(offsetChar, cryptoFile);
    }

    private static void performEncoding(String payload, char modifier, String destPath) {
        try (EncoderStream outStream = new EncoderStream(new FileWriter(destPath), modifier)) {
            outStream.write(payload);
            System.out.println("[Шифратор] Успішно збережено у " + destPath);
        } catch (IOException ex) {
            System.out.println("Помилка при кодуванні: " + ex.getMessage());
        }
    }

    private static void performDecoding(char modifier, String srcPath) {
        try (DecoderStream inStream = new DecoderStream(new FileReader(srcPath), modifier)) {
            StringBuilder resultBuffer = new StringBuilder();
            int charByte;
            while ((charByte = inStream.read()) != -1) {
                resultBuffer.append((char) charByte);
            }
            System.out.println("[Дешифратор] Отриманий текст: " + resultBuffer.toString());
        } catch (IOException ex) {
            System.out.println("Помилка при декодуванні: " + ex.getMessage());
        }
    }
}

class EncoderStream extends FilterWriter {
    private final int shiftValue;

    protected EncoderStream(Writer baseWriter, char modifier) {
        super(baseWriter);
        this.shiftValue = modifier;
    }

    @Override
    public void write(int singleChar) throws IOException {
        super.write(singleChar + shiftValue);
    }

    @Override
    public void write(char[] buffer, int start, int length) throws IOException {
        for (int idx = start; idx < start + length; idx++) {
            write(buffer[idx]);
        }
    }

    @Override
    public void write(String textSegment, int start, int length) throws IOException {
        for (int idx = start; idx < start + length; idx++) {
            write(textSegment.charAt(idx));
        }
    }
}

class DecoderStream extends FilterReader {
    private final int shiftValue;

    protected DecoderStream(Reader baseReader, char modifier) {
        super(baseReader);
        this.shiftValue = modifier;
    }

    @Override
    public int read() throws IOException {
        int readChar = super.read();
        return (readChar == -1) ? -1 : (readChar - shiftValue);
    }

    @Override
    public int read(char[] buffer, int start, int length) throws IOException {
        int bytesFetched = super.read(buffer, start, length);
        if (bytesFetched == -1) return -1;
        for (int idx = start; idx < start + bytesFetched; idx++) {
            buffer[idx] = (char) (buffer[idx] - shiftValue);
        }
        return bytesFetched;
    }
}

class WebParserModule {
    public static void runParser(Scanner scannerRef) {
        System.out.print("Надайте веб-адресу (напр. https://example.com): ");
        String link = scannerRef.nextLine().trim();

        Map<String, Integer> dictionary = new HashMap<>();

        try {
            URL targetResource = URI.create(link).toURL();
            BufferedReader webReader = new BufferedReader(new InputStreamReader(targetResource.openStream()));
            String webLine;
            
            Pattern htmlPattern = Pattern.compile("<([a-zA-Z0-9]+)[^>]*>");

            while ((webLine = webReader.readLine()) != null) {
                Matcher regexMatcher = htmlPattern.matcher(webLine);
                while (regexMatcher.find()) {
                    String extractedTag = regexMatcher.group(1).toLowerCase();
                    dictionary.put(extractedTag, dictionary.getOrDefault(extractedTag, 0) + 1);
                }
            }
            webReader.close();

            Map<String, Integer> alphabeticallySorted = new TreeMap<>(dictionary);
            System.out.println("\n[Сортування за абеткою]");
            alphabeticallySorted.forEach((key, val) -> System.out.println(key + " -> " + val));

            List<Map.Entry<String, Integer> > volumeSorted = new ArrayList<>(dictionary.entrySet());
            volumeSorted.sort(Map.Entry.comparingByValue());
            System.out.println("\n[Сортування за кількістю входжень]");
            volumeSorted.forEach(item -> System.out.println(item.getKey() + " -> " + item.getValue()));

            System.out.print("\nВкажіть файл для збереження знайденої статистики тегів (напр. tags.dat): ");
            String dumpPath = scannerRef.nextLine().trim();
            StorageManager.exportData(dumpPath, new HashMap<>(dictionary));

        } catch (Exception ex) {
            System.out.println("Не вдалося обробити ресурс: " + ex.getMessage());
        }
    }
}

class StorageManager {
    public static void exportData(String documentPath, Object payload) {
        try (ObjectOutputStream objWriter = new ObjectOutputStream(new FileOutputStream(documentPath))) {
            objWriter.writeObject(payload);
            System.out.println("[Сховище] Дані успішно серіалізовано до: " + documentPath);
        } catch (IOException ex) {
            System.out.println("[Помилка збереження] " + ex.getMessage());
        }
    }

    public static Object importData(String documentPath) {
        try (ObjectInputStream objReader = new ObjectInputStream(new FileInputStream(documentPath))) {
            return objReader.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("[Помилка завантаження] " + ex.getMessage());
            return null;
        }
    }
}