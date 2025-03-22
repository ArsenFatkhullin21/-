package ru.arsen.oop4withobserver.sixlab;

import ru.arsen.oop4withobserver.model.Shape;

public interface AbstractShapeFactory {

      Shape createShape(String shapeName, String data);
}
