package ru.arsen.oop4withobserver.sixlab;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.arsen.oop4withobserver.Paint;
import ru.arsen.oop4withobserver.model.Shape;
import ru.arsen.oop4withobserver.model.ShapeGroup;
import ru.arsen.oop4withobserver.myList.MyList;
import java.io.*;
import java.util.Arrays;

public class ShapeSerializer {

    private static Paint paint = null;

    public ShapeSerializer(Paint paint) {
        this.paint = paint;
    }


    public static boolean saveShapes(MyList<Shape> shapes, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовый файл", "*.txt"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(shapes.size() + "\n");
                for (Shape shape : shapes) {
                    saveShape(writer, shape);
                }
                return true;
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    private static void saveShape(BufferedWriter writer, Shape shape) throws IOException {

        if (shape instanceof ShapeGroup) {
            ShapeGroup shapeGroup = (ShapeGroup) shape;

            writer.write( shapeGroup.getClass().getSimpleName() + " "+ shapeGroup.save() +"\n");
            writer.write( shapeGroup.getShapes().size() + "\n");
            for (Shape groupShape : shapeGroup.getShapes()) {
                saveShape(writer, groupShape);
            }
        } else {

            writer.write(shape.getClass().getSimpleName() + " " + shape.save() + "\n");
        }
    }

    public static void loadShapes(MyList<Shape> shapes, Stage stage, AbstractShapeFactory shapeFactory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовый файл", "*.txt"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                int numShapes = Integer.parseInt(reader.readLine());
                for (int i = 0; i < numShapes; i++) {
                    Shape shape = loadShape(reader, (ShapeFactory) shapeFactory);
                    if (shape != null) {
                        shapes.add(shape);
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при загрузке: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Метод для загрузки одной фигуры, использующий ShapeFactory
    private static Shape loadShape(BufferedReader reader, AbstractShapeFactory shapeFactory) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }

        String[] parts = line.split(" ");
        String shapeType = parts[0];
        String shapeParameters = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

        if (shapeType.equals("ShapeGroup")) {

            ShapeGroup shapeGroup = (ShapeGroup) shapeFactory.createShape(shapeType, shapeParameters);

            int numShapesInGroup = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numShapesInGroup; i++) {
                Shape groupShape = loadShape(reader, shapeFactory); // Рекурсивно загружаем фигуру внутри группы
                if (groupShape != null) {
                    shapeGroup.addShape(groupShape); // Добавляем фигуру в группу
                }
            }
            shapeGroup.addObserver(shape1 -> paint.draw(paint.getGraphicsContext()));
            return shapeGroup;
        } else {
            // Если не группа, используем фабрику для создания фигуры
            Shape shape = shapeFactory.createShape(shapeType, shapeParameters);
            shape.addObserver(shape1 -> paint.draw(paint.getGraphicsContext()));
            return shape;
        }
    }
}