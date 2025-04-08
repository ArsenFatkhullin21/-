package ru.arsen.oop4withobserver.sevenLab;

import ru.arsen.oop4withobserver.model.Shape;
import ru.arsen.oop4withobserver.myList.MyList;

public interface ArrowObserver {
    void update(Shape shape, MyList<Shape> shapes);
}
