package gui;

import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import map.RectangularMap;
import map.elements.MapElement;

public class MainThreadRunnable implements Runnable {
    CanvasMap canvasMap;
    Thread thread;
    RectangularMap map;
    StatisticsGrid statistics;
    Tooltip tooltip = new Tooltip();

    public MainThreadRunnable(CanvasMap canvasMap, StatisticsGrid statistics){
        this.canvasMap = canvasMap;
        this.thread = new Thread(new CanvasMapRunnable(canvasMap));
        this.map = canvasMap.map;
        this.statistics = statistics;
    }

    @Override
    public void run() {
        Runnable updater = new Runnable() {
            @Override
            public void run() {
//                statistics.animalCount.setText(Integer.toString(map.getAnimals().size()));
//                statistics.roundCount.setText(Integer.toString(canvasMap.round));
//                statistics.grassCount.setText(Integer.toString(map.getGrasses().size()));
//                statistics.avgEnVal.setText(Integer.toString(map.getAvgEnergy()));
//                statistics.domGenomVal.setText(map.getDominantGenoType().toString());
//                statistics.domAnimalsVal.setText(Integer.toString(map.countDominantAnimals()));
//                statistics.avgLifeTimeVal.setText(Integer.toString((int) map.getAvgLifeTime()));
//                statistics.avgChildrenVal.setText(Integer.toString(map.getAvgChildrenCount()));


            }


        };
        thread.setDaemon(true);
        thread.start();
        while(true){
            Platform.runLater(updater);
        }

    }
    private void showGenotype(MapElement animalHovered) {
        Tooltip.install(canvasMap, tooltip);
        tooltip.setText(animalHovered.getAnimals().get(0).getGenoType().toString());
    }
}
