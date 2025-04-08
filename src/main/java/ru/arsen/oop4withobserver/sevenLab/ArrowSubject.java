package ru.arsen.oop4withobserver.sevenLab;

public interface ArrowSubject {

    void addObserver(ArrowObserver o);
    void removeObserver(ArrowObserver o);
    void notifyObservers(double dx, double dy);
}
