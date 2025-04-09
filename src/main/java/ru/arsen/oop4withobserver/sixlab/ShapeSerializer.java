package ru.arsen.oop4withobserver.sixlab;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.arsen.oop4withobserver.Paint;
import ru.arsen.oop4withobserver.model.ArrowShape;
import ru.arsen.oop4withobserver.model.Shape;
import ru.arsen.oop4withobserver.model.ShapeGroup;
import ru.arsen.oop4withobserver.myList.MyList;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShapeSerializer {

    private static Paint paint = null;

    public ShapeSerializer(Paint paint) {
        ShapeSerializer.paint = paint;
    }

    public static boolean saveShapes(MyList<Shape> shapes, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовый файл", "*.txt"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

                writer.write(shapes.size() + "\n");


                List<Shape> contextShapes = new ArrayList<>();

                for (Shape shape : shapes) {
                    saveShape(writer, shape, contextShapes);
                }

                return true;
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    private static void saveShape(BufferedWriter writer, Shape shape, List<Shape> contextShapes) throws IOException {
        if (shape instanceof ShapeGroup group) {

            writer.write("ShapeGroup " + group.save() + "\n");
            writer.write(group.getShapes().size() + "\n");


            for (Shape inner : group.getShapes()) {
                saveShape(writer, inner, contextShapes);
            }


            contextShapes.add(group);
        } else if (shape instanceof ArrowShape arrow) {

            int indexA = contextShapes.indexOf(arrow.getObjectA());
            int indexB = contextShapes.indexOf(arrow.getObjectB());

            writer.write("ArrowShape "  + indexA + " " + indexB + "\n");
        } else {

            writer.write(shape.getClass().getSimpleName() + " " + shape.save() + "\n");
        }


        if (!(shape instanceof ShapeGroup)) {
            contextShapes.add(shape);
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

                // Контекст, чтобы восстанавливать ArrowShape по индексам
                MyList<Shape> contextShapes = new MyList<>();

                for (int i = 0; i < numShapes; i++) {
                    Shape shape = loadShape(reader, (ShapeFactory) shapeFactory, contextShapes);
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

    private static Shape loadShape(BufferedReader reader, AbstractShapeFactory shapeFactory, MyList<Shape> contextShapes) throws IOException {
        String line = reader.readLine();
        if (line == null) return null;

        String[] parts = line.split(" ");
        String shapeType = parts[0];
        String shapeParameters = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

        if (shapeType.equals("ShapeGroup")) {
            ShapeGroup shapeGroup = (ShapeGroup) shapeFactory.createShape(shapeType, shapeParameters, new MyList<>());
            int numInner = Integer.parseInt(reader.readLine());

            for (int i = 0; i < numInner; i++) {
                Shape subShape = loadShape(reader, shapeFactory, contextShapes);
                if (subShape != null) {
                    shapeGroup.addShape(subShape);
                }
            }

            shapeGroup.addObserver(s -> paint.draw(paint.getGraphicsContext()));
            return shapeGroup;

        } else {
            Shape shape = shapeFactory.createShape(shapeType, shapeParameters, contextShapes);
            shape.addObserver(s -> paint.draw(paint.getGraphicsContext()));
            contextShapes.add(shape);
            return shape;
        }
    }
}