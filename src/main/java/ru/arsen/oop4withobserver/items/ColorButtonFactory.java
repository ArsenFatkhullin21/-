package ru.arsen.oop4withobserver.items;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ru.arsen.oop4withobserver.Paint;


public class ColorButtonFactory {
    public static Rectangle createColorButton(Color color) {
        Rectangle button = new Rectangle(15, 15);
        if (color != Color.TRANSPARENT) {
            button.setStyle(
                    "-fx-fill: " + toRgbString(color) + "; " +
                            "-fx-border-color: transparent; " +
                            "-fx-background-insets: 0; " +
                            "-fx-effect: none;"
            );
        } else {
            button.setStyle(
                    "-fx-fill: " + "transparent" + "; " +
                            "-fx-border-color: black; " +
                            "-fx-background-insets: 1; " +
                            "-fx-effect: none;"
            );
        }
        button.setOnMouseClicked(actionEvent -> {
            Paint.currentColor = color;

            MyToolBar.currentColorRectangle.setFill(color);
        });


        return button;
    }

    private static String toRgbString(Color color) {
        return "rgb(" +
                (int) (color.getRed() * 255) + "," +
                (int) (color.getGreen() * 255) + "," +
                (int) (color.getBlue() * 255) + ")";
    }
}
