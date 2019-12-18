package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import map.RectangularMap;
import map.elements.animal.AnimalGenerator;

public class Main extends Application {

    Stage window;

        private int count = 0;
        private RectangularMap map = new RectangularMap(100,100,1,100,30,0.3);


        @Override
        public void start(Stage primaryStage) {
            BorderPane root = new BorderPane();

            CanvasMap canvasMap = new CanvasMap(map);


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

            for(int i = 0; i < 200; i++){
                generator.generateAnimal(map);
                System.out.println(i);
            }

            for(int i = 0; i < 2000; i++){
                System.out.println(i);
                map.growGrass();
            }


            Scene scene = new Scene(root);

            // longrunning operation runs on different thread
            Thread thread = new Thread(new Runnable() {

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
                            statistics.animalCount.setText(Integer.toString(map.getAnimals().size()));
                            statistics.roundCount.setText(Integer.toString(count));
                            statistics.grassCount.setText(Integer.toString(map.getGrasses().size()));
                            statistics.avgEnVal.setText(Integer.toString(map.getAvgEnergy()));
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
            // don't let thread prevent JVM shutdown
            thread.setDaemon(true);
            thread.start();

            pause.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if(running[0]) {
                        running[0] = false;
                        pause.setText("Wznów");
                        thread.suspend();
                    }
                    else {
                        running[0] = true;
                        pause.setText("Zatrzymaj");
                        thread.resume();
                    }
                }
            });

            primaryStage.setScene(scene);
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }



}
