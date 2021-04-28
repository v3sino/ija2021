package ija.gui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShelfWindow{
    public static void display() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Shelf");
        Group layout = new Group();
        primaryStage.initModality(Modality.APPLICATION_MODAL);

        // -- Get image
        Image shelf = new Image("file:data/shelf.png");
        ImageView shelf_view = new ImageView(shelf);
        shelf_view.setX(0);
        shelf_view.setY(0);
        layout.getChildren().add(shelf_view);

        Scene scene = new Scene(layout, shelf.getWidth(), shelf.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.showAndWait();
    }
}
