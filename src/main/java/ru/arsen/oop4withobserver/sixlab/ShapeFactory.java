package ru.arsen.oop4withobserver.sixlab;

import javafx.scene.paint.Color;

import ru.arsen.oop4withobserver.Paint;
import ru.arsen.oop4withobserver.model.*;
import ru.arsen.oop4withobserver.myList.MyList;

import java.util.List;

public class ShapeFactory implements AbstractShapeFactory{

  /*  public Shape createShape(String shapeName, String data, MyList<Shape> shapes) {
        String[] params = data.split(" ");

        double x = 0;
        double y = 0;
        double width = 0;
        double height =0;
        Color color = Color.TRANSPARENT;
        int strokeWeight = 0;

        Shape from = null;
        Shape to = null;


        if (!shapeName.equals("ArrowShape")) {

            // Убедимся, что параметров достаточно
            if (params.length < 6) {
                throw new IllegalArgumentException("One or more parameters are invalid: " + data);
            }

            x = Double.parseDouble(params[0].replace(',', '.'));
            y = Double.parseDouble(params[1].replace(',', '.'));
            width = Double.parseDouble(params[2].replace(',', '.'));
            height = Double.parseDouble(params[3].replace(',', '.'));

            // Обработка цвета

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

            strokeWeight = Integer.parseInt(params[5]);
        } else {
            if (params.length < 2) {
                throw new IllegalArgumentException("ArrowShape requires two indices: " + data);
            }

            int indexA = Integer.parseInt(params[0]);
            int indexB = Integer.parseInt(params[1]);

            if (indexA < 0 || indexA >= shapes.size() || indexB < 0 || indexB >= shapes.size()) {
                throw new IndexOutOfBoundsException("Invalid shape index for ArrowShape: " + data);
            }

            from = shapes.get(indexA);
            to= shapes.get(indexB);

        }


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

            case "ArrowShape":

                return new ArrowShape(from,to);
            default:
                return null;
        }
    }*/

    public Shape createShape(String shapeName, String data, MyList<Shape> shapes) {
        String[] params = data.split(" ");


        double x = Double.MAX_VALUE;
        double y = Double.MAX_VALUE;
        double width = 0;
        double height =0;
        Color color = Color.TRANSPARENT;
        int strokeWeight = 0;

        Shape from = null;
        Shape to = null;

        if (!shapeName.equals("ArrowShape")) {

            // Убедимся, что параметров достаточно
            if (params.length < 6) {
                throw new IllegalArgumentException("One or more parameters are invalid: " + data);
            }

            x = Double.parseDouble(params[0]);
            y = Double.parseDouble(params[1]);
            width = Double.parseDouble(params[2]);
            height = Double.parseDouble(params[3]);

            // Обработка цвета

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

            strokeWeight = Integer.parseInt(params[5]);

        } else {
            if (params.length < 2) {
                throw new IllegalArgumentException("ArrowShape requires two indices: " + data);
            }

            int indexA = Integer.parseInt(params[0]);
            int indexB = Integer.parseInt(params[1]);

            if (indexA < 0 || indexA >= shapes.size() || indexB < 0 || indexB >= shapes.size()) {
                throw new IndexOutOfBoundsException("Invalid shape index for ArrowShape: " + data);
            }

            from = shapes.get(indexA);
            to= shapes.get(indexB);
        }

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
            case "ArrowShape":

                return new ArrowShape(from,to);
            default:
                return null;
        }
    }





}
