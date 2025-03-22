package ru.arsen.oop4withobserver;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.arsen.oop4withobserver.items.MyToolBar;
import ru.arsen.oop4withobserver.model.*;
import ru.arsen.oop4withobserver.myList.MyList;
import ru.arsen.oop4withobserver.observer.ShapeLogger;
import ru.arsen.oop4withobserver.sevenLab.ShapeTreeView;
import ru.arsen.oop4withobserver.sixlab.ShapeSerializer;

import java.util.HashMap;
import java.util.Map;

public class Paint extends Application {


    private boolean dragging = false;
    private boolean resizing = false;
    private int resizingHandle = -1;
    private double lastX, lastY;
    private double startX, startY;

    private ToolBar toolBar = new ToolBar();
    public static String chosenShape = "cursor";
    public static Color currentColor;


    boolean[] shiftIsPressed = {false};
    boolean[] controlIsPressed = {false};
    boolean[] creating ={false};
    public static boolean[] changeColorButton = {false};
    public static boolean[] cursorButton = {false};


    public final MyList<Shape> shapes = new MyList<>();
    public final MyList<Shape> highlightedShapes = new MyList<>();


    Canvas canvas = new Canvas(1000,500);

    private final GraphicsContext gc = canvas.getGraphicsContext2D();
   ;

    ShapeLogger shapeLogger = new ShapeLogger();

    public GraphicsContext getGraphicsContext() {
        return gc;
    }


    @Override
    public void start(Stage primaryStage) {

        ShapeTreeView shapeTreeView = new ShapeTreeView(shapes);
        TreeView<Shape> treeView = shapeTreeView.getTreeView();

        Pane root = new Pane(canvas);

        FlowPane flowPane = new FlowPane(root);

        root.setMinHeight(500);
        root.setPrefSize(1500, 1000);


        MyToolBar myToolBar = new MyToolBar(toolBar, this);
        myToolBar.createToolBar();
        MenuBar menuBar = myToolBar.createMenuBar(shapes,primaryStage);

        VBox vBox = new VBox(menuBar,toolBar,root);

        Scene scene = new Scene(vBox, 1000, 500);



        primaryStage.setOnCloseRequest(event -> {
            // Показываем диалог подтверждения перед закрытием
            boolean confirmed = showConfirmationDialog(primaryStage);
            if (!confirmed) {
                // Отменяем закрытие окна, если пользователь не подтвердил
                event.consume();
            }
        });





        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double newWidthValue = newWidth.doubleValue();
            root.setPrefWidth(newWidthValue);
            canvas.setWidth(newWidthValue);
            draw(gc);
        });

        scene.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            double newHeightValue = newHeight.doubleValue() - toolBar.getHeight(); // Вычитаем высоту ToolBar
            root.setPrefHeight(newHeightValue);
            canvas.setHeight(newHeightValue);
            draw(gc);
        });

        scene.setOnKeyPressed(event -> {

            KeyCode key = event.getCode();
            if (key == KeyCode.SHIFT) {
                shiftIsPressed[0] = true;
            } else if (key == KeyCode.BACK_SPACE) {
                deleteSelectedShapes();
            } else if (key == KeyCode.CONTROL) {
                controlIsPressed[0] = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            KeyCode key = event.getCode();
            if (key == KeyCode.SHIFT) {
                shiftIsPressed[0] = false;
            } else if (key == KeyCode.BACK_SPACE) {
                deleteSelectedShapes();
            } else if (key == KeyCode.CONTROL) {
                controlIsPressed[0] = false;

            }

        });

        KeyCombination commandG = new KeyCodeCombination(KeyCode.G, KeyCombination.META_DOWN);

        scene.getAccelerators().put(commandG, () -> {
            if (highlightedShapes.size() > 1) {


                // Создаем группу
                ShapeGroup group = new ShapeGroup();
                for (Shape shape : highlightedShapes) {
                    group.addShape(shape);
                    shapes.remove(shape);
                    shape.setSelected(false);
                }

                // Очищаем выделенные фигуры
                highlightedShapes.removeAll();

                // Добавляем группу в список фигур
                shapes.add(group);
                System.out.println("Фигур в списке: " + shapes.size());

                draw(gc);
            } else {
                System.out.println("Выберите больше одной фигуры для группировки.");
            }
        });


        KeyCombination commandU = new KeyCodeCombination(KeyCode.U, KeyCombination.META_DOWN);

        scene.getAccelerators().put(commandU, () -> {
            if (highlightedShapes.size() == 1 && highlightedShapes.get(0) instanceof ShapeGroup) {
                ShapeGroup group = (ShapeGroup) highlightedShapes.get(0);
                shapes.remove(group);
                highlightedShapes.remove(group);

                for (Shape shape : group.getShapes()) {
                    shape.setSelected(true);
                    shapes.add(shape);
                    highlightedShapes.add(shape);
                }

                draw(gc);
                System.out.println("Группа разъединена.");
            } else {
                System.out.println("Выберите одну группу для разгруппировки.");
            }
        });




        // Изначально не создаём прямоугольник, будем добавлять их по нажатию
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if ((!controlIsPressed[0]) && (!chosenShape.equals("changeColor")) && (!chosenShape.equals("cursor"))  ) {

                unSelectedShape();

                creating[0] = true;

                startX = event.getX();
                startY = event.getY();

                if(currentColor == null) {
                    currentColor = Color.TRANSPARENT;
                }

                Shape newShape = createSheape(startX, startY, currentColor, 2);
                newShape.addObserver(shape -> {
                    draw(gc);

                });
                newShape.addObserver(shapeLogger);
                shapes.add(newShape);


            } else if (chosenShape.equals("changeColor")) {

                // Создаём новый прямоугольник, где произошло нажатие
                startX = event.getX();
                startY = event.getY();

                for (int i = shapes.size() - 1; i >= 0; i--) {
                    if (!highlightedShapes.isEmpty()){
                        for (Shape shape : highlightedShapes) {
                            shape.chainColor(currentColor);
                        }
                    }
                    Shape shape = shapes.get(i);

                    if (shape.contains(event.getX(), event.getY())) {
                        shape.chainColor(currentColor);


                        break;
                    }
                }



            }

            else if (controlIsPressed[0]) {

                boolean shapeFound = false;
                for (int i = shapes.size() - 1; i >= 0; i--) {
                    Shape shape = shapes.get(i);
                    resizingHandle = shape.getHandleAt(event.getX(), event.getY());

                    if (resizingHandle != -1) {

                        resizing = true;
                        shapeFound = true;
                        break;
                    } else if (shape.contains(event.getX(), event.getY())) {
                        if (!shape.isSelected()) {
                            shape.setSelected(true);
                            highlightedShapes.add(shape);
                            draw(gc);
                        }
                        dragging = true;
                        shapeFound = true;


                        break;
                    }
                }

                if (!shapeFound) {
                    unSelectedShape();
                }

                lastX = event.getX();
                lastY = event.getY();


            }
            else {
                unSelectedShape();
            }
        });



        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if ((creating[0]==true)&&(!chosenShape.equals("cursor"))) {
                Shape lastShape = shapes.get(shapes.size() - 1);

                double width = event.getX() - startX;
                double height = event.getY() - startY;
                if ((lastShape instanceof Square) || (lastShape instanceof Circle)){
                    lastShape.setWidth(width);
                    lastShape.setHeight(width);
                } else {
                    lastShape.setWidth(width);
                    lastShape.setHeight(height);
                }
              // Перерисовываем холст

            } else {

                double dx = event.getX() - lastX;
                double dy = event.getY() - lastY;
                double canvasWidth = canvas.getWidth();
                double canvasHeight = canvas.getHeight();

                // Если фигура выбрана, перемещаем её
                if (dragging && !highlightedShapes.isEmpty()) {


                    // Определяем границы всей выделенной группы фигур
                    double minX = Double.MAX_VALUE;
                    double minY = Double.MAX_VALUE;
                    double maxX = Double.MIN_VALUE;
                    double maxY = Double.MIN_VALUE;

                    for (Shape shape : highlightedShapes) {
                        minX = Math.min(minX, shape.getX());
                        minY = Math.min(minY, shape.getY());
                        maxX = Math.max(maxX, shape.getX() + shape.getWidth());
                        maxY = Math.max(maxY, shape.getY() + shape.getHeight());
                    }

                    // Проверяем, не выходит ли группа фигур за границы
                    double newMinX = minX + dx;
                    double newMinY = minY + dy;
                    double newMaxX = maxX + dx;
                    double newMaxY = maxY + dy;

                    // Корректируем движение группы, если она выходит за границы
                    if (newMinX < 0) dx -= newMinX; // Смещение влево
                    if (newMinY < 0) dy -= newMinY; // Смещение вверх
                    if (newMaxX > canvasWidth) dx -= (newMaxX - canvasWidth); // Смещение вправо
                    if (newMaxY > canvasHeight) dy -= (newMaxY - canvasHeight); // Смещение вниз

                    // Перемещаем все выделенные фигуры с учетом корректированного dx, dy
                    for (Shape shape : highlightedShapes) {
                        shape.move(dx, dy);
                        draw(gc);
                    }
                }

                // Если идет изменение размера, применяем его
                if (resizing) {
                    for (Shape shape : shapes) {
                        if (shape.isSelected()) {
                            double newX = shape.getX() + dx;
                            double newY = shape.getY() + dy;

                            // Ограничиваем координаты внутри холста
                            if (newX < 0) newX = 0;
                            if (newY < 0) newY = 0;
                            if (newX + shape.getWidth() > canvasWidth) newX = canvasWidth - shape.getWidth();
                            if (newY + shape.getHeight() > canvasHeight) newY = canvasHeight - shape.getHeight();
                            shape.resizeFromHandle(resizingHandle, newX - shape.getX(), newY - shape.getY());
                            draw(gc);
                            break;
                        }
                    }
                }

                lastX = event.getX();
                lastY = event.getY();

               ;
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            dragging = false;
            resizing = false;
            resizingHandle = -1;
            creating[0] = false;
        });

         // Начальная отрисовка
        primaryStage.setScene(scene);
        primaryStage.setTitle("Paint App");
        primaryStage.show();

    }


    private void deleteSelectedShapes() {
        shapes.removeAll(highlightedShapes);

        highlightedShapes.removeAll();
        draw(gc);
    }


    private Shape createSheape(double startX, double startY, Color color, int strokeWeight  ) {

        switch (chosenShape){
            case "rectangle":
                if (shiftIsPressed[0]==true) {
                    Square square = new Square(startX, startY, 0, color, strokeWeight);
                    return square;
                }else {
                    Rectangle rectangle = new Rectangle(startX, startY, 0,0 , color, strokeWeight);
                    return rectangle;
                }

            case "circle":
                if (shiftIsPressed[0]==true) {
                    Circle circle = new Circle(startX,startY,0,color,strokeWeight);
                    return circle;
                }else {
                    Ellipse ellipse = new Ellipse(startX,startY,0,0,color,strokeWeight);
                    return ellipse;
                }

            case "triangle":
                if (shiftIsPressed[0]==true) {

                }else {
                    Triangle triangle = new Triangle(startX,startY,0,0,color,strokeWeight);
                    return triangle;
                }
                break;
            case "line":
                Line line;
                if (color==Color.TRANSPARENT) {
                    line = new  Line(startX,startY,0,0,Color.BLACK,strokeWeight);
                }else{
                    line= new Line(startX,startY,0,0,color,strokeWeight);
                }
                return line;


            case "highlightRectangle":
                HighlightRectangle highlightRectangle = new HighlightRectangle(startX,startY,0,0,color,2);
                return highlightRectangle;
            default:
                break;
        }


        return null;
    }


    private void unSelectedShape() {
        for (Shape shape : highlightedShapes) {
            shape.setSelected(false);
            highlightedShapes.removeAll();
        }
    }

    public void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, 1500, 1000);

        // Рисуем все фигуры из списка
        for (Shape shape : shapes) {
            shape.draw(gc);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private boolean showConfirmationDialog(Stage primaryStage) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Вы уверены, что хотите закрыть приложение?");
        alert.setContentText("Все несохраненные данные будут потеряны.");


        ButtonType saveButton = new ButtonType("Сохранить");
        ButtonType cancelButton = new ButtonType("Отмена");
        ButtonType closeButton = new ButtonType("Закрыть без сохранения");

        alert.getButtonTypes().setAll(saveButton, cancelButton, closeButton);


        ButtonType result = alert.showAndWait().orElse(cancelButton);

        if (result == saveButton) {
            boolean saved = ShapeSerializer.saveShapes(shapes, primaryStage);

            if (saved) {

                primaryStage.close();
                return true;
            }
            return false;
        } else if (result == closeButton) {

            primaryStage.close();
            return true;
        }

        return false;
    }

}