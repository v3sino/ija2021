package ija.gui;

import ija.carts.Cart;
import ija.carts.Destination;
import ija.carts.Order;
import ija.warehouse.Goods;
import ija.warehouse.GoodsType;
import ija.warehouse.MapInfo;
import ija.warehouse.Shelf;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  Main graphic interface of application
 * @author xbegan01
 */

public class GUI extends Application{

    // -- Privates -- //
    private Pane building; // the main scene group (contains shelves, carts, etc...)
    private ArrayList<SequentialTransition> cart_moves;
    private double deltaX; // delta of scene movement while mouse dragging
    private double deltaY;
    private double speed = 0.75; // speed of one cart in seconds per square
    private final double speedDelta = speed-(speed*0.75);
    private double speedLabel = 1.0; // variable shows the speed ratio
    private static final int size = 40;
    private static ArrayList<Line> cartPath;
    int delay = 2000;
    int buildingWidth = 520;
    int buildingHeight = 560;

    private ArrayList<Circle> heatMapItems;
    private Rectangle hMap;
    List<Rectangle> shelves;
    private Scene scene;
    private List<ImageView> carts;

    // From warehouse
    private static ArrayList<GoodsType> goodsTypes;
    private static ArrayList<Shelf> shelvesInfo;
    private static ArrayList<Cart> cartsInfo;
	private static MapInfo map;

    /**
     * Stores information about map and shelves from external class
     * @param shelvesInf list of shelves with data
     * @param mapInfo map structure with the map description
     */
    public void initIfo(ArrayList<Shelf> shelvesInf, MapInfo mapInfo, ArrayList<Cart> cartsInf, ArrayList<GoodsType> gTypes) {
        shelvesInfo = shelvesInf;
        cartsInfo = cartsInf;
    	map = mapInfo;
        goodsTypes = gTypes;
	}

    public void start(Stage primaryStage) throws Exception{
        building = new Pane();
        building.setPrefSize(560, 600);
        cart_moves = new ArrayList<>();
        carts = new ArrayList<>();
        shelves = new ArrayList<>();
        cartPath= new ArrayList<>();


        // Setting scene
        primaryStage.setTitle("Warehouse");
        BorderPane layout = new BorderPane();
        BorderPane help_layout = new BorderPane();
        scene = new Scene(layout, 570, 600);
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

        //hlp_menu.getItems().addAll(readme);

        // -- Cotrols menu -- //
        Menu play_menu = new Menu("Controls");

        // Text displaying speed
        Text speedTxt = new Text(scene.getWidth() -90, scene.getHeight()-50, Double.toString(speed));
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

        play_menu.getItems().addAll(speedUp, speedDown);

        // -- Scene menu -- //
        Menu scene_menu = new Menu("Scene");
        MenuItem heatMap = new MenuItem("HeatMap");
        heatMap.setOnAction(actionEvent -> displayHMap());
        scene_menu.getItems().addAll(reset_scene, heatMap);

        // -- Progress menu -- //
        Menu progress_menu = new Menu("Details");
        MenuItem progress = new MenuItem("Progress");
        progress.setOnAction(actionEvent -> showProgress());
        progress_menu.getItems().addAll(progress);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(play_menu, scene_menu, progress_menu);
        // -- END OF MENU INITIALIZATION -- //

        // Init objects
        InitWarehouse();
        initHeatMap();

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

        // -- CONTROL BUTTONS -- //
        VBox controlButtons = new VBox();
        controlButtons.setSpacing(20);
        controlButtons.setPadding(new Insets(20, 10,10,10));

        SignButton pauseSB = new SignButton("||");
        StackPane pauseButton = pauseSB.toStackPane();

        SignButton speedSB = new SignButton("<");
        StackPane speedSign = speedSB.toStackPane();
        speedSB.getTextSign().setRotate(90);

        SignButton speedDownSB = new SignButton(">");
        StackPane speedDownSign = speedDownSB.toStackPane();
        speedDownSB.getTextSign().setRotate(90);

        SignButton addOrder = new SignButton("+");
        StackPane newOrderSign = addOrder.toStackPane();

        controlButtons.getChildren().addAll(speedSign, pauseButton, speedDownSign, newOrderSign);
        VBox.setMargin(newOrderSign, new Insets(30, 0,0,0));
        // -- END OF CONTROL BUTTONS -- //

        layout.setTop(menuBar);
        layout.setCenter(building);
        layout.setRight(controlButtons);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    	map.readShelfToGui(this);
    	map.readCartToGui(this);

    	// Loading indicators of carts
        ArrayList<ProgressIndicator> indicators = new ArrayList<>();
        for (int i = 0; i < carts.size(); i++) {
            ProgressIndicator pi = new ProgressIndicator(-1);
            building.getChildren().add(pi);
            pi.setVisible(false);
            indicators.add(pi);
        }

    	Timer tmr = new Timer(delay, e -> {
            // Actualize HeatMap
    	    getHeatMap();

    	    // Opacity of displayed speed slowly fades out
    	    if (op.get() > 0.) {
                op.updateAndGet(v -> v - 0.25);
                speedTxt.setOpacity(op.get());
            }

            System.out.println("Tick\n");
            for(Cart cart : cartsInfo) {
            	cart.move();
            }
            playAnimation();

            // Small loading indicator while cart is laoding goods
            for (ImageView cart:carts) {
                int i = carts.indexOf(cart);
                if (cartsInfo.get(i).getWaitTime() == 1) {
                    indicators.get(i).setLayoutY(carts.get(i).getY() - 15);
                    indicators.get(i).setLayoutX(carts.get(i).getX() - 15);
                    indicators.get(i).setPrefHeight(20);
                    indicators.get(i).setPrefWidth(20);
                    indicators.get(i).setVisible(true);
                }
                else
                    indicators.get(i).setVisible(false);
            }

        });
        tmr.start();

        // -- Actions of buttons on the right panel -- //
        pauseSB.getButton().setOnAction(actionEvent -> {
            if (tmr.isRunning()){
                pauseSB.getTextSign().setText(">");
                pauseSB.getTextSign().setFont(Font.font("Verdana", FontWeight.BOLD, 20));
                tmr.stop();
            }
            else{
                pauseSB.getTextSign().setText("||");
                pauseSB.getTextSign().setFont(Font.font("Verdana", FontWeight.BOLD, 16));
                tmr.start();
            }
        });
        pauseButton.setOnMouseClicked(mouseEvent -> pauseSB.getButton().fire());

        speedSB.getButton().setOnAction(actionEvent -> {
            if (speed - 0.25 >0) {
                speed -= speedDelta;
                speedLabel += 0.25;
            }

            String speedStr = "Speed: " + speedLabel;
            speedTxt.setText(speedStr);
            op.set(1.);
            speedTxt.setOpacity(1.);
        });
        speedSign.setOnMouseClicked(mouseEvent -> speedSB.getButton().fire());

        speedDownSB.getButton().setOnAction(actionEvent -> {
            if (speedLabel - 0.25 > 0) {
                speed += speedDelta;
                speedLabel -= 0.25;
            }

            String speedStr = "Speed: " + speedLabel;
            speedTxt.setText(speedStr);
            op.set(1.);
            speedTxt.setOpacity(1.);
        });
        speedDownSign.setOnMouseClicked(mouseEvent -> speedDownSB.getButton().fire());

        addOrder.getButton().setOnAction(actionEvent -> addNewOrder());
        addOrder.getTextSign().setOnMouseClicked(mouseEvent -> addOrder.getButton().fire());
    }

    /**
     * Displays dialog and sends request for a new order
     */
    private void addNewOrder(){
        // Init dialog
        Dialog<Pair<String, String>> order = new Dialog<>();
        order.setTitle("New Order");
        order.setHeaderText("Add new order:");

        // Pane for items
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Text field for number
        Spinner<Integer> number = new Spinner<>(1, 50, 1);
        number.setEditable(true);
        number.setPrefSize(75, 25);

        ChoiceBox<String> goodsType = new ChoiceBox<>();
        ArrayList<String> goodsNames = new ArrayList<>();
        for (GoodsType gt : goodsTypes){
            goodsNames.add(gt.getName());
        }
        goodsType.getItems().addAll(goodsNames);

        // -- Set buttons -- //
        ButtonType orderButtonType = new ButtonType("Send Order", ButtonBar.ButtonData.OK_DONE);
        order.getDialogPane().lookupButton(orderButtonType);
        order.getDialogPane().getButtonTypes().addAll(orderButtonType, ButtonType.CANCEL);

        grid.add(new Label("Quantity:"), 0, 0);
        grid.add(number, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(goodsType, 1, 1);

        order.getDialogPane().setContent(grid);

        order.setResultConverter(dialogButton -> {
            if (dialogButton == orderButtonType) {
                return new Pair<>(number.getValue().toString(), goodsType.getValue());
            }
            return null;
        });

        Optional<Pair<String, String>> result = order.showAndWait();

        result.ifPresent(orderAssign -> {
            if (orderAssign.getValue() != null) {
                GoodsType newGoods = new GoodsType(orderAssign.getValue());
                Order newOrder = new Order(Integer.parseInt(orderAssign.getKey()), newGoods);
                cartsInfo.get(0).planner.addOrder(newOrder);
            }
        });
    }

    /**
     * Function for adding new goods into a shelf
     */
    public static void addGoods(Shelf shelf){
        // Init dialog
        Dialog<Pair<String, String>> order = new Dialog<>();
        order.setTitle("Resupply");
        order.setHeaderText("Add new goods:");

        // Pane for items
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Text field for number
        Spinner<Integer> number = new Spinner<>(1, 50, 1);
        number.setEditable(true);
        number.setPrefSize(75, 25);

        ChoiceBox<String> goodsType = new ChoiceBox<>();
        ArrayList<String> goodsNames = new ArrayList<>();
        for (GoodsType gt : goodsTypes){
            goodsNames.add(gt.getName());
        }
        goodsType.getItems().addAll(goodsNames);

        // -- Set buttons -- //
        ButtonType orderButtonType = new ButtonType("Add Goods", ButtonBar.ButtonData.OK_DONE);
        order.getDialogPane().lookupButton(orderButtonType);
        order.getDialogPane().getButtonTypes().addAll(orderButtonType, ButtonType.CANCEL);

        grid.add(new Label("Quantity:"), 0, 0);
        grid.add(number, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(goodsType, 1, 1);

        order.getDialogPane().setContent(grid);

        order.setResultConverter(dialogButton -> {
            if (dialogButton == orderButtonType) {
                return new Pair<>(number.getValue().toString(), goodsType.getValue());
            }
            return null;
        });

        Optional<Pair<String, String>> result = order.showAndWait();

        result.ifPresent(orderAssign -> {
            if (orderAssign.getValue() != null) {
                Goods newGoods = new Goods(new GoodsType(orderAssign.getValue()));
                for (int i = 0; i < Integer.parseInt(orderAssign.getKey()); i++)
                    shelf.put(newGoods);

            }
        });
    }

    /**
     * Function initiates heat map objects
     */
    private void initHeatMap(){
        double xpos, ypos;
        heatMapItems = new ArrayList<>();

        hMap = new Rectangle(0, 0, scene.getWidth(), scene.getHeight());
        hMap.setOpacity(0.4);
        hMap.setFill(Color.LIGHTBLUE);
        hMap.setVisible(false);
        building.getChildren().add(hMap);

        for (int x = 0; x < map.x_size; x++) {
            for (int y = 0; y < map.y_size; y++) {
                if (map.cells[x][y].type != 1){
                    xpos = x*size+(double)size/2;
                    ypos = y*size+(double)size/2;
                    Circle heat = new Circle(xpos, ypos, (double) size/2);
                    heat.setOpacity(.6);
                    heat.setVisible(false);
                    building.getChildren().add(heat);
                    heatMapItems.add(heat);
                }

            }
        }

    }

    /**
     * Function calculates intensity of positions on map
     */
    private void getHeatMap(){
        int i = 0;
        for (int x = 0; x < map.x_size; x++) {
            for (int y = 0; y < map.y_size; y++) {
                if (map.cells[x][y].type != 1) {
                    try {
                        switch (map.cells[x][y].trafficIntensity) {
                            case 1:
                                heatMapItems.get(i).setFill(Color.rgb(117, 238, 70));
                                break;
                            case 2:
                                heatMapItems.get(i).setFill(Color.rgb(255, 255, 0));
                                break;
                            case 3:
                                heatMapItems.get(i).setFill(Color.rgb(255, 165, 0));
                                break;
                            case 4:
                                heatMapItems.get(i).setFill(Color.rgb(255, 0, 0));
                                break;
                            default:
                                heatMapItems.get(i).setFill(Color.LIGHTBLUE);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Something went wrong");
                    }
                    i++;
                }
            }
        }


    }

    /**
     * Function displays map
     */
    private void displayHMap(){
        hMap.setVisible(!hMap.isVisible());
        for (Node hMapNode : heatMapItems)
            hMapNode.setVisible(!hMapNode.isVisible());
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
        if (i < carts.size()) {
            building.getChildren().remove(carts.get(i));
            carts.remove(i);
        }

        Image cart = new Image("file:data/cart.png");

        ImageView imageview = new ImageView(cart);
        imageview.setX(col*size+(size*0.67)/4); // put cart in the middle of rectangle
        imageview.setY(row*size);
        imageview.setFitHeight(size);
        imageview.setFitWidth(size*0.67);
        imageview.setRotate(imageview.getRotate()+180);

        // Details of cart when hovering with mouse
        Text cartText = new Text(10, 25, "");
        cartText.setStyle("-fx-font: 16 arial;");
        building.getChildren().add(cartText);
        cartText.setVisible(false);

        Text cartName = new Text("Cart"+i);
        cartName.setFill(Color.DIMGRAY);
        building.getChildren().add(cartName);
        cartName.setVisible(false);
        imageview.setOnMouseEntered(mouseEvent -> {
            String txt;
            try {
                txt = cartsInfo.get(i).getCargoToString();
            }
            catch (IndexOutOfBoundsException e){
                txt = "No info";
            }
            cartText.setText(txt);
            cartText.setVisible(true);
            building.getChildren().addAll(Objects.requireNonNull(createCartPath(i)));

            if (imageview.getX() < scene.getWidth()-size)
                cartName.setX(imageview.getX()+10);
            else
                cartName.setX(imageview.getX()-10-size);

            if (imageview.getY()-10 > 0)
                cartName.setY(imageview.getY()-10);
            else
                cartName.setY(imageview.getY()+10+size);

            cartName.setVisible(true);
        });
        imageview.setOnMouseExited(mouseEvent -> {
            cartText.setVisible(false);
            building.getChildren().removeAll(Objects.requireNonNull(createCartPath(i)));
            cartPath.clear();
            cartName.setVisible(false);
        });

        carts.add(imageview);
        building.getChildren().add(imageview);
    }

    /**
     * Finction creates path of a cart
     *
     * @param i index of checked cart
     * @return array of lines creating the path
     */
    public ArrayList<Line> createCartPath(int i){
        try {
            double x, y, new_x, new_y;
            Cart observedCart = cartsInfo.get(i);
            ImageView displayedCart = carts.get(i);

            x = (double) ((int) displayedCart.getX()/40);
            x = x*size + (double)  size /2;
            y = (double) ((int) displayedCart.getY()/40);
            y = y*size + (double)  size /2;

            for(Destination dest : observedCart.getPlanned_path()) {
                new_x = ((dest.x) * size)+(double) size/2;
                new_y = ((dest.y) * size)+(double) size/2;
                Line pathLine = new Line(x, y, new_x, new_y);
                pathLine.setStroke(Color.MEDIUMVIOLETRED);
                pathLine.setStrokeWidth(5);
                pathLine.setOpacity(.8);
                cartPath.add(pathLine);

                x = new_x;
                y = new_y;
            }

            return cartPath;
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Function draws lines for orientation in warehouse
     */
    private void InitWarehouse(){
        // Divides the scene into equally distributed rectangles with size of variable 'size'

        // Vertical lines
        for (int i = 0; i <= buildingWidth; i+= GUI.size) {
            Line line_ver = new Line(i, 0, i, buildingHeight);
            line_ver.setStroke(Color.GRAY);

            building.getChildren().add(line_ver);

        }
        // Horizontal lines
        for (int i = 0; i < buildingHeight; i+= GUI.size) {
            Line line_hor = new Line(0, i, buildingWidth, i);
            line_hor.setStroke(Color.GRAY);

            building.getChildren().add(line_hor);
        }

        // Draw wall and output place
        Rectangle wall = new Rectangle(0, buildingHeight, buildingWidth, 20);
        wall.setFill(Color.DARKGRAY);
        Rectangle door = new Rectangle(6*size+0.5, buildingHeight-2, size-1, 22);
        door.setFill(Color.web("#f4f4f4"));
        building.getChildren().addAll(wall, door);
    }


    /**
     * Function calculates actuall progress of orders and displays alert with details
     */
    public void showProgress(){
        Cart crt = cartsInfo.get(0);
        int all = crt.planner.getTotalCount();
        int dispatched = crt.planner.getTotalDispatched();

        Alert progAlert = new Alert(Alert.AlertType.INFORMATION);
        progAlert.setTitle("Progress of orders");
        progAlert.setHeaderText("Dispatched "+dispatched+"/"+all+" products");
        progAlert.setContentText(null);

        ProgressBar prog = new ProgressBar((double) dispatched/all);
        prog.setPrefWidth(200);
        prog.setMinHeight(60);
        progAlert.getDialogPane().setContent(prog);

        progAlert.show();
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

    /** Displays alert
     *
     * @param type type of alert {WARNING, INFORMATION, ERROR, CONFIRMATION}
     * @param title title of alert
     * @param msg alert message
     */
    public static void showAlert(Alert.AlertType type, String title, String header, String msg){

        Alert dispatch = new Alert(type);
        dispatch.setTitle(title);
        dispatch.setHeaderText(header);
        dispatch.setContentText(msg);
        dispatch.show();
    }

    public static void main() {
        launch();
    }
    
}
