package ru.arsen.oop4withobserver.sixlab;

import ru.arsen.oop4withobserver.model.Shape;
import ru.arsen.oop4withobserver.myList.MyList;

import java.util.List;

public interface AbstractShapeFactory {

      Shape createShape(String shapeName, String data, MyList<Shape> shapes);
}
