import java.util.Arrays;
import java.util.Comparator;

public class MvcShapesApp {

    interface Drawable {
        void draw();
    }

    abstract static class Shape implements Drawable {
        protected String shapeColor;

        public Shape(String shapeColor) {
            this.shapeColor = shapeColor;
        }

        public String getShapeColor() {
            return shapeColor;
        }

        public abstract double calcArea();

        @Override
        public String toString() {
            return "Колір: " + shapeColor;
        }
    }

    static class Rectangle extends Shape {
        private double width;
        private double height;

        public Rectangle(String shapeColor, double width, double height) {
            super(shapeColor);
            this.width = width;
            this.height = height;
        }

        @Override
        public double calcArea() {
            return width * height;
        }

        @Override
        public void draw() {
            System.out.println("Малюємо Прямокутник.");
        }

        @Override
        public String toString() {
            return String.format("Прямокутник [%s, ширина=%.1f, висота=%.1f, площа=%.2f]",
                    super.toString(), width, height, calcArea());
        }
    }

    static class Triangle extends Shape {
        private double base;
        private double height;

        public Triangle(String shapeColor, double base, double height) {
            super(shapeColor);
            this.base = base;
            this.height = height;
        }

        @Override
        public double calcArea() {
            return 0.5 * base * height;
        }

        @Override
        public void draw() {
            System.out.println("Малюємо Трикутник.");
        }

        @Override
        public String toString() {
            return String.format("Трикутник [%s, основа=%.1f, висота=%.1f, площа=%.2f]",
                    super.toString(), base, height, calcArea());
        }
    }

    static class Circle extends Shape {
        private double radius;

        public Circle(String shapeColor, double radius) {
            super(shapeColor);
            this.radius = radius;
        }

        @Override
        public double calcArea() {
            return Math.PI * radius * radius;
        }

        @Override
        public void draw() {
            System.out.println("Малюємо Коло.");
        }

        @Override
        public String toString() {
            return String.format("Коло [%s, радіус=%.1f, площа=%.2f]",
                    super.toString(), radius, calcArea());
        }
    }

    static class ShapeModel {
        private Shape[] shapes;

        public ShapeModel(Shape[] shapes) {
            this.shapes = shapes;
        }

        public Shape[] getShapes() {
            return shapes;
        }
        public double calcTotalArea() {
            double total = 0;
            for (Shape shape : shapes) {
                total += shape.calcArea();
            }
            return total;
        }
        public double calcTotalAreaByType(Class<?> shapeClass) {
            double total = 0;
            for (Shape shape : shapes) {
                if (shapeClass.isInstance(shape)) {
                    total += shape.calcArea();
                }
            }
            return total;
        }

        public void sortByArea() {
            Arrays.sort(shapes, new Comparator<Shape>() {
                @Override
                public int compare(Shape s1, Shape s2) {
                    return Double.compare(s1.calcArea(), s2.calcArea());
                }
            });
        }

        public void sortByColor() {
            Arrays.sort(shapes, new Comparator<Shape>() {
                @Override
                public int compare(Shape s1, Shape s2) {
                    return s1.getShapeColor().compareToIgnoreCase(s2.getShapeColor());
                }
            });
        }
    }

    static class ShapeView {
        public void printMessage(String message) {
            System.out.println("\n" + message);
        }

        public void printShapes(Shape[] shapes) {
            if (shapes == null || shapes.length == 0) {
                System.out.println("Набір фігур порожній.");
                return;
            }
            for (int i = 0; i < shapes.length; i++) {
                System.out.println((i + 1) + ". " + shapes[i].toString());
            }
        }

        public void printArea(String message, double area) {
            System.out.printf("%s %.2f\n", message, area);
        }
    }

    static class ShapeController {
        private ShapeModel model;
        private ShapeView view;

        public ShapeController(ShapeModel model, ShapeView view) {
            this.model = model;
            this.view = view;
        }

        public void execute() {
            // Відображення початкових даних
            view.printMessage("=== Початковий набір даних (10 фігур) ===");
            view.printShapes(model.getShapes());

            // Сумарна площа всіх фігур
            view.printMessage("=== Сумарна площа всіх фігур ===");
            view.printArea("Загальна площа:", model.calcTotalArea());

            // Сумарна площа трикутників
            view.printMessage("=== Сумарна площа фігур типу 'Трикутник' ===");
            view.printArea("Площа трикутників:", model.calcTotalAreaByType(Triangle.class));

            // Впорядкування за площею
            view.printMessage("=== Набір фігур, впорядкований за ЗБІЛЬШЕННЯМ ПЛОЩІ ===");
            model.sortByArea();
            view.printShapes(model.getShapes());

            // Впорядкування за кольором
            view.printMessage("=== Набір фігур, впорядкований ЗА КОЛЬОРОМ ===");
            model.sortByColor();
            view.printShapes(model.getShapes());
            
            // Демонстрація методу draw() для перших 3 відсортованих фігур
            view.printMessage("=== Демонстрація методу draw() ===");
            for (int i = 0; i < 3 && i < model.getShapes().length; i++) {
                model.getShapes()[i].draw();
            }
        }
    }

    public static void main(String[] args) {
        // Підготовлені заздалегідь дані (масив з 10 елементів)
        Shape[] initialShapes = new Shape[] {
            new Rectangle("Червоний", 5.0, 10.0),
            new Circle("Синій", 7.0),
            new Triangle("Зелений", 4.0, 5.0),
            new Rectangle("Жовтий", 2.0, 3.0),
            new Circle("Червоний", 3.0),
            new Triangle("Білий", 6.0, 8.0),
            new Rectangle("Синій", 7.0, 7.0),
            new Circle("Зелений", 1.5),
            new Triangle("Жовтий", 10.0, 12.0),
            new Circle("Білий", 5.5)
        };

        // Ініціалізація MVC
        ShapeModel model = new ShapeModel(initialShapes);
        ShapeView view = new ShapeView();
        ShapeController controller = new ShapeController(model, view);

        controller.execute();
    }
}