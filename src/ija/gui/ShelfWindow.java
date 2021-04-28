package ija.gui;

import ija.warehouse.Shelf;
import ija.warehouse.SubShelf;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Random;

public class ShelfWindow{

    public static void display(Shelf shelfInfo) {
        int goodsNum = shelfInfo.numberOfNonEmptyShelves();

        //  -- Set stage
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Shelf");
        BorderPane layout = new BorderPane();
        primaryStage.initModality(Modality.APPLICATION_MODAL);

        // -- Get image
        Image shelf = new Image("file:data/shelf.png");
        ImageView shelf_view = new ImageView(shelf);
        shelf_view.setX(0);
        shelf_view.setY(0);
        layout.getChildren().add(shelf_view);

        // -- Print goods
        for (int i = 0; i < goodsNum; i++) {
            generateGoods(i, layout, shelfInfo.getSubShelf(i));
        }

        // -- Add  back button
        Pane buttonPane = new Pane();
        Button back = new Button("Späť");
        back.setPadding(new Insets(10, 20, 10, 20));
        back.setLayoutX(10);
        back.setLayoutY(-10);
        buttonPane.getChildren().add(back);

        back.setOnAction(actionEvent -> primaryStage.close());
        layout.setBottom(buttonPane);

        // -- Display stage
        Scene scene = new Scene(layout, shelf.getWidth(), shelf.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.showAndWait();
    }


    /**
     * Generates one box of goods (one subshelf)
     *
     * @param shelfPos position on shelf (1-5)
     * @param layout window layout
     * @param box subshelf cointaining info which is to be displayed
     */
    private static void generateGoods(int shelfPos, BorderPane layout, SubShelf box){

        // There's a random brobability on which side of shelf is box going to be
        Random rand = new Random();
        int x, y, pos = rand.nextInt(100);
        if (pos<50)
            x = 75;
        else
            x = 310;

        // Just some calculations to make it fit properly to the image
        y = 444 - (shelfPos*100);

        // Placing the box with goods
        Rectangle goods = new Rectangle(x, y, 125, 60);
        goods.setFill(Color.SADDLEBROWN);
        goods.setOpacity(.85);
        layout.getChildren().add(goods);

        Text info = new Text(10, 25, box.getContent());
        info.setStyle("-fx-font: 18 arial;");

        // Display info on hover
        goods.setOnMouseEntered(mouseEvent -> {
            layout.getChildren().add(info);
            goods.setOpacity(1);
        });

        // Hide info
        goods.setOnMouseExited(mouseEvent -> {
            layout.getChildren().remove(info);
            goods.setOpacity(.85);
        });
    }

}
