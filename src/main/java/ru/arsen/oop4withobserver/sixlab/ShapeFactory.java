package ru.arsen.oop4withobserver.sixlab;

import javafx.scene.paint.Color;

import ru.arsen.oop4withobserver.model.*;

public class ShapeFactory {

    public static Shape createShape(String shapeName, String data) {
        String[] params = data.split(" ");

        // Убедимся, что параметров достаточно
        if (params.length < 6) {
            throw new IllegalArgumentException("One or more parameters are invalid: " + data);
        }


        double x = Double.parseDouble(params[0]);
        double y = Double.parseDouble(params[1]);
        double width = Double.parseDouble(params[2]);
        double height = Double.parseDouble(params[3]);

        // Обработка цвета
        Color color;
        try {
            // Если цвет в формате 0xRRGGBB
            if (params[4].startsWith("0x") || params[4].startsWith("0X")) {
                // Преобразуем 0xRRGGBB в #RRGGBB
                String hexColor = "#" + params[4].substring(2);  // Убираем "0x" и добавляем "#"
                color = Color.web(hexColor);
            } else {
                // В противном случае принимаем строку как есть
                color = Color.valueOf(params[4]);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid color value: " + params[4]);
        }

        int strokeWeight = Integer.parseInt(params[5]);

        // Создаем фигуру в зависимости от типа
        switch (shapeName) {
            case "Rectangle":
                return new Rectangle(x, y, width, height, color, strokeWeight);
            case "Square":
                return new Square(x, y, width, color, strokeWeight);
            case "Circle":
                return new Circle(x, y, width, color, strokeWeight);
            case "Ellipse":
                return new Ellipse(x, y, width, height, color, strokeWeight);
            case "Triangle":
                return new Triangle(x, y, width, height, color, strokeWeight);
            case "Line":
                return new Line(x, y, width, height, color, strokeWeight);
            case "ShapeGroup":
                return new ShapeGroup();
            default:
                return null;
        }
    }
}
