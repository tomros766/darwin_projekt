package gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import map.RectangularMap;
import map.elements.MapElementType;

public class ImageViewGenerator {
    public ImageView generateImageView (MapElementType type){
        ImageView imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.setPreserveRatio(true);
        imageView.setImage(new Image(getClass().getResource(type.getUrl()).toString()));
        return imageView;
    }
}
