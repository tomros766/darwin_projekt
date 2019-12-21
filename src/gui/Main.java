package gui;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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

import javafx.scene.control.CheckBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main extends Application {

    Stage window;

    private int count[] = {0,0};
    private ArrayList<RectangularMap> maps = new ArrayList<>();
    ArrayList<CanvasMap> canvasMaps = new ArrayList<>();
    TabPane mapsPane = new TabPane();

    ArrayList<FollowedAnimalGrid> followedAnimalGrids = new ArrayList<>();


    @Override
        public void start(Stage primaryStage) {
            BorderPane root = new BorderPane();
            root.setPrefSize(1366,768);
            Scene scene = new Scene(root);

        try {
            maps.add(parseParameters());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        canvasMaps.add(new CanvasMap(maps.get(0)));
            followedAnimalGrids.add(new FollowedAnimalGrid(canvasMaps.get(0)));

            mapsPane.getTabs().add(new Tab("mapa 1", canvasMaps.get(0)));
            mapsPane.getTabs().get(0).setClosable(false);



            //Panel boczny z opcjami
            VBox rightPanel = new VBox();
            rightPanel.setPadding(new Insets(10,5,10,0));
            rightPanel.setSpacing(10);
            rightPanel.setAlignment(Pos.TOP_CENTER);
            root.setRight(rightPanel);

            Button pause = new Button("Zatrzymaj/Wznów");

            ArrayList <CheckBox> genoTypesBoxes = new ArrayList<>();
            genoTypesBoxes.add(new CheckBox("Zaznacz zwierzęta z dominującym genotypem"));
            addGenoTypeListener(genoTypesBoxes.get(0),0);




            final Boolean[] running = {true, false};    //na sztywno zakładam, że można uruchomić 5 symulacji jednocześnie... shame
            Button writeStatistics = new Button("Zapisz statystyki mapy do pliku");
            writeStatistics.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        generateStatisticsFile(maps.get(mapsPane.getSelectionModel().getSelectedIndex()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });





                //siatka z danymi mapy

            StatisticsGrid statisticsGrid = new StatisticsGrid();

            //koniec panelu z opcjami

        rightPanel.getChildren().addAll(pause,writeStatistics, genoTypesBoxes.get(0),new Separator(),statisticsGrid, new Separator());


        Menu simulations = new Menu("Symulacja");
        simulations.getItems().add(new MenuItem("Nowa symulacja"));
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(simulations);


        simulations.getItems().get(0).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int index = mapsPane.getTabs().size();



                try {
                    maps.add(parseParameters());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CanvasMap newCanvasMap = new CanvasMap(maps.get(index));
                canvasMaps.add(newCanvasMap);
                addPickerListener(newCanvasMap,index,rightPanel);
                followedAnimalGrids.add(new FollowedAnimalGrid(canvasMaps.get(index)));
                mapsPane.getTabs().add(new Tab("mapa " + (mapsPane.getTabs().size() + 1), canvasMaps.get(index)));
                mapsPane.getTabs().get(index).setOnClosed(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        running[index] = false;
                    }
                });

                genoTypesBoxes.add(new CheckBox("Zaznacz zwierzęta z dominującym genotypem"));
                addGenoTypeListener(genoTypesBoxes.get(index),index);
                rightPanel.getChildren().add(1,genoTypesBoxes.get(index));
                rightPanel.getChildren().get(1).setVisible(false);
            }
        });


        // brzydkie do wywalenia






        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {

                    @Override
                    public void run() {
                        for(int i = 0; i < running.length; i++) {

                            if(running[i]){

                                followedAnimalGrids.get(i).managedProperty().bind(mapsPane.getTabs().get(i).selectedProperty());
                                followedAnimalGrids.get(i).visibleProperty().bind(mapsPane.getTabs().get(i).selectedProperty());

                                genoTypesBoxes.get(i).managedProperty().bind(mapsPane.getTabs().get(i).selectedProperty());
                                genoTypesBoxes.get(i).visibleProperty().bind(mapsPane.getTabs().get(i).selectedProperty());

                                count[i]++;
//                                System.out.println("day: " + count);
                                maps.get(i).circleOfLife();
                                maps.get(i).statistics.updateAvgs();
//                                System.out.println(maps.get(i).getAnimals().size());

                                if(maps.get(i).statistics.animalFollowed != null){
                                    if(!rightPanel.getChildren().contains(followedAnimalGrids.get(i)))
                                        rightPanel.getChildren().add(followedAnimalGrids.get(i));


                                    followedAnimalGrids.get(i).updateStatistics();
                                }
                                else{
                                    rightPanel.getChildren().remove(followedAnimalGrids.get(i));
                                }
                            }
                        }


                        for(CanvasMap canvasMap : canvasMaps){
                            canvasMap.refreshMap();
                        }

                        int index = mapsPane.getSelectionModel().getSelectedIndex();
                        statisticsGrid.animalCount.setText(Integer.toString(maps.get(index).getAnimals().size()));
                        statisticsGrid.roundCount.setText(Integer.toString(count[index]));
                        statisticsGrid.grassCount.setText(Integer.toString(maps.get(index).getGrasses().size()));
                        statisticsGrid.avgEnVal.setText(Integer.toString(maps.get(index).statistics.getAvgEnergy()));
                        statisticsGrid.domGenomVal.setText(maps.get(index).statistics.getDominantGenoType().toString());
                        statisticsGrid.domAnimalsVal.setText(Integer.toString(maps.get(index).statistics.countDominantAnimals()));
                        statisticsGrid.avgLifeTimeVal.setText(Integer.toString((int) maps.get(index).statistics.getAvgLifeTime()));
                        statisticsGrid.avgChildrenVal.setText(Integer.toString(maps.get(index).statistics.getAvgChildrenCount()));


                        simulations.getItems().get(0).setDisable(maps.size() >= 2);
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

            addPickerListener(canvasMaps.get(0),0,rightPanel);




            pause.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    int index = mapsPane.getSelectionModel().getSelectedIndex();
                    if(running[index]) {
                        running[index] = false;
                    }
                    else {
                        running[index] = true;
                        rightPanel.getChildren().remove(followedAnimalGrids.get(0));
                    }
                }
            });







        root.setCenter(mapsPane);
        root.setTop(menuBar);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void addPickerListener(CanvasMap canvasMap, int index, VBox panel){
        canvasMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Vector2d position = mousePosition(mouseEvent);
                if(canvasMap.map.getOccupied().containsKey(position) && canvasMap.map.getOccupied().get(position).hasAnimals()){
//                            System.out.println("wybrano zwierzę");
                    followAnimal(canvasMap.map.getOccupied().get(position).getAnimals(),canvasMap.map);
                    canvasMap.refreshMap();
                    if(!panel.getChildren().contains(followedAnimalGrids.get(index)))
                        panel.getChildren().add(followedAnimalGrids.get(index));
                }
                else{
                    if(maps.get(index).statistics.animalFollowed != null) maps.get(index).statistics.animalFollowed.animal.followed = false;
                    maps.get(index).statistics.animalFollowed = null;
                    followedAnimalGrids.get(0).updateStatistics();
                }
            }
        });
    }

    private void addGenoTypeListener(CheckBox checkGenoType, int index){
        checkGenoType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(checkGenoType.isSelected()) canvasMaps.get(index).trackAlphas = true;
                else canvasMaps.get(index).trackAlphas = false;
            }
        });
    }



    private void followAnimal(ArrayList<Animal> animals, RectangularMap map) {
        Animal animal = null;
        int index = maps.indexOf(map);

        if(animals.size() > 1) animal = new AlertBox().display(animals);
        if(animal == null) animal = animals.get(0);

//        System.out.println(animal.position);
        for(Animal an : map.getAnimals()){
            if(an.followed) an.followed = false;
        }
        animal.followed = true;
        map.statistics.animalFollowed = new FollowedAnimal(animal, canvasMaps.get(0));
        followedAnimalGrids.get(index).updateStatistics();
    }

    public Vector2d mousePosition(MouseEvent mouseEvent){
        int xMouse = (int) mouseEvent.getX();
        int yMouse = (int) mouseEvent.getY();
        int xCoord = (int) (xMouse/canvasMaps.get(0).ratio);
        int yCoord = (int) (yMouse/canvasMaps.get(0).ratio);
        return new Vector2d(xCoord, yCoord);
    }

    private void prepareMap(RectangularMap map, int grassOnStart, int animalsOnStart){
        AnimalGenerator generator = new AnimalGenerator();

        for(int i = 0; i < animalsOnStart; i++){
            generator.generateAnimal(map);
        }

        for(int i = 0; i < grassOnStart/2; i++){
            map.growGrass();
        }
    }

    private void generateStatisticsFile(RectangularMap map) throws FileNotFoundException {
        JSONObject statistics = new JSONObject();

        statistics.put("Epoka: ", map.statistics.getRound());
        statistics.put("średnia liczba zwierząt na rundę: ", map.statistics.getAvgAnimalsCount());
        statistics.put("średnia liczba traw na rundę: ", map.statistics.getAvgGrassCount());
        statistics.put("Genotyp dominujący przez największą liczbę rund: ", map.statistics.getDominantGenoType().toString());
        statistics.put("średnia długość życia martwych zwierząt: ", map.statistics.getAvgLifeTime());
        statistics.put("średnia ilość dzieci żyjących zwierząt: ", map.statistics.getAvgChildrenCount());

        PrintWriter statisticsFile = new PrintWriter("mapStatistics.json");
        statisticsFile.write(statistics.toJSONString());

        statisticsFile.flush();
        statisticsFile.close();
    }

    private RectangularMap parseParameters() throws IOException, ParseException {
        Object object = new JSONParser().parse(new FileReader("src/gui/Resources/mapParameters.json"));

        JSONObject parameters = (JSONObject) object;

        int width = Integer.parseInt((String) parameters.get("width"));
        int height = Integer.parseInt((String) parameters.get("height"));
        double startEnergy = Double.parseDouble((String) parameters.get("startEnergy"));
        double moveEnergy = Double.parseDouble((String) parameters.get("moveEnergy"));
        double plantEnergy = Double.parseDouble((String) parameters.get("plantEnergy"));
        double jungleRatio = Double.parseDouble((String) parameters.get("jungleRatio"));
        int grassOnStart = Integer.parseInt((String) parameters.get("grassOnStart"));
        int animalsOnStart = Integer.parseInt((String) parameters.get("animalsOnStart"));

        RectangularMap map = new RectangularMap(width,height,moveEnergy,startEnergy,plantEnergy,jungleRatio);
        prepareMap(map,grassOnStart,animalsOnStart);
        return map;
    }



    public static void main(String[] args) {
            launch(args);
        }



}
