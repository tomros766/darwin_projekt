package gui;

import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

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

    Label domAnimalsDesc = new Label("Liczba zwierząt posiadających dominujący genotyp: ");
    Label domAnimalsVal = new Label();

    Label avgLifeTimeDesc = new Label("Średnia długość życia: ");
    Label avgLifeTimeVal = new Label();

    Label avgChildrenDesc = new Label("Średnia liczba dzieci: ");
    Label avgChildrenVal = new Label();

    ArrayList<Label> labels = new ArrayList<>();

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
        this.setConstraints(domGenomVal,0,5,2,1);
        this.setConstraints(domAnimalsDesc,0,6);
        this.setConstraints(domAnimalsVal,1,6);
        this.setConstraints(avgLifeTimeDesc,0,7);
        this.setConstraints(avgLifeTimeVal,1,7);
        this.setConstraints(avgChildrenDesc,0,8);
        this.setConstraints(avgChildrenVal,1,8);


        this.getChildren().addAll(animalDesc,animalCount,roundDesc,roundCount,grassDesc,grassCount,avgEnDesc,avgEnVal,domGenomDesc,domGenomVal,
                                  domAnimalsDesc,domAnimalsVal,avgLifeTimeDesc,avgLifeTimeVal,avgChildrenDesc,avgChildrenVal);
        this.setVgap(10);

    }
}
