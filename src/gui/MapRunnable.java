package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import map.RectangularMap;
import map.elements.animal.AnimalGenerator;

public class MapRunnable implements Runnable {

    Stage window;
    int count = 0;
    RectangularMap map;
    CanvasMap canvasMap;

    public MapRunnable(RectangularMap map) {
        this.map = map;
        canvasMap = new CanvasMap(map);


    }

    @Override
    public void run() {
        count++;
        System.out.println("day: " + count);
        map.circleOfLife();
        canvasMap.refreshMap();
    }
}


