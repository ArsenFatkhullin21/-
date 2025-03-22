package ru.arsen.oop4withobserver.sevenLab;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import ru.arsen.oop4withobserver.model.Shape;
import ru.arsen.oop4withobserver.myList.MyList;

public class ShapeTreeView {

    private final MyList<Shape> shapes;
    private final TreeView<Shape> treeView;

    public ShapeTreeView(MyList<Shape> shapes) {
        this.shapes = shapes;
        this.treeView = new TreeView<>();
        updateTreeView();
    }

    private void updateTreeView() {
        if (shapes.isEmpty()) {
            treeView.setRoot(null); // Если фигур нет, очищаем дерево
            return;
        }

        // Берем первую фигуру как корень
        TreeItem<Shape> firstItem = new TreeItem<>(shapes.get(0));
        treeView.setRoot(firstItem);

        // Добавляем остальные фигуры как "корневые" элементы
        for (int i = 1; i < shapes.size(); i++) {
            firstItem.getChildren().add(new TreeItem<>(shapes.get(i)));
        }
    }

    public MyList<Shape> getShapes() {
        return shapes;
    }

    public TreeView<Shape> getTreeView() {
        return treeView;
    }
}
