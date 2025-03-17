module ru.arsen.oop4withobserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ru.arsen.oop4withobserver to javafx.fxml;
    exports ru.arsen.oop4withobserver;
}