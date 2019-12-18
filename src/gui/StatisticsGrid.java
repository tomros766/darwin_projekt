package gui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class StatisticsGrid extends GridPane {

    Label animalDesc = new Label("Liczba zwierząt na mapie:");
    Label animalCount = new Label();

    Label roundDesc = new Label("Aktualna epoka:");
    Label roundCount = new Label();

    Label grassDesc = new Label("Liczba traw na mapie:");
    Label grassCount = new Label();

    Label avgEnDesc = new Label("Średnia energia zwierząt:");
    Label avgEnVal = new Label();

    Label domGenomDesc = new Label("Dominujący genotyp: ");
    Label domGenomVal = new Label();


    public StatisticsGrid(){


        this.setConstraints(roundDesc,0,0);
        this.setConstraints(roundCount,1,0);
        this.setConstraints(animalDesc,0,1);
        this.setConstraints(animalCount,1,1);
        this.setConstraints(grassDesc,0,2);
        this.setConstraints(grassCount,1,2);
        this.setConstraints(avgEnDesc,0,3);
        this.setConstraints(avgEnVal,1,3);
        this.setConstraints(domGenomDesc,0,4);
        this.setConstraints(domGenomVal,1,4);

        this.getChildren().addAll(animalDesc,animalCount,roundDesc,roundCount,grassDesc,grassCount,avgEnDesc,avgEnVal,domGenomDesc,domGenomVal);
        this.setVgap(10);

    }
}
