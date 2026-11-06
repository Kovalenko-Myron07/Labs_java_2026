import java.util.Arrays;

public class MinDistinctChars {

    public static String[] findWordWithMinDistinctChars(String input) {
        // Якщо рядок порожній або null, повертаємо порожній масив
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }

        // Розбиваємо рядок на масив слів за whitespace-символами
        String[] words = input.trim().split("\\s+");
        
        String targetWord = "";
        long minDistinctCount = Integer.MAX_VALUE;

        // Проходимо по кожному слову
        for (String word : words) {
            // Рахуємо кількість унікальних символів у слові
            long distinctCount = word.chars().distinct().count();
            
            // Якщо знайшли слово з меншою кількістю унікальних символів
            // Використовуємо строго < , щоб у разі рівності залишити ПЕРШЕ слово
            if (distinctCount < minDistinctCount) {
                minDistinctCount = distinctCount;
                targetWord = word;
            }
        }

        // За умовою на виході має бути масив String
        return new String[]{targetWord};
    }

    public static void main(String[] args) {
        // Приклад використання
        String text = "hello world java programming aaaa bbb xyz";
        
        String[] result = findWordWithMinDistinctChars(text);
        
        System.out.println("Вхідний рядок: " + text);
        System.out.println("Результат (масив): " + Arrays.toString(result));
    }
}