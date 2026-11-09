import java.util.ArrayList;
import java.util.List;

class CageFullException extends RuntimeException {
    public CageFullException(String message) {
        super(message);
    }
}

class AnimalNotFoundException extends RuntimeException {
    public AnimalNotFoundException(String message) {
        super(message);
    }
}

abstract class Animal { }
abstract class Mammal extends Animal { }
abstract class Bird extends Animal { }

class Lion extends Mammal { }
abstract class Ungulate extends Mammal { }

class Zebra extends Ungulate { }
class Giraffe extends Ungulate { }
class Eagle extends Bird { }

abstract class Cage<T extends Animal> {
    private final int maxCapacity;
    private final List<T> animals;

    public Cage(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.animals = new ArrayList<>(maxCapacity);
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getOccupiedCount() {
        return animals.size();
    }

    public void addAnimal(T animal) {
        if (animals.size() >= maxCapacity) {
            throw new CageFullException("Вольєр заповнений. Немає вільних місць");
        }
        animals.add(animal);
    }

    public void removeAnimal(T animal) {
        if (!animals.remove(animal)) {
            throw new AnimalNotFoundException("Вказаної тварини немає у цьому вольєрі");
        }
    }
}

abstract class MammalCage<T extends Mammal> extends Cage<T> {
    public MammalCage(int maxCapacity) { super(maxCapacity); }
}

class BirdCage extends Cage<Bird> {
    public BirdCage(int maxCapacity) { super(maxCapacity); }
}

class LionCage extends MammalCage<Lion> {
    public LionCage(int maxCapacity) { super(maxCapacity); }
}

class UngulateCage extends MammalCage<Ungulate> {
    public UngulateCage(int maxCapacity) { super(maxCapacity); }
}

public class Zoo {
    // Використання wildcard <? extends Animal> для зберігання різних вольєрів
    public List<Cage<? extends Animal>> cages = new ArrayList<>();

    public int getCountOfAnimals() {
        int totalAnimals = 0;
        for (Cage<? extends Animal> cage : cages) {
            totalAnimals += cage.getOccupiedCount();
        }
        return totalAnimals;
    }

    public void addCage(Cage<? extends Animal> cage) {
        cages.add(cage);
    }

    public static void main(String[] args) {
        System.out.println("=== ЗАПУСК МОДУЛЬНИХ ТЕСТІВ ЗООПАРКУ ===\n");

        runTest("Тест 1: Вольєр для левів приймає тільки левів", Zoo::testLionCageAcceptsOnlyLions);
        runTest("Тест 2: Вольєр для копитних приймає зебр та жирафів", Zoo::testUngulateCageAcceptsZebrasAndGiraffes);
        runTest("Тест 3: Генерація помилки при переповненні вольєра", Zoo::testCageFullException);
        runTest("Тест 4: Генерація помилки при відсутності тварини", Zoo::testAnimalNotFoundException);
        runTest("Тест 5: Правильний підрахунок всіх тварин у зоопарку", Zoo::testZooTotalAnimalsCount);

        System.out.println("\n=== ТЕСТУВАННЯ ЗАВЕРШЕНО ===");
        
        /* 
         * Щоб показати помилку компіляції (роботу Generics), 
         * треба розкоментувати наступний рядок 
         */
        //LionCage cage = new LionCage(2); cage.addAnimal(new Zebra()); 
    }

    private static void runTest(String testName, Runnable testMethod) {
        System.out.print(testName + " ... ");
        try {
            testMethod.run();
            System.out.println(" тест пройдено");
        } catch (AssertionError e) {
            System.err.println(" тест невдалий: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(" НЕОЧІКУВАНА ПОМИЛКА: " + e.toString());
        }
    }

    private static void testLionCageAcceptsOnlyLions() {
        LionCage lionCage = new LionCage(2);
        lionCage.addAnimal(new Lion());
        
        if (lionCage.getOccupiedCount() != 1) {
            throw new AssertionError("Неправильна кількість левів у вольєрі");
        }
    }

    private static void testUngulateCageAcceptsZebrasAndGiraffes() {
        UngulateCage ungulateCage = new UngulateCage(3);
        ungulateCage.addAnimal(new Zebra());
        ungulateCage.addAnimal(new Giraffe());

        if (ungulateCage.getOccupiedCount() != 2) {
            throw new AssertionError("Неправильна робота вольєра для копитних");
        }
    }

    private static void testCageFullException() {
        BirdCage birdCage = new BirdCage(1);
        birdCage.addAnimal(new Eagle());

        try {
            birdCage.addAnimal(new Eagle()); // Має викликати помилку
            throw new AssertionError("Очікувалось виключення CageFullException, але його не було");
        } catch (CageFullException e) {
        }
    }

    private static void testAnimalNotFoundException() {
        LionCage lionCage = new LionCage(2);
        lionCage.addAnimal(new Lion());

        try {
            lionCage.removeAnimal(new Lion()); // Спроба видалити іншого лева
            throw new AssertionError("Очікувалось виключення AnimalNotFoundException, але його не було");
        } catch (AnimalNotFoundException e) {
            // Тест пройдено
        }
    }

    private static void testZooTotalAnimalsCount() {
        Zoo zoo = new Zoo();

        LionCage lionCage = new LionCage(2);
        lionCage.addAnimal(new Lion());
        lionCage.addAnimal(new Lion());

        UngulateCage ungulateCage = new UngulateCage(5);
        ungulateCage.addAnimal(new Zebra());

        zoo.addCage(lionCage);
        zoo.addCage(ungulateCage);

        int total = zoo.getCountOfAnimals();
        if (total != 3) {
            throw new AssertionError("Очікувалась кількість тварин 3, а отримано: " + total);
        }
    }
}