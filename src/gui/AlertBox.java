package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import map.elements.animal.Animal;

import java.util.ArrayList;

public class AlertBox {

    static Animal animal;

    public static Animal display(ArrayList<Animal> animals) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        window.setMinWidth(150);
        Label label = new Label();
        label.setText("Wybierz zwierzę, które chcesz obserwować (domyślnie pierwsze): ");

        ListView<Animal> animalList = new ListView<>();

        animals.forEach(animal -> animalList.getItems().add(animal));

        Button pickButton = new Button("Wybierz");


        pickButton.setOnAction(e -> {
            animal = animalList.getSelectionModel().getSelectedItem();
            window.close();
        });

        VBox layout = new VBox(10);

        //Add buttons
        layout.getChildren().addAll(label,animalList, pickButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return animal;
    }

}
