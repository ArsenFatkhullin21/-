package ru.arsen.oop4withobserver.sevenLab;

import ru.arsen.oop4withobserver.model.Shape;
import ru.arsen.oop4withobserver.myList.MyList;

public interface ListObserver {
    void update(MyList list);
}
