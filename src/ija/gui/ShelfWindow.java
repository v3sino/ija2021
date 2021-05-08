package ija.gui;

import ija.warehouse.Shelf;
import ija.warehouse.SubShelf;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Random;

/**
 * Interface of one shelf
 * @author xbegan02
 */
public class ShelfWindow{

    public static void display(Shelf shelfInfo) {
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
        if (shelfInfo != null) {
            int goodsNum = shelfInfo.numberOfNonEmptyShelves();
            for (int i = 0; i < goodsNum; i++) {
                generateGoods(i, layout, shelfInfo.getSubShelf(i));
            }
        }

        // -- Add back button
        HBox buttonPane = new HBox();
        buttonPane.setPadding(new Insets(10, 10,10,10));

        Button back = new Button("Back");
        back.setPadding(new Insets(10, 20, 10, 20));
        back.setOnAction(actionEvent -> primaryStage.close());

        // -- Add goods button
        Button add = new Button("Resupply");
        add.setPadding(new Insets(10, 20, 10, 20));
        add.setOnAction(actionEvent -> {
            GUI.addGoods(shelfInfo);
            layout.getChildren().removeIf(node -> node instanceof Rectangle);
            if (shelfInfo != null) {
                int goodsNum = shelfInfo.numberOfNonEmptyShelves();
                for (int i = 0; i < goodsNum; i++) {
                    generateGoods(i, layout, shelfInfo.getSubShelf(i));
                }
            }
        });
        HBox.setMargin(add, new Insets(0, 0, 0,shelf.getWidth()-175));


        buttonPane.getChildren().addAll(back, add);
        layout.setBottom(buttonPane);

        // -- Display stage
        Scene scene = new Scene(layout, shelf.getWidth(), shelf.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.showAndWait();
    }

    /**
     * Generates one box of goods (one subshelf) on the scene
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
        layout.getChildren().add(info);
        info.setVisible(false);

        // Display info on hover
        goods.setOnMouseEntered(mouseEvent -> {
            info.setVisible(true);
            goods.setOpacity(1);
        });

        // Hide info
        goods.setOnMouseExited(mouseEvent -> {
            info.setVisible(false);
            goods.setOpacity(.85);
        });
    }


}
