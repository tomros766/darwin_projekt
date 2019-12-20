package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import map.RectangularMap;
import map.Vector2d;
import map.elements.MapElement;
import map.elements.animal.Animal;
import map.elements.animal.AnimalGenerator;
import map.elements.animal.FollowedAnimal;

import java.util.ArrayList;

public class Main extends Application {

    Stage window;

    private int count = 0;
    private RectangularMap map = new RectangularMap(30,20,1,100,15,0.3);
    CanvasMap canvasMap = new CanvasMap(map);
    FollowedAnimalGrid followedAnimalGrid = new FollowedAnimalGrid(canvasMap);


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

            rightPanel.getChildren().addAll(pause, new Separator());


                //siatka z danymi mapy

            StatisticsGrid statistics = new StatisticsGrid();


            //koniec panelu z opcjami
            rightPanel.getChildren().addAll(statistics, new Separator());


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

                            if(map.statistics.animalFollowed != null){
                                if(!rightPanel.getChildren().contains(followedAnimalGrid))
                                    rightPanel.getChildren().add(followedAnimalGrid);

                                followedAnimalGrid.updateStatistics();
                            }
                            else{
                                rightPanel.getChildren().remove(followedAnimalGrid);
                            }

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
                        if(!rightPanel.getChildren().contains(followedAnimalGrid))
                            rightPanel.getChildren().add(followedAnimalGrid);
                    }
                    else{
                        if(map.statistics.animalFollowed != null) map.statistics.animalFollowed.animal.followed = false;
                        map.statistics.animalFollowed = null;
                        followedAnimalGrid.updateStatistics();
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
                        rightPanel.getChildren().remove(followedAnimalGrid);
                    }
                }
            });

            primaryStage.setScene(scene);
            primaryStage.show();
        }


    private void followAnimal(ArrayList<Animal> animals) {
        Animal animal = null;

        if(animals.size() > 1) animal = new AlertBox().display(animals);
        if(animal == null) animal = animals.get(0);

        System.out.println(animal.position);
        for(Animal an : map.getAnimals()){
            if(an.followed) an.followed = false;
        }
        animal.followed = true;
        map.statistics.animalFollowed = new FollowedAnimal(animal, canvasMap);
        followedAnimalGrid.updateStatistics();
    }

    public Vector2d mousePosition(MouseEvent mouseEvent){
        int xMouse = (int) mouseEvent.getX();
        int yMouse = (int) mouseEvent.getY();
        int xCoord = (int) (xMouse/canvasMap.ratio);
        int yCoord = (int) (yMouse/canvasMap.ratio);
        return new Vector2d(xCoord, yCoord);
    }



    public static void main(String[] args) {
            launch(args);
        }



}
