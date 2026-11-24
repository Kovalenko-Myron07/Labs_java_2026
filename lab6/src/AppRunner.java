import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Клас для роботи з двомовним лексиконом та перекладу тексту
class LexiconConverter {
    private final Map<String, String> vocabularyMap;

    public LexiconConverter() {
        this.vocabularyMap = new HashMap<>();
    }

    // Додавання нової мовної пари
    public void registerEntry(String sourceTerm, String targetTerm) {
        if (sourceTerm != null && targetTerm != null && !sourceTerm.isBlank() && !targetTerm.isBlank()) {
            vocabularyMap.put(sourceTerm.trim().toLowerCase(), targetTerm.trim().toLowerCase());
        }
    }

    // Перетворення вхідного рядка на перекладений текст
    public String processSentence(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "";
        }

        Pattern tokenRegex = Pattern.compile("[a-zA-Z']+");
        Matcher tokenFinder = tokenRegex.matcher(rawText);
        StringBuilder outputBuffer = new StringBuilder();

        while (tokenFinder.find()) {
            String extractedToken = tokenFinder.group();
            String normalizedKey = extractedToken.toLowerCase();

            String mappedValue = vocabularyMap.getOrDefault(normalizedKey, "[" + extractedToken + "]");

            if (Character.isUpperCase(extractedToken.charAt(0)) && !mappedValue.startsWith("[")) {
                mappedValue = Character.toUpperCase(mappedValue.charAt(0)) + mappedValue.substring(1);
            }

            tokenFinder.appendReplacement(outputBuffer, Matcher.quoteReplacement(mappedValue));
        }
        tokenFinder.appendTail(outputBuffer);

        return outputBuffer.toString();
    }

    public int countEntries() {
        return vocabularyMap.size();
    }
}

public class AppRunner {
    public static void main(String[] args) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                new ProcessBuilder("cmd", "/c", "chcp 65001").inheritIO().start().waitFor();
            } catch (Exception ignored) {
            }
        }

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Scanner consoleReader = new Scanner(System.in, StandardCharsets.UTF_8);

        LexiconConverter converter = new LexiconConverter();

        // Початкове заповнення бази слів
        converter.registerEntry("hello", "привіт");
        converter.registerEntry("world", "світ");
        converter.registerEntry("i", "я");
        converter.registerEntry("love", "люблю");
        converter.registerEntry("programming", "програмування");
        converter.registerEntry("java", "джава");
        converter.registerEntry("is", "є");
        converter.registerEntry("great", "чудова");
        converter.registerEntry("language", "мова");
        converter.registerEntry("cat", "кіт");
        converter.registerEntry("dog", "собака");
        converter.registerEntry("and", "і");

        System.out.println("=== Консольний перекладач (EN -> UA) ===");
        System.out.println("У базовому словнику вже є " + converter.countEntries() + " слів.");

        // Поповнення бази користувачем
        System.out.println("\nБажаєте додати нові слова до словника? (так / ні):");
        String userDecision = consoleReader.nextLine().trim().toLowerCase();

        if (userDecision.equals("так") || userDecision.equals("yes") || userDecision.equals("y") || userDecision.equals("+")) {
            System.out.println("Вводьте пари слів. Для завершення натисніть Enter на порожньому рядку.");
            while (true) {
                System.out.print("Англійське слово: ");
                String foreignKey = consoleReader.nextLine().trim();
                if (foreignKey.isEmpty()) {
                    break;
                }

                System.out.print("Український переклад: ");
                String nativeValue = consoleReader.nextLine().trim();
                if (nativeValue.isEmpty()) {
                    System.out.println("Переклад не може бути порожнім. Спробуйте ще раз.");
                    continue;
                }

                converter.registerEntry(foreignKey, nativeValue);
                System.out.println("Додано: " + foreignKey + " -> " + nativeValue);
            }
        }

        // Зчитування речення та виведення результату
        System.out.println("\nВведіть фразу англійською мовою для перекладу:");
        String userSentence = consoleReader.nextLine();

        String finalOutput = converter.processSentence(userSentence);

        System.out.println("\nОригінал: " + userSentence);
        System.out.println("Переклад: " + finalOutput.replaceAll("\\s+", " ").trim());

        consoleReader.close();
    }
}