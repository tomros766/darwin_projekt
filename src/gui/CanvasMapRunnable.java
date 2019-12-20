package gui;

import javafx.application.Platform;
import map.RectangularMap;

public class CanvasMapRunnable implements Runnable {
    int count = 0;
    RectangularMap map;
    CanvasMap canvasMap;

    public CanvasMapRunnable(CanvasMap canvasMap){
        this.map = canvasMap.map;
        this.canvasMap = canvasMap;
    }

    @Override
    public void run() {
        Runnable updater = new Runnable() {

            @Override
            public void run() {
                count++;
                System.out.println("day: " + count);
                map.circleOfLife();
                System.out.println(map.getAnimals().size());
                canvasMap.refreshMap();
            }
        };

        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
            }

            // UI update is run on the Application thread
            Platform.runLater(updater);
        }
    }
}
