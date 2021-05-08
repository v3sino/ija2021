package ija.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *  Class for creating buttons with signs
 * @author xbegan01
 */
public class SignButton extends StackPane{
    private final Text textSign;
    private final StackPane signButton;
    private final Button but;

    public SignButton(String sign){
        signButton = new StackPane();
        but = new Button();
        but.setPrefSize(30, 30);

        Text butSign = new Text(sign);
        butSign.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        butSign.setStroke(Color.web("#7080A0"));
        butSign.setFill(Color.GRAY);

        signButton.getChildren().addAll(but, butSign);
        signButton.setAlignment(Pos.CENTER);

        this.textSign = butSign;
        //return signButton;
    }

    public Text getTextSign() {
        return textSign;
    }
    public StackPane toStackPane(){return signButton;}
    public Button getButton() {
        return but;
    }
}
