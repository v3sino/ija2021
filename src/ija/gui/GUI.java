package ija.gui;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

import ija.gui.ShelfWindow;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("Draft");
        Group layout = new Group();
        Scene scene = new Scene(layout, 520, 560);

        int size = 40;
        List<Rectangle> shelves = InitWarehouse(scene, layout, size);

        InitCarts(scene, layout, size);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Function draws basic lines for orientation and shelves
     *
     * @param scene scene in which warehouse will be drawn
     * @param layout layout for scene
     * @param size size of one rectangle unit (for proper display needs to be 40)
     * @return list of created shelves (may be used in the future)
     */
    private List<Rectangle> InitWarehouse(Scene scene, Group layout, int size){
        List<Rectangle> shelves = new ArrayList<>();

        // Divides the scene into equally distributed rectangles with size of variable 'size'
        for (int i = 0; i < scene.getWidth(); i+=size) {
            // Vertical lines
            Line line_ver = new Line(i, 0, i, scene.getHeight());
            line_ver.setStroke(Color.GRAY);
            layout.getChildren().add(line_ver);

            // Shelves
            int s_width = size;
            int s_height = 2*size;

            if ((i/10) % 8 != 0) {
                for (int j = 2*size; j < scene.getHeight(); j+=size*3) {
                    Rectangle shelf = new Rectangle(i, j, s_width, s_height);
                    shelf.setFill(Color.SADDLEBROWN);
                    layout.getChildren().add(shelf);

                    shelf.setOnMouseEntered(mouseEvent -> {
                        shelf.setStrokeWidth(4);
                        shelf.setStroke(Color.RED);
                    });
                    shelf.setOnMouseExited(mouseEvent -> shelf.setStrokeWidth(0));
                    // Displaying shelf window
                    shelf.setOnMouseClicked(mouseEvent -> ShelfWindow.display());
                    shelves.add(shelf);
                }
            }
        }

        for (int i = 0; i < scene.getHeight(); i+=size) {
            // Horizontal lines
            Line line_hor = new Line(0, i, scene.getWidth(), i);
            line_hor.setStroke(Color.GRAY);
            layout.getChildren().add(line_hor);
        }

        return shelves; }

    private void InitCarts(Scene scene, Group layout, int size){
        Image cart = new Image("ija/images/cart.png");
        ImageView imageview = new ImageView(cart);
        imageview.setX(2*size+(size*0.67)/4);
        imageview.setY(0);
        imageview.setFitHeight(size);
        imageview.setFitWidth(size*0.67);
        imageview.setRotate(imageview.getRotate()+180);
        layout.getChildren().add(imageview);

        PathTransition cart_move = new PathTransition();
        cart_move.setNode(imageview);
        cart_move.setDuration(Duration.seconds(5));
        int x_ax = 2*size+size/2;
        cart_move.setPath(new Line(x_ax, imageview.getY(), x_ax, scene.getHeight()));
        cart_move.setCycleCount(PathTransition.INDEFINITE);
        cart_move.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
