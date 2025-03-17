package ru.arsen.oop4withobserver.items;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import ru.arsen.oop4withobserver.Paint;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.arsen.oop4withobserver.model.Shape;
import ru.arsen.oop4withobserver.myList.MyList;
import ru.arsen.oop4withobserver.sixlab.ShapeFactory;
import ru.arsen.oop4withobserver.sixlab.ShapeSerializer;

import java.util.function.BiConsumer;


public class MyToolBar {

    private final ToolBar toolBar;

    public static Rectangle currentColorRectangle = new Rectangle(33,33, Color.TRANSPARENT);

    private final Paint paint;


    public MyToolBar(ToolBar toolBar, Paint paint ) {

        this.toolBar = toolBar;
        this.paint = paint;
    }

    private Canvas createRectangleGraphic(double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, width, height);
        return canvas;
    }

    private Canvas createCircleGraphic(double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.strokeOval(0, 0, width, height);
        return canvas;
    }

    private Canvas createTriangleGraphic(double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        // Определим координаты трех вершин треугольника
        double[] xPoints = {width / 2, 0, width};
        double[] yPoints = {0, height, height};

        // Рисуем треугольник
        gc.strokePolygon(xPoints, yPoints, 3);

        return canvas;
    }

    private Canvas createLineGraphic(double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        gc.strokeLine( width, height,0,0);

        return canvas;
    }

    private Canvas createHighlightRectangleGraphic(double width, double height) {

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setLineDashes(3, 2);  // 10 - длина отрезка, 5 - пробел между отрезками


        gc.setStroke(Color.BLACK);

        gc.strokeRect(0, 0, width, height);
        return canvas;
    }






    public void createToolBar() {
        Button rectangleButton = new Button();
        rectangleButton.setGraphic(createRectangleGraphic(10, 10));
        rectangleButton.setOnAction(actionEvent -> {
            Paint.chosenShape = "rectangle";
        });

        Button circleButton = new Button();
        circleButton.setGraphic(createCircleGraphic(10, 10));
        circleButton.setOnAction(actionEvent -> {
            Paint.chosenShape = "circle";
        });

        Button triangleButton = new Button();
        triangleButton.setGraphic(createTriangleGraphic(10, 10));
        triangleButton.setOnAction(actionEvent -> {
            Paint.chosenShape = "triangle";
        });

        Button lineButton = new Button();
        lineButton.setGraphic(createLineGraphic(10, 10));
        lineButton.setOnAction(actionEvent -> {
            Paint.chosenShape = "line";
        });




        Button cursorButton = new Button();

        Image image = new Image("file:/Users/arsenfatkhulllin/Desktop/learn/OOP/OOP4withObserver/src/main/resources/ru/arsen/oop4withobserver/image.png"); // Путь к изображению на диске
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(12.25);
        imageView.setFitHeight(15);
        // Устанавливаем изображение в кнопку
        cursorButton.setGraphic(imageView);


        cursorButton.setOnMouseClicked(actionEvent -> {
            Paint.chosenShape = "cursor";
        });





        Button highlightRectangleButton = new Button();
        highlightRectangleButton.setGraphic(createHighlightRectangleGraphic(10, 10));
        highlightRectangleButton.setOnAction(actionEvent -> {
            Paint.chosenShape = "highlightRectangle";
        });


        Button changeColorButton = new Button();
        Image image2 = new Image("file:/Users/arsenfatkhulllin/Desktop/learn/OOP/OOP4withObserver/src/main/resources/ru/arsen/oop4withobserver/Group.png"); // Путь к изображению на диске
        ImageView imageView2 = new ImageView(image2);
        imageView2.setFitWidth(12);
        imageView2.setFitHeight(15);
        changeColorButton.setGraphic(imageView2);

        changeColorButton.setOnAction(  actionEvent -> {
            Paint.chosenShape = "changeColor";
        });




        currentColorRectangle.setStroke(Color.BLACK);


        Rectangle redButton = ColorButtonFactory.createColorButton(Color.RED);
        Rectangle transparentButton = ColorButtonFactory.createColorButton(Color.TRANSPARENT);
        Rectangle greenButton = ColorButtonFactory.createColorButton(Color.GREEN);
        Rectangle blueButton = ColorButtonFactory.createColorButton(Color.BLUE);
        Rectangle yellowButton = ColorButtonFactory.createColorButton(Color.YELLOW);
        Rectangle pinkButton = ColorButtonFactory.createColorButton(Color.PINK);
        Rectangle grayButton = ColorButtonFactory.createColorButton(Color.GRAY);
        Rectangle cyanButton = ColorButtonFactory.createColorButton(Color.CYAN);
        Rectangle brownButton = ColorButtonFactory.createColorButton(Color.BROWN);
        Rectangle violetButton = ColorButtonFactory.createColorButton(Color.VIOLET);
        Rectangle darkBlueButton = ColorButtonFactory.createColorButton(Color.DARKBLUE);
        Rectangle lightGreenButton = ColorButtonFactory.createColorButton(Color.LIGHTGREEN);







        GridPane gridPane = new GridPane();
        gridPane.setHgap(3);
        gridPane.setVgap(3);

        gridPane.add(greenButton, 0, 0);
        gridPane.add(transparentButton, 0, 1);
        gridPane.add(redButton, 1, 0);
        gridPane.add(blueButton, 1, 1);
        gridPane.add(violetButton, 2, 0);
        gridPane.add(yellowButton, 2,1);
        gridPane.add(cyanButton, 3, 0);
        gridPane.add(pinkButton, 3,1);
        gridPane.add(grayButton, 4, 0);
        gridPane.add(brownButton, 4,1);
        gridPane.add(darkBlueButton, 5, 0);
        gridPane.add(lightGreenButton, 5,1);










        // Добавляем кнопки в панель инструментов
        toolBar.getItems().addAll(cursorButton,
                rectangleButton, circleButton, triangleButton, lineButton, changeColorButton, currentColorRectangle,
                gridPane);
    }

    public MenuBar createMenuBar(MyList<Shape> shapes, Stage stage) {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("Файл");

        MenuItem openMenuItem = new MenuItem("Открыть");
        openMenuItem.setOnAction(event -> {
            ShapeSerializer.loadShapes(shapes, stage, new ShapeFactory());
            paint.draw(paint.getGraphicsContext());
            System.out.println(shapes);
        });

        MenuItem saveMenuItem = new MenuItem("Сохранить");


        saveMenuItem.setOnAction(event -> ShapeSerializer.saveShapes(shapes, stage));

        fileMenu.getItems().addAll(openMenuItem, saveMenuItem);
        menuBar.getMenus().addAll(fileMenu);

        return menuBar;
    }






}


