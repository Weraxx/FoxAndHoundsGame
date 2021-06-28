import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class RadioButtonDialog extends Dialog<RadioButton> {

    private int pos;
    private boolean buttonOK = false;

    public RadioButtonDialog() {
        GridPane gridPane = new GridPane();
        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(gridPane);
        dialogPane.setPrefHeight(60);
        dialogPane.setPrefWidth(250);
        this.setDialogPane(dialogPane);
        this.getDialogPane().getScene().getWindow().setOnCloseRequest(dialogEvent -> RadioButtonDialog.super.close());
        Text text = new Text("Choose fox's start position");
        text.setId("textDialog");
        RadioButton radioButtonLeft = new RadioButton();
        Text textL = new Text("left corner");
        textL.setId("textDialog");
        RadioButton radioButtonRight = new RadioButton();
        Text textR = new Text("right corner");
        textR.setId("textDialog");
        ToggleGroup toggleGroup = new ToggleGroup();
        radioButtonLeft.setToggleGroup(toggleGroup);
        radioButtonRight.setToggleGroup(toggleGroup);
        Button button = new Button("OK");
        button.setId("button");
        dialogPane.getScene().getStylesheets().add("style.css");
        gridPane.setBackground(new Background(new BackgroundFill(Color.FLORALWHITE, null, null)));
        dialogPane.setBackground(new Background(new BackgroundFill(Color.FLORALWHITE, null, null)));
        radioButtonLeft.setSelected(true);
        gridPane.add(text, 1, 0);
        gridPane.add(radioButtonLeft, 0, 1);
        gridPane.add(radioButtonRight, 2, 1);
        gridPane.add(textL, 0, 2);
        gridPane.add(button, 1, 2);
        gridPane.add(textR, 2, 2);
        gridPane.setAlignment(Pos.CENTER);
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            GridPane.setHalignment(gridPane.getChildren().get(i), HPos.CENTER);
            GridPane.setValignment(gridPane.getChildren().get(i), VPos.CENTER);
        }


        radioButtonLeft.setOnAction(actionEvent -> pos = 0);
        radioButtonRight.setOnAction(actionEvent -> pos = 6);

        button.setOnAction(actionEvent -> {
            buttonOK = true;
            RadioButtonDialog.super.getDialogPane().getScene().getWindow().hide();
        });
    }

    public int getPos() {
        return pos;
    }

    public boolean isButtonOK() {
        return buttonOK;
    }
}
