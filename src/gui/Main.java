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
import map.MapTools.Vector2d;
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

    private int count[] = {0,0};
    private ArrayList<RectangularMap> maps = new ArrayList<>();
    private ArrayList<CanvasMap> canvasMaps = new ArrayList<>();
    private TabPane mapsPane = new TabPane();
    private ArrayList<FollowedAnimalGrid> followedAnimalGrids = new ArrayList<>();


    @Override
        public void start(Stage primaryStage) {
            BorderPane root = new BorderPane();     //Tworzenie sceny
            root.setPrefSize(1366,768);
            Scene scene = new Scene(root);


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


        //mechanizm zapisywania do pliku
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


        StatisticsGrid statisticsGrid = new StatisticsGrid();                   //siatka z danymi mapy

        rightPanel.getChildren().addAll(pause,writeStatistics, genoTypesBoxes.get(0),new Separator(),statisticsGrid, new Separator()); //dodanie elementów do panelu z opcjami
        //koniec panelu z opcjami


        //dodawanie pierwszej (głównej) symulacji
        try {
            maps.add(getMapFromParameters());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        canvasMaps.add(new CanvasMap(maps.get(0)));
        addPickerListener(canvasMaps.get(0),0,rightPanel);      //obsługa wyboru zwierzęcia do śledzenia
        followedAnimalGrids.add(new FollowedAnimalGrid(canvasMaps.get(0)));

        mapsPane.getTabs().add(new Tab("mapa 1", canvasMaps.get(0)));
        mapsPane.getTabs().get(0).setClosable(false);
        //koenic dodawania głównej symulacji




        final Boolean[] running = {true, false};    //na sztywno zakładam, że można uruchomić tylko 2 symulacje jednocześnie... shame

        //Menu do uruchamiania drugiej symulacji
        Menu simulations = new Menu("Symulacja");
        simulations.getItems().add(new MenuItem("Nowa symulacja"));
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(simulations);



        //obsługa uruchamiania drugiej symulacji
        simulations.getItems().get(0).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int index = mapsPane.getTabs().size();

                try {
                    maps.add(getMapFromParameters());
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
                        canvasMaps.remove(index);
                        maps.remove(index);
                        followedAnimalGrids.remove(index);
                        running[index] = false;
                        rightPanel.getChildren().remove(genoTypesBoxes.get(index));
                    }
                });

                genoTypesBoxes.add(new CheckBox("Zaznacz zwierzęta z dominującym genotypem"));
                addGenoTypeListener(genoTypesBoxes.get(index),index);
                rightPanel.getChildren().add(index,genoTypesBoxes.get(index));
                rightPanel.getChildren().get(index).visibleProperty().bind(mapsPane.getTabs().get(index).selectedProperty());
            }
        });







        //działanie aplikacji
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {

                    @Override
                    public void run() {
                        for(int i = 0; i < running.length; i++) {

                            if(running[i]){

                                followedAnimalGrids.get(i).managedProperty().bind(mapsPane.getTabs().get(i).selectedProperty()); //widoczny i obslugiwany jest tylko panel ze statystykami aktywnej mapy
                                followedAnimalGrids.get(i).visibleProperty().bind(mapsPane.getTabs().get(i).selectedProperty());

                                genoTypesBoxes.get(i).managedProperty().bind(mapsPane.getTabs().get(i).selectedProperty());     //checkbox pokazujący zwierzęta z dominującym genotypem działa tylko dla aktywnej mapy
                                genoTypesBoxes.get(i).visibleProperty().bind(mapsPane.getTabs().get(i).selectedProperty());

                                //właściwy początek epoki
                                count[i]++;
                                maps.get(i).circleOfLife();
                                maps.get(i).statistics.updateAvgs();
                                //koniec epoki

                                //brzydka obsługa wybranego zwierzęcia
                                if(maps.get(i).statistics.animalFollowed != null){
                                    if(!rightPanel.getChildren().contains(followedAnimalGrids.get(i)))
                                        rightPanel.getChildren().add(followedAnimalGrids.get(i));

                                    followedAnimalGrids.get(i).updateStatistics();
                                }
                                else{
                                    rightPanel.getChildren().remove(followedAnimalGrids.get(i));
                                }
                                //koniec brzydkiej obsługi
                            }
                        }


                        for(CanvasMap canvasMap : canvasMaps){              //odświeżenie widoku wszystkich uruchomionych map
                            canvasMap.refreshMap();
                        }

                        //wyświetlanie statystyk dla aktywnej mapy
                        int index = mapsPane.getSelectionModel().getSelectedIndex();
                        statisticsGrid.animalCount.setText(Integer.toString(maps.get(index).getAnimals().size()));
                        statisticsGrid.roundCount.setText(Integer.toString(count[index]));
                        statisticsGrid.grassCount.setText(Integer.toString(maps.get(index).getGrasses().size()));
                        statisticsGrid.avgEnVal.setText(Integer.toString(maps.get(index).statistics.getAvgEnergy()));
                        statisticsGrid.domGenomVal.setText(maps.get(index).statistics.getDominantGenoType().toString());
                        statisticsGrid.domAnimalsVal.setText(Integer.toString(maps.get(index).statistics.countDominantAnimals()));
                        statisticsGrid.avgLifeTimeVal.setText(Integer.toString((int) maps.get(index).statistics.getAvgLifeTime()));
                        statisticsGrid.avgChildrenVal.setText(Integer.toString(maps.get(index).statistics.getAvgChildrenCount()));

                        //jeśli aktywne są dwie mapy, nie można uruchomić więcej
                        simulations.getItems().get(0).setDisable(maps.size() >= 2);
                    }
                };

                while (true) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                    }


                    Platform.runLater(updater);
                }
            }

        });

            thread.setDaemon(true);
            thread.start();





            //obsługa przycisku zatrzymującego symulację
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

        if(animals.size() > 1) animal = new PickAnimalBox().display(animals);
        if(animal == null) animal = animals.get(0);

//        System.out.println(animal.position);
        for(Animal an : map.getAnimals()){
            if(an.followed) an.followed = false;
        }
        animal.followed = true;
        map.statistics.animalFollowed = new FollowedAnimal(animal, canvasMaps.get(0));
        followedAnimalGrids.get(index).updateStatistics();
    }

    private Vector2d mousePosition(MouseEvent mouseEvent){
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
        statistics.put("Genotyp dominujący przez największą liczbę rund: ", map.statistics.getMostDominantGenoType().toString());
        statistics.put("średnia ilość energii zwierzęcia na rundę", map.statistics.getAvgAvgEnergy());
        statistics.put("średnia długość życia martwych zwierząt: ", map.statistics.getAvgLifeTime());
        statistics.put("średnia ilość dzieci żyjących zwierząt: ", map.statistics.getAvgAvgChildrenCount());

        PrintWriter statisticsFile = new PrintWriter("mapStatistics.json");
        statisticsFile.write(statistics.toJSONString());

        statisticsFile.flush();
        statisticsFile.close();
    }

    private RectangularMap getMapFromParameters() throws IOException, ParseException {
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
