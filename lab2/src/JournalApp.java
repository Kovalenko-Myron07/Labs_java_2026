import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JournalApp {

    // Клас для зберігання адреси
    static class Address {
        private String street;
        private String building;
        private String apartment;

        public Address(String street, String building, String apartment) {
            this.street = street;
            this.building = building;
            this.apartment = apartment;
        }

        @Override
        public String toString() {
            return String.format("вул. %s, буд. %s, кв. %s", street, building, apartment);
        }
    }

    // Сутність "Запис в журналі куратора"
    static class JournalEntry {
        private String lastName;
        private String firstName;
        private LocalDate birthDate;
        private String phone;
        private Address address;

        public JournalEntry(String lastName, String firstName, LocalDate birthDate, String phone, Address address) {
            this.lastName = lastName;
            this.firstName = firstName;
            this.birthDate = birthDate;
            this.phone = phone;
            this.address = address;
        }

        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return String.format("Студент: %s %s | Дата народження: %s | Телефон: %s | Адреса: %s",
                    lastName, firstName, birthDate.format(formatter), phone, address.toString());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<JournalEntry> journal = new ArrayList<>();
        boolean running = true;

        System.out.println("=== Журнал куратора ===");

        while (running) {
            System.out.println("\nОберіть дію:");
            System.out.println("1 - Додати новий запис");
            System.out.println("2 - Показати всі записи");
            System.out.println("0 - Вийти");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addEntry(scanner, journal);
                    break;
                case "2":
                    printJournal(journal);
                    break;
                case "0":
                    running = false;
                    System.out.println("Робота завершена.");
                    break;
                default:
                    System.out.println("Невідома команда. Спробуйте ще раз.");
            }
        }
        
        scanner.close();
    }

    private static void addEntry(Scanner scanner, List<JournalEntry> journal) {
        System.out.println("\n--- Додавання нового студента ---");

        // Валідація кожного поля окремо. Цикл while не пропустить далі, поки дані не будуть правильними.
        String lastName = readStringInput(scanner, "Прізвище: ", "^[А-Яа-яІіЇїЄєҐґA-Za-z'\\- ]+$", 
                "Прізвище має містити лише літери, дефіс або апостроф.");
        
        String firstName = readStringInput(scanner, "Ім'я: ", "^[А-Яа-яІіЇїЄєҐґA-Za-z'\\- ]+$", 
                "Ім'я має містити лише літери, дефіс або апостроф.");
        
        LocalDate birthDate = readDateInput(scanner, "Дата народження (формат ДД.ММ.РРРР): ");
        
        String phone = readStringInput(scanner, "Телефон (наприклад +380991234567 або 0991234567): ", "^(\\+38)?0\\d{9}$", 
                "Невірний формат телефону. Має бути 10 або 12 цифр, починатися з 0 або +380.");
        
        String street = readStringInput(scanner, "Вулиця: ", "^.+$", "Вулиця не може бути порожньою.");
        String building = readStringInput(scanner, "Номер будинку: ", "^[0-9А-Яа-яA-Za-z\\-\\/]+$", "Невірний формат будинку (дозволені цифри, літери, дефіс, дріб).");
        String apartment = readStringInput(scanner, "Номер квартири (якщо приватний будинок, введіть '-'): ", "^.+$", "Поле не може бути порожнім.");

        // Якщо всі дані коректно введені - створюємо сутності та додаємо до списку
        Address address = new Address(street, building, apartment);
        JournalEntry entry = new JournalEntry(lastName, firstName, birthDate, phone, address);
        
        journal.add(entry);
        System.out.println("\nЗапис успішно додано до журналу ");
    }

    private static void printJournal(List<JournalEntry> journal) {
        System.out.println("\n--- Записи в журналі ---");
        if (journal.isEmpty()) {
            System.out.println("Журнал порожній.");
        } else {
            for (int i = 0; i < journal.size(); i++) {
                System.out.println((i + 1) + ". " + journal.get(i).toString());
            }
        }
    }

    private static String readStringInput(Scanner scanner, String prompt, String regex, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.matches(regex)) {
                return input; // Повертаю, якщо валідація успішна
            } else {
                System.out.println("ПОМИЛКА: " + errorMessage);
            }
        }
    }

    /**
     * Окремий метод для зчитування та валідації дати
     */
    private static LocalDate readDateInput(Scanner scanner, String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, formatter); // Намагаюсь перетворити рядок у дату
            } catch (DateTimeParseException e) {
                System.out.println("Невірний формат дати.використовуйте правильний формат(наприклад, 15.09.2002).");
            }
        }
    }
}