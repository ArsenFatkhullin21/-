package ru.arsen.oop4withobserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.arsen.oop4withobserver.items.MyToolBar;
import ru.arsen.oop4withobserver.model.*;
import ru.arsen.oop4withobserver.myList.MyList;
import ru.arsen.oop4withobserver.observer.ShapeLogger;
import ru.arsen.oop4withobserver.sixlab.ShapeSerializer;

public class Paint extends Application {


    private static Paint instance;


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


    public final  MyList<Shape> shapes = new MyList<>();
    public final MyList<Shape> highlightedShapes = new MyList<>();
    private TreeView<String> shapeTreeView = new TreeView<>();


    Canvas canvas = new Canvas(1000,500);

    private final GraphicsContext gc = canvas.getGraphicsContext2D();
   ;

    ShapeLogger shapeLogger = new ShapeLogger();

    TreeItem<String> rootItem = new TreeItem<>("Shapes");


    public GraphicsContext getGraphicsContext() {
        return gc;
    }


    @Override
    public void start(Stage primaryStage) {


        Pane root = new Pane(canvas);


        root.setMinHeight(500);
        root.setPrefSize(1500, 1000);


        MyToolBar myToolBar = new MyToolBar(toolBar, this);
        myToolBar.createToolBar();
        MenuBar menuBar = myToolBar.createMenuBar(shapes,primaryStage);

        rootItem.setExpanded(true);


        shapeTreeViewHandler();


        shapes.addObserver(list -> rebuildTree(rootItem));


        HBox contentBox = new HBox(shapeTreeView,root);
        VBox vBox = new VBox(menuBar,toolBar,contentBox);
        Scene scene = new Scene(vBox, 1000, 500);

        closeHandler(primaryStage);

        formSizeListener(scene, root);

        keysHandler(scene);

        mouseHandler();





        primaryStage.setScene(scene);
        primaryStage.setTitle("Paint App");
        primaryStage.show();

    }

    private void shapeTreeViewHandler() {
        shapeTreeView.setPrefWidth(200);
        shapeTreeView.setMaxWidth(200);
        shapeTreeView.setRoot(rootItem);
        shapeTreeView.setShowRoot(true);

        shapeTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {

                String selectedName = newItem.getValue();

                // Ищем фигуру по имени в списке shapes
                for (Shape shape : shapes) {
                    String shapeName = shape.getClass().getSimpleName() + " " + shape.getX() + " " + shape.getY();
                    if (shapeName.equals(selectedName)) {
                        // Если нашли соответствующую фигуру, выделяем её
                        shape.setSelected(true);
                        if (!shiftIsPressed[0]) {
                            highlightedShapes.removeAll();
                            for (Shape shape1 : shapes) {
                                shape1.setSelected(false);
                            }
                        }

                        highlightedShapes.add(shape);
                        for (Shape hs: highlightedShapes){
                            hs.setSelected(true);
                        }
                        draw(gc);
                        break;
                    }
                }
            }
        });

    }

    private void closeHandler(Stage primaryStage) {
        primaryStage.setOnCloseRequest(event -> {
            // Показываем диалог подтверждения перед закрытием
            boolean confirmed = showConfirmationDialog(primaryStage);
            if (!confirmed) {
                // Отменяем закрытие окна, если пользователь не подтвердил
                event.consume();
            }
        });
    }

    public static Paint getInstance() {
        if (instance == null) {
            instance = new Paint();
        }
        return instance;
    }

    Shape selectedObjectA = null;
    Shape selectedObjectB = null;
    boolean firstSelection = true;

    private void mouseHandler() {
        // Изначально не создаём прямоугольник, будем добавлять их по нажатию
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (chosenShape.equals("ArrowShape")) {

                Shape clickedShape = findShapeAtCoordinates(event.getX(), event.getY());

                if (clickedShape != null) {
                    clickedShape.setSelected(true);
                    highlightedShapes.add(clickedShape);

                    if (firstSelection) {
                        selectedObjectA = clickedShape;
                        firstSelection = false;
                    } else {
                        selectedObjectB = clickedShape;

                        // Создаем стрелку между двумя выбранными объектами
                        ArrowShape arrow = new ArrowShape(selectedObjectA, selectedObjectB);
                        shapes.add(arrow);

                        // Сброс выбора
                        selectedObjectA = null;
                        selectedObjectB = null;
                        firstSelection = true;

                        // Подождать и снять выделение
                        new Thread(() -> {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // UI-операции должны выполняться в FX Application Thread
                            Platform.runLater(() -> {
                                unSelectedShape();
                                draw(gc);
                            });
                        }).start();
                    }
                }
            }else if ((!controlIsPressed[0]) && (!chosenShape.equals("changeColor")) && (!chosenShape.equals("cursor"))  ) {

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
            if ((creating[0])&&(!chosenShape.equals("cursor"))) {
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
    }

    private Shape findShapeAtCoordinates(double x, double y) {
        for (Shape shape : shapes) {
            if (shape.contains(x, y)) {
                return shape;  // Возвращаем объект, если он содержит точку
            }
        }
        return null;  // Возвращаем null, если объект не найден
    }

    private void keysHandler(Scene scene) {
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

                // Сначала удаляем группу
                shapes.remove(group);
                highlightedShapes.remove(group);

                // Собираем стрелки для удаления
                MyList<ArrowShape> arrowsToRemove = new MyList<>();

                // Перебираем все элементы группы
                for (Shape shape : group.getShapes()) {
                    shape.setSelected(true);

                    // Перебираем все стрелки
                    for (Shape sh : shapes) {
                        if (sh instanceof ArrowShape) {
                            ArrowShape arrowShape = (ArrowShape) sh;
                            Shape objA = arrowShape.getObjectA();
                            Shape objB = arrowShape.getObjectB();

                            // Если фигура является источником или целью стрелки, то помечаем стрелку для удаления
                            if (group.equals(objA) || group.equals(objB)) {
                                arrowsToRemove.add(arrowShape);
                            }
                        }
                    }

                    // Добавляем фигуру обратно в список фигур
                    shapes.add(shape);
                    highlightedShapes.add(shape);
                }

                // Удаляем собранные стрелки после завершения итерации
                for (ArrowShape arrowShape : arrowsToRemove) {
                    shapes.remove(arrowShape);
                }

                draw(gc);
                System.out.println("Группа разъединена.");
            } else {
                System.out.println("Выберите одну группу для разгруппировки.");
            }
        });
    }

    private void formSizeListener(Scene scene, Pane root) {
        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double newWidthValue = newWidth.doubleValue()-200;
            root.setPrefWidth(newWidthValue);
            canvas.setWidth(newWidthValue);
            draw(gc);
        });

        scene.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            double newHeightValue = newHeight.doubleValue() - toolBar.getHeight()-30;
            root.setPrefHeight(newHeightValue);
            canvas.setHeight(newHeightValue);
            draw(gc);
        });
    }


    private void deleteSelectedShapes() {
        // Сначала обрабатываем удаление стрелок, связанных с выбранными фигурами
        MyList<Shape> toRemove = new MyList<>();

        for (Shape s : shapes) {
            if (s instanceof ArrowShape arrow) {
                // Проверяем, связывает ли эта стрелка любую из выбранных фигур или элементов группы
                for (Shape highlightedShape : highlightedShapes) {
                    if (arrow.getObjectA() == highlightedShape || arrow.getObjectB() == highlightedShape) {
                        toRemove.add(arrow);
                        break;
                    }


                    if (highlightedShape instanceof ShapeGroup group) {
                        for (Shape groupMember : group.getShapes()) {
                            if (arrow.getObjectA() == groupMember || arrow.getObjectB() == groupMember) {
                                toRemove.add(arrow);
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Удаляем собранные стрелки и отменяем регистрацию их наблюдателей
        for (Shape s : toRemove) {
            if (s instanceof ArrowShape) {
                ((ArrowShape) s).removeObservers();
                removeArrowsIfObjectRemoved(s);
                shapes.remove(s);
            }
        }

        // Удаляем выбранные фигуры
        for (Shape shape : highlightedShapes) {
            shapes.remove(shape);
            shape.setSelected(false);
        }


        highlightedShapes.removeAll();


        draw(gc);
    }






    private Shape createSheape(double startX, double startY, Color color, int strokeWeight) {

        switch (chosenShape){
            case "rectangle":
                if (shiftIsPressed[0]) {
                    return new Square(startX, startY, 0, color, strokeWeight);
                }else {
                    return new Rectangle(startX, startY, 0,0 , color, strokeWeight);
                }

            case "circle":
                if (shiftIsPressed[0]) {
                    return new Circle(startX,startY,0,color,strokeWeight);
                }else {
                    return new Ellipse(startX,startY,0,0,color,strokeWeight);
                }

            case "triangle":
                if (shiftIsPressed[0]) {

                }else {
                    return new Triangle(startX,startY,0,0,color,strokeWeight);
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
                return new HighlightRectangle(startX,startY,0,0,color,2);



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


        for (Shape shape : shapes) {
            shape.draw(gc);
        }


        TreeItem<String> selectedItem = findTreeItemByShape(shapeTreeView.getRoot(), highlightedShapes.isEmpty() ? null : highlightedShapes.get(0));
        if (selectedItem != null) {
            shapeTreeView.getSelectionModel().select(selectedItem);
        }
    }

    private TreeItem<String> findTreeItemByShape(TreeItem<String> root, Shape shape) {
        if (root == null || shape == null) return null;

        // Формируем строку для сравнения: имя фигуры и её координаты
        String shapeName = shape.getClass().getSimpleName() + " " + shape.getX() + " " + shape.getY();
        if (root.getValue().equals(shapeName)) return root;

        // Рекурсивно ищем в поддеревьях
        for (TreeItem<String> child : root.getChildren()) {
            TreeItem<String> found = findTreeItemByShape(child, shape);
            if (found != null) return found;
        }
        return null;
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
            ShapeSerializer serializer = new ShapeSerializer(this);
            boolean saved = serializer.saveShapes(shapes, primaryStage);

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


    private void rebuildTree(TreeItem<String> rootItem) {
        rootItem.getChildren().clear();
        for (Shape shape : shapes) {
            processNode(rootItem, shape);  // Добавляем новые элементы
        }
        shapeTreeView.setRoot(rootItem);
    }

    private void processNode(TreeItem<String> parentItem, Shape shape) {
        String name =
                shape.getClass().getSimpleName() + " " + shape.getX() + " " + shape.getY();


        TreeItem<String> item = new TreeItem<>(name);
        parentItem.getChildren().add(item);


        if (shape instanceof ShapeGroup) {
            ShapeGroup group = (ShapeGroup) shape;
            for (Shape child : group.getShapes()) {
                processNode(item, child);
            }
        }
    }

    public void removeArrowsIfObjectRemoved(Shape removedShape) {
        MyList<ArrowShape> toRemove = new MyList<>();

        // Проверяем все стрелки, чтобы удалить те, которые ссылаются на удаленные объекты
        for (Shape shape : shapes) {
            if (shape instanceof ArrowShape) {
                ArrowShape arrow = (ArrowShape) shape;
                if (arrow.getObjectA() == removedShape || arrow.getObjectB() == removedShape) {
                    toRemove.add(arrow);
                }
            }
        }

        // Удаляем стрелки из списка
        for (ArrowShape arrow : toRemove) {
            arrow.removeObservers();
            shapes.remove(arrow);
            System.out.println("Стрелка удалена, так как один из объектов исчез.");
        }
    }




}