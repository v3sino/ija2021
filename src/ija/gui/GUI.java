package ija.gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Timer;

import ija.carts.Cart;
import ija.warehouse.MapInfo;
import ija.warehouse.Shelf;

public class GUI extends Application{

    // -- Privates -- //
    private Group building; // the main scene group (contains shelves, carts, etc...)
    private ArrayList<SequentialTransition> cart_moves;
    double deltaX; // delta of scene movement while mouse dragging
    double deltaY;
    private double speed = 0.75; // speed of one cart in seconds per square
    private double speedLabel = 1.0; // variable shows the speed ratio
    int delay = 2000;

    List<Rectangle> shelves;
    List<ImageView> carts;
    private static ArrayList<Shelf> shelvesInfo;  			// link to shelves
    private static ArrayList<Cart> cartsInfo;
	private static MapInfo map;				   // link to mapInfo

    /**
     * Stores information about map and shelves from external class
     * @param shelvesInf list of shelves with data
     * @param mapInfo map structure with the map description
     */
    public void initIfo(ArrayList<Shelf> shelvesInf, MapInfo mapInfo, ArrayList<Cart> cartsInf) {
        shelvesInfo = shelvesInf;
        cartsInfo = cartsInf;
    	map = mapInfo;
	}
    
    public void start(Stage primaryStage) throws Exception{
        building = new Group();
        cart_moves = new ArrayList<>();
        carts = new ArrayList<>();
        shelves = new ArrayList<>();

        int size = 40;

        // Setting scene
        primaryStage.setTitle("Warehouse");
        BorderPane layout = new BorderPane();
        BorderPane help_layout = new BorderPane();
        Scene scene = new Scene(layout, 520, 580);
        Scene help_scene = new Scene(help_layout, 450, 250);

        // -- SETTING HELP SCENE -- //
        StackPane g = new StackPane();
        Button back = new Button("Späť");
        back.setOnAction(actionEvent -> primaryStage.setScene(scene));
        g.getChildren().add(back);
        g.setAlignment(Pos.CENTER);
        TextArea help_text = new TextArea();
        help_text.setWrapText(true);
        help_text.setEditable(false);

        String help = "Základná funkcionalita pre GUI projekt IJA:\n - zobrazenie skladu \n - približovanie skladu pomocou kolieska na myši \n - posúvanie zobrazenia skladu buď pomocou 'wasd' alebo stlačením a ťahaním pravého tlačidla myši \n - uvedenie zobrazenia do pôvodnej podoby pomocou Help->Reset scene \n - zvládanie pohybu vozíkmi na základe príkazov k pohubu (ukážka Play->Animation1 potom Play->Animation2) \n - reset vozíkov do pôvodnej podoby cez Play->Reset \n - zobrazenie okna s regálom po kliknutí na ľubovoľný regál";
        help_text.setText(help);

        help_layout.setCenter(help_text);
        help_layout.setBottom(g);
        // -- HELP SCENE SET -- //

        // -- SETTING HEATMAP -- //
        Rectangle hMap = new Rectangle(0, 0, scene.getWidth(), scene.getHeight());
        hMap.setOpacity(0.4);
        hMap.setFill(Color.LIGHTBLUE);
        hMap.setVisible(false);
        // -- HEATMAP SET -- //

        // -- MENU INITIALIZATION -- //
        Menu hlp_menu = new Menu("Help");
        MenuItem readme = new MenuItem("ReadMe");
        readme.setOnAction(actionEvent -> primaryStage.setScene(help_scene));

        MenuItem reset_scene = new MenuItem("Reset scene");
        reset_scene.setOnAction(actionEvent -> {
            building.setTranslateX(0);
            building.setTranslateY(0);
            building.setScaleX(1);
            building.setScaleY(1);
        });

        hlp_menu.getItems().addAll(readme);

        // -- Cotrols menu -- //
        Menu play_menu = new Menu("Controls");

        // This menu item restarts the cart initial positions and set default speed
        MenuItem restart = new MenuItem("Reset Carts");
        restart.setOnAction(actionEvent -> {
            InitCarts(size);
            speed = 0.75;
            speedLabel = 1.0;
        });

        // Text displaying speed
        Text speedTxt = new Text(scene.getWidth() -90, scene.getHeight()-35, Double.toString(speed));
        speedTxt.setOpacity(0.);
        speedTxt.setStyle("-fx-font: 16 arial;");
        building.getChildren().add(speedTxt);
        AtomicReference<Double> op = new AtomicReference<>(0.);

        // This menu item speeds up the animations
        MenuItem speedUp = new MenuItem("Speed Up");
        speedUp.setOnAction(actionEvent -> {
            if (speed - 0.25 >0) {
                speed -= 0.25;
                speedLabel += 0.25;
            }
            String speedStr = "Speed: " + speedLabel;
            speedTxt.setText(speedStr);
            op.set(1.);
            speedTxt.setOpacity(1.);
        });

        // This menu item slows down the animations
        MenuItem speedDown = new MenuItem("Speed Down");
        speedDown.setOnAction(actionEvent -> {
            speed += 0.25;
            speedLabel -= 0.25;

            String speedStr = "Speed: " + speedLabel;
            speedTxt.setText(speedStr);
            op.set(1.);
            speedTxt.setOpacity(1.);
        });

        play_menu.getItems().addAll(speedUp, speedDown, restart);

        // -- Scene menu -- //
        Menu scene_menu = new Menu("Scene");
        MenuItem heatMap = new MenuItem("HeatMap");
        heatMap.setOnAction(actionEvent -> hMap.setVisible(!hMap.isVisible()));
        scene_menu.getItems().addAll(reset_scene, heatMap);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(hlp_menu, play_menu, scene_menu);
        // -- END OF MENU INITIALIZATION -- //

        // Init objects
        InitWarehouse(scene, size);
        //InitCarts(size);

        // ------- ACTIONS WITH MOVING AND SCALING THE SCENE ------- //

        // Implementation of zooming
        primaryStage.addEventHandler(ScrollEvent.SCROLL, event ->{
            double move = event.getDeltaY();
            if (move > 0) {
                building.setScaleX(building.getScaleX()*1.1);
                building.setScaleY(building.getScaleY()*1.1);
            }
            else if (move < 0){
                building.setScaleX(building.getScaleX()*.9);
                building.setScaleY(building.getScaleY()*.9);
            }
        });

        // Moving the scene
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode().toString()) {
                case "A":
                    building.setTranslateX(building.getTranslateX() + 5);
                    break;
                case "D":
                    building.setTranslateX(building.getTranslateX() - 5);
                    break;
                case "W":
                    building.setTranslateY(building.getTranslateY() + 5);
                    break;
                case "S":
                    building.setTranslateY(building.getTranslateY() - 5);
                    break;
            }
        });

        // Mouse dragging of the scene
        scene.setOnMousePressed(mouseEvent -> {
            deltaX = mouseEvent.getSceneX()-building.getTranslateY();
            deltaY = mouseEvent.getSceneY()-building.getTranslateY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            building.setTranslateX(mouseEvent.getSceneX() - deltaX);
            building.setTranslateY(mouseEvent.getSceneY() - deltaY);
        });
        // ------- END OF ACTIONS WITH MOVING AND SCALING THE SCENE ------- //

        building.getChildren().add(hMap);

        layout.setTop(menuBar);
        layout.setCenter(building);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    	map.readShelfToGui(this);
    	map.readCartToGui(this);

    	Timer tmr = new Timer(delay, e -> {

    	    // Opacity of displayed speed slowly fades out
    	    if (op.get() > 0.) {
                op.updateAndGet(v -> v - 0.25);
                speedTxt.setOpacity(op.get());
            }

            System.out.println("Tick\n");
            cartsInfo.get(0).move();
            cartsInfo.get(1).move();
            playAnimation(); // -- changed name of timer() function becase it was bullshiting us
        });
        tmr.start();
    }

    /**
     * Function draws one sqare shelf and sets its actions
     *
     * @param col column in which shelf will be drawn
     * @param row row in which shelf will be drawn
     * @param size size of the rectangle (default size for stage recommended)
     * @param i identificator of the shelf
     * @param len length of shelf
     */
    public void PutShelf(int col, int row, int size, int i, int len){
        int x = (row-1)*size;
        int y = (col-1)*size;

        Rectangle shelf = new Rectangle(x, y, size, len*size);
        shelf.setFill(Color.LIGHTGREY);
        shelf.setStrokeWidth(4);
        shelf.setStroke(Color.DARKGRAY);

        shelf.setOnMouseEntered(mouseEvent -> shelf.setStroke(Color.RED));
        shelf.setOnMouseExited(mouseEvent -> shelf.setStroke(Color.DARKGRAY));

        shelf.setOnMouseClicked(mouseEvent -> {
            try {
                ShelfWindow.display(shelvesInfo.get(i));
            }
            catch (IndexOutOfBoundsException e){
                ShelfWindow.display(null);
            }
        });

        // Shelf label
        Text shelfLabel = new Text(x+5, y+12, Integer.toString(i));
        shelfLabel.setStroke(Color.GRAY);

        shelves.add(shelf);
        building.getChildren().add(shelf);
        building.getChildren().add(shelfLabel);
    }

    /**
     * Function puts cart on given row and column of the map
     * in case the cart is already inicialized, calling the function will re-inicialize it
     *
     * @param col column position of the cart
     * @param row row position of the cart
     * @param size size of the square
     * @param i identificator of the cart
     */
    public void PutCart(int col, int row, int size, int i){
        /*if (!carts.isEmpty()) {
            building.getChildren().remove(i);
            carts.remove(i);
        }*/

        Image cart = new Image("file:data/cart.png");

        ImageView imageview = new ImageView(cart);
        imageview.setX(col*size+(size*0.67)/4); // put cart in the middle of rectangle
        imageview.setY(row*size);
        imageview.setFitHeight(size);
        imageview.setFitWidth(size*0.67);
        imageview.setRotate(imageview.getRotate()+180);

        String txt;
        try {
            txt = cartsInfo.get(i).getCargoToString();
        }
        catch (IndexOutOfBoundsException e){
            txt = "No info";
        }

        // Details of cart when hovering with mouse
        Text cartText = new Text(10, 25, txt);
        cartText.setStyle("-fx-font: 16 arial;");
        building.getChildren().add(cartText);
        cartText.setVisible(false);

        imageview.setOnMouseEntered(mouseEvent -> cartText.setVisible(true));
        imageview.setOnMouseExited(mouseEvent -> cartText.setVisible(false));

        carts.add(imageview);
        building.getChildren().add(imageview);
    }

    /**
     * Function draws lines for orientation in warehouse
     *
     * @param scene scene in which warehouse will be drawn
     * @param size size of one rectangle unit (for proper display needs to be 40)
     */
    private void InitWarehouse(Scene scene, int size){
        // Divides the scene into equally distributed rectangles with size of variable 'size'

        // Vertical lines
        for (int i = 0; i <= scene.getWidth(); i+=size) {
            Line line_ver = new Line(i, 0, i, scene.getHeight()-20);
            line_ver.setStroke(Color.GRAY);

            building.getChildren().add(line_ver);

        }
        // Horizontal lines
        for (int i = 0; i <= scene.getHeight(); i+=size) {
            Line line_hor = new Line(0, i, scene.getWidth(), i);
            line_hor.setStroke(Color.GRAY);

            building.getChildren().add(line_hor);
        }
    }

    /**
     * Function (re)initializes 5 carts on the map
     *
     * @param size size of one rectangle unit (for proper display needs to be 40)
     */
    private void InitCarts(int size) {
        if (!carts.isEmpty()) {

            for (ImageView i : carts) {
                building.getChildren().remove(i);
            }

            carts.clear();
        }

        Image cart = new Image("file:data/cart.png");

        for (int i = 1; i < 6; i++) {
            ImageView imageview = new ImageView(cart);
            imageview.setX(2*i*size+(size*0.67)/4); // put cart in the middle of rectangle
            imageview.setY(0);
            imageview.setFitHeight(size);
            imageview.setFitWidth(size*0.67);
            imageview.setRotate(imageview.getRotate()+180);

            String txt;
            try {
                txt = cartsInfo.get(i).getCargoToString();
            }
            catch (IndexOutOfBoundsException e){
                txt = "No info";
            }

            Text cartText = new Text(10, 25, txt);
            cartText.setStyle("-fx-font: 18 arial;");
            building.getChildren().add(cartText);
            cartText.setVisible(false);

            imageview.setOnMouseEntered(mouseEvent -> cartText.setVisible(true));
            imageview.setOnMouseExited(mouseEvent -> cartText.setVisible(false));


            carts.add(imageview);
            building.getChildren().add(imageview);
        }

    }

    /**
     * Function rotates and moves cart down in given number of moves (rectangles)
     * @param cart cart which is to be moved
     * @param size size of one map-rectangle
     * @param moves number of moves
     *
     */
    public synchronized void CartMoveDown(ImageView cart, int size, int moves) {

        double dur = moves * speed;

        // Line which creates movement path
        double x_ax = cart.getX()+ (double) size/3;
        x_ax = Math.floor(x_ax);
        double y_ax = cart.getY() + (double) size/2;
        Line line = new Line(x_ax, y_ax, x_ax, y_ax+moves*size);

        // Set rotation movement
        RotateTransition cart_rotate = new RotateTransition();
        cart_rotate.setNode(cart);
        cart_rotate.setDuration(Duration.seconds(1));

        // Set rotation to the right direction
        cart_rotate.setToAngle(180);

        // Performing movement
        PathTransition cart_move = new PathTransition();
        cart_move.setNode(cart);
        cart_move.setDuration(Duration.seconds(dur));
        cart_move.setPath(line);

        cart.setY(cart.getY()+moves*size);
        SequentialTransition move = new SequentialTransition(cart_rotate, cart_move);
        move.setNode(cart);
        cart_moves.add(move);
    }

    /**
     * Function rotates and moves cart down in given number of moves (rectangles)
     * @author xbabac02
     * @param cart_index index of cart which is to be moved
     * @param size size of one map-rectangle
     * @param moves number of moves
     *
     */
    public synchronized void CartMoveDown(int cart_index, int size, int moves) {
    	this.CartMoveDown(carts.get(cart_index), size, moves);
    }

    /**
     * Function moves cart up in given number of moves (rectangles)
     * @param cart cart which is to be moved
     * @param size size of one map-rectangle
     * @param moves number of moves
     *
     */
    public synchronized void CartMoveUp(ImageView cart, int size, int moves){

        double dur = moves * speed;

        // Line which creates movement path
        double x_ax = cart.getX()+ (double) size/3;
        x_ax = Math.floor(x_ax);
        double y_ax = cart.getY() + (double) size/2;
        Line line = new Line(x_ax, y_ax, x_ax, y_ax-moves*size);

        // Set rotation movement
        RotateTransition cart_rotate = new RotateTransition();
        cart_rotate.setNode(cart);
        cart_rotate.setDuration(Duration.seconds(1));

        // We want cart to be rotated at 0 deg
        cart_rotate.setToAngle(0);

        // Set movement
        PathTransition cart_move = new PathTransition();
        cart_move.setNode(cart);
        cart_move.setDuration(Duration.seconds(dur));
        cart_move.setPath(line);

        cart.setY(cart.getY()-moves*size);
        SequentialTransition move = new SequentialTransition(cart_rotate, cart_move);
        move.setNode(cart);
        cart_moves.add(move);
    }

    /**
     * Function moves cart up in given number of moves (rectangles)
     * @author xbabac02
     * @param cart_index index of cart which is to be moved
     * @param size size of one map-rectangle
     * @param moves number of moves
     *
     */
    public synchronized void CartMoveUp(int cart_index, int size, int moves){
    	this.CartMoveUp(carts.get(cart_index), size, moves);
    }

    /**
     * Function rotates and moves cart in the right direction of a given number of moves (rectangles)
     * @param cart cart which is to be moved
     * @param size size of one map-rectangle
     * @param moves number of moves
     *
     */
    public synchronized void CartMoveRight(ImageView cart, int size, int moves) {

        double dur = moves * speed;

        // Line which creates movement path
        double x_ax = cart.getX()+ (double) size/3;
        x_ax = Math.floor(x_ax);
        double y_ax = cart.getY() + (double) size/2;
        Line line = new Line(x_ax, y_ax, x_ax+moves*size, y_ax);

        // Set rotation movement
        RotateTransition cart_rotate = new RotateTransition();
        cart_rotate.setNode(cart);
        cart_rotate.setDuration(Duration.seconds(1));

        // Set rotation if the cart isn't in a good position
        cart_rotate.setToAngle(90);

        // Set movement
        PathTransition cart_move = new PathTransition();
        cart_move.setNode(cart);
        cart_move.setDuration(Duration.seconds(dur));
        cart_move.setPath(line);

        cart.setX(cart.getX()+moves*size);
        SequentialTransition move = new SequentialTransition(cart_rotate, cart_move);
        move.setNode(cart);
        cart_moves.add(move);
    }
    
    /**
     * Function rotates and moves cart in the right direction of a given number of moves (rectangles)
     * @author xbabac02
     * @param cart_index index of cart which is to be moved
     * @param size size of one map-rectangle
     * @param moves number of moves
     *
     */
    public synchronized void CartMoveRight(int cart_index, int size, int moves) {
    	this.CartMoveRight(carts.get(cart_index), size, moves);
    }

    /**
     * Function rotates and moves cart in the left direction of a given number of moves (rectangles)
     * @param cart cart which is to be moved
     * @param size size of one map-rectangle
     * @param moves number of moves
     *
     */
    public synchronized void CartMoveLeft(ImageView cart, int size, int moves){

        double dur = moves * speed;

        // Line which creates movement path
        double x_ax = cart.getX()+ (double) size/3;
        x_ax = Math.floor(x_ax);
        double y_ax = cart.getY() + (double) size/2;
        Line line = new Line(x_ax, y_ax, x_ax-moves*size, y_ax);

        // Set rotation movement
        RotateTransition cart_rotate = new RotateTransition();
        cart_rotate.setNode(cart);
        cart_rotate.setDuration(Duration.seconds(1));

        // Set rotation if the cart isn't in a good position
        cart_rotate.setToAngle(270);

        // Set movement
        PathTransition cart_move = new PathTransition();
        cart_move.setNode(cart);
        cart_move.setDuration(Duration.seconds(dur));
        cart_move.setPath(line);

        cart.setX(cart.getX()-moves*size);
        SequentialTransition move = new SequentialTransition(cart_rotate, cart_move);
        move.setNode(cart);
        cart_moves.add(move);
    }
    
    /**
     * Function rotates and moves cart in the left direction of a given number of moves (rectangles)
     * @author xbabac02
     * @param cart_index index of cart which is to be moved
     * @param size size of one map-rectangle
     * @param moves number of moves
     *
     */
    public synchronized void CartMoveLeft(int cart_index, int size, int moves){

        this.CartMoveLeft(carts.get(cart_index), size, moves);
    }
    
    /**
     * Function handles cart movements.
     * Probably will be called every xy seconds
     */
    public void playAnimation(){
        // Create move sequence for each cart
        SequentialTransition cart1_moves = new SequentialTransition();
        SequentialTransition cart2_moves = new SequentialTransition();
        SequentialTransition cart3_moves = new SequentialTransition();
        SequentialTransition cart4_moves = new SequentialTransition();
        SequentialTransition cart5_moves = new SequentialTransition();

        // Sort the moves from cart_moves to each particular cart
        for (SequentialTransition move:cart_moves){
            if (move.getNode().equals(carts.get(0))){
                cart1_moves.getChildren().add(move);
            } else if (move.getNode().equals(carts.get(1))){
                cart2_moves.getChildren().add(move);
            }else if (move.getNode().equals(carts.get(2))){
                cart3_moves.getChildren().add(move);
            } else if (move.getNode().equals(carts.get(3))){
                cart4_moves.getChildren().add(move);
            }else if (move.getNode().equals(carts.get(4))){
                cart5_moves.getChildren().add(move);
            }
        }

        // Move the carts
        cart1_moves.play();
        cart2_moves.play();
        cart3_moves.play();
        cart4_moves.play();
        cart5_moves.play();

        // Cleanup the moves
        cart_moves.clear();
        cart1_moves.getChildren().clear();
        cart2_moves.getChildren().clear();
        cart3_moves.getChildren().clear();
        cart4_moves.getChildren().clear();
        cart5_moves.getChildren().clear();
    }

    public static void main() {
        launch();
    }
    
}
