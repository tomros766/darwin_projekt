package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import map.RectangularMap;
import map.Vector2d;
import map.elements.MapElement;
import map.elements.animal.Animal;
import map.elements.animal.AnimalGenerator;

import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Main extends Application {

    Stage window;

    private int count = 0;
    private RectangularMap map = new RectangularMap(30,20,1,100,15,0.3);
    CanvasMap canvasMap = new CanvasMap(map);
    Tooltip tooltip = new Tooltip();



    @Override
        public void start(Stage primaryStage) {
            BorderPane root = new BorderPane();
            root.setPrefSize(1366,768);
            Scene scene = new Scene(root);



            root.setCenter(canvasMap);





            //Panel boczny z opcjami
            VBox rightPanel = new VBox();
            rightPanel.setPadding(new Insets(10,5,10,0));
            rightPanel.setSpacing(10);
            rightPanel.setAlignment(Pos.TOP_CENTER);
            root.setRight(rightPanel);

            Button pause = new Button("Zatrzymaj");
            final Boolean[] running = {true};

            rightPanel.getChildren().add(pause);


                //siatka z danymi mapy

            StatisticsGrid statistics = new StatisticsGrid();


            //koniec panelu z opcjami
            rightPanel.getChildren().add(statistics);
            AnimalGenerator generator = new AnimalGenerator();

            for(int i = 0; i < 40; i++){
                generator.generateAnimal(map);
                System.out.println(i);
            }

            for(int i = 0; i < 100; i++){
                System.out.println(i);
                map.growGrass();
            }





        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {

                    @Override
                    public void run() {
                        if(running[0]){
                            count++;
                            System.out.println("day: " + count);
                            map.circleOfLife();
                            System.out.println(map.getAnimals().size());
                        }
                        canvasMap.refreshMap();

                        statistics.animalCount.setText(Integer.toString(map.getAnimals().size()));
                        statistics.roundCount.setText(Integer.toString(count));
                        statistics.grassCount.setText(Integer.toString(map.getGrasses().size()));
                        statistics.avgEnVal.setText(Integer.toString(map.statistics.getAvgEnergy()));
                        statistics.domGenomVal.setText(map.statistics.getDominantGenoType().toString());
                        statistics.domAnimalsVal.setText(Integer.toString(map.statistics.countDominantAnimals()));
                        statistics.avgLifeTimeVal.setText(Integer.toString((int) map.statistics.getAvgLifeTime()));
                        statistics.avgChildrenVal.setText(Integer.toString(map.statistics.getAvgChildrenCount()));



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

        });
            // longrunning operation runs on different thread


            // don't let thread prevent JVM shutdown
            thread.setDaemon(true);
            thread.start();

            canvasMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Vector2d position = mousePosition(mouseEvent);
                    if(map.getOccupied().containsKey(position) && map.getOccupied().get(position).hasAnimals()){
                        System.out.println("wybrano zwierzę");
                        followAnimal(map.getOccupied().get(position).getAnimals());
                        canvasMap.refreshMap();
                    }
                    else{
                        if(map.statistics.animalFollowed != null) map.statistics.animalFollowed.followed = false;
                        map.statistics.animalFollowed = null;
                    }
                }
            });

            pause.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if(running[0]) {
                        running[0] = false;
                        pause.setText("Wznów");
                    }
                    else {
                        running[0] = true;
                        pause.setText("Zatrzymaj");
                    }
                }
            });

            primaryStage.setScene(scene);
            primaryStage.show();
        }


    private void followAnimal(ArrayList<Animal> animals) {
        Animal animal = animals.get(0);
        System.out.println(animal.position);
        for(Animal an : map.getAnimals()){
            if(an.followed) an.followed = false;
        }
        animal.followed = true;
        map.statistics.animalFollowed = animal;
    }

    public Vector2d mousePosition(MouseEvent mouseEvent){
        int xMouse = (int) mouseEvent.getX();
        int yMouse = (int) mouseEvent.getY();
        int xCoord = (int) (xMouse/canvasMap.ratio);
        int yCoord = (int) (yMouse/canvasMap.ratio);
        return new Vector2d(xCoord, yCoord);
    }


    private void showGenotype(MapElement animalHovered) {
        Tooltip.install(canvasMap, tooltip);
        tooltip.setText(animalHovered.getAnimals().get(0).getGenoType().toString());
    }

    public static void main(String[] args) {
            launch(args);
        }



}
