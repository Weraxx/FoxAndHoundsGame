import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FoxAndHoundsGame extends Application {

    private static final int COL_COUNT = 8;
    private static final int ROW_COUNT = 8;
    private final Piece hound_01 = new Piece(PieceType.HOUNDS, 0, 1);
    private final Piece hound_02 = new Piece(PieceType.HOUNDS, 0, 3);
    private final Piece hound_03 = new Piece(PieceType.HOUNDS, 0, 5);
    private final Piece hound_04 = new Piece(PieceType.HOUNDS, 0, 7);
    private final Piece[] lastMove = {hound_01};
    private final Piece[] hounds = {hound_01, hound_02, hound_03, hound_04};
    private int pos;
    private final Piece fox = new Piece(PieceType.FOX, 7, pos);
    private final Piece[] pieces = {hound_01, hound_02, hound_03, hound_04, fox};
    private final Menu timerLabel = new Menu();
    private final Menu whoseTurn = new Menu();
    private final int TIME_MAX = 30;
    private final int[] timerInput = {30};
    private final Timeline checkingWinner = new Timeline();
    private final Timeline timer = new Timeline();
    StackPane[][] stackPaneFields = new StackPane[8][8];
    BoardSquare[][] boardSquares = new BoardSquare[8][8];
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        scene = new Scene(getStartWindow(primaryStage), 800, 600);
        scene.getStylesheets().add("style.css");
        timer(primaryStage);
        primaryStage.setTitle("Fox and Hounds");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private StackPane getStartWindow(Stage primaryStage) {

        StackPane startWindow = new StackPane();
        startWindow.setAlignment(Pos.CENTER);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        Button startButton = new Button("START");
        startButton.setPrefSize(150, 50);
        startButton.setId("buttonStart");
        VBox.setMargin(startButton, new Insets(40, 0, 0, 0));

        ImageView imageView = new ImageView("resources/Game-of-Fox-and-Hounds.jpg");
        imageView.fitHeightProperty().bind(startWindow.heightProperty());
        imageView.fitWidthProperty().bind(startWindow.widthProperty());

        Button openButton = new Button("OPEN");
        openButton.setId("buttonStart");
        openButton.setPrefSize(150, 50);
        VBox.setMargin(openButton, new Insets(10, 0, 0, 0));
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        openButton.setOnAction(actionEvent -> {
            getBoard(primaryStage);
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                if (file.getAbsolutePath().endsWith(".txt")) {
                    openStart(file, stackPaneFields, primaryStage);
                } else {
                    showErrorDialog("WRONG FILE EXTENSION!");
                }
            }
        });

        Button rulesButton = new Button("RULES");
        rulesButton.setId("buttonStart");
        rulesButton.setPrefSize(150, 50);
        VBox.setMargin(rulesButton, new Insets(10, 0, 0, 0));
        rulesButton.setOnAction(e -> scene.setRoot(getRulesWindow(primaryStage)));

        RadioButtonDialog radioButtonDialog = new RadioButtonDialog();
        startButton.setOnMouseClicked(mouseEvent -> {
            radioButtonDialog.showAndWait();
            if (radioButtonDialog.isButtonOK()) {
                pos = radioButtonDialog.getPos();
                fox.setNewPosition(7, pos);
                hound_01.setNewPosition(0, 1);
                hound_02.setNewPosition(0, 3);
                hound_03.setNewPosition(0, 5);
                hound_04.setNewPosition(0, 7);
                timerInput[0] = TIME_MAX;
                lastMove[0] = hound_01;
                scene = new Scene(getBoard(primaryStage), 600, 600);
                primaryStage.setScene(scene);
                primaryStage.show();
            }
        });

        vBox.getChildren().addAll(startButton, openButton, rulesButton);
        startWindow.getChildren().addAll(imageView, vBox);

        return startWindow;
    }

    private ScrollPane getRulesWindow(Stage primaryStage) {

        ScrollPane scrollPane = new ScrollPane();

        VBox rulesWindow = new VBox();
        rulesWindow.setAlignment(Pos.CENTER);

        Background background = new Background(new BackgroundFill(Color.FLORALWHITE, null, null));
        scrollPane.setBackground(background);
        rulesWindow.setBackground(background);

        Label textHeader_01 = new Label("INTRODUCTION OF FOX AND THE HOUNDS");
        textHeader_01.setId("labelWinner");
        VBox.setMargin(textHeader_01, new Insets(20, 0, 10, 0));
        Text text_01 = new Text("Fox and the Hounds is an abstract strategy board game that uses checkers and an 8??8 grid.");
        text_01.setId("text");
        text_01.setWrappingWidth(500);

        Label textHeader_02 = new Label("SETUP");
        textHeader_02.setId("labelWinner");
        VBox.setMargin(textHeader_02, new Insets(20, 0, 10, 0));
        Text text_02 = new Text("Who is playing as the hounds shall place their four pieces on the dark spaces in their back row. " +
                "The player who is playing as the fox can place their piece on one of the selected end fields in their back row.");
        text_02.setId("text");
        text_02.setWrappingWidth(500);

        Label textHeader_03 = new Label("THE PLAY");
        textHeader_03.setId("labelWinner");
        VBox.setMargin(textHeader_03, new Insets(15, 0, 10, 0));
        Text text_03 = new Text("The game begins with the fox making their move. The fox is allowed to move one space diagonally in any direction much " +
                "like a king piece in checkers. After the fox makes their first move, the hounds can now take their turn. During the hounds turn, " +
                "the player may choose one hound to move. Hounds move diagonally, but they may only move forward. " +
                "Once a hound has reached the opposite end of the board it is stuck and can no longer move. Play like this continues " +
                "until either side meets their win condition. In this game, neither the fox or the hounds are allowed to jump over or land on other pieces. " +
                "They may only move into an adjacent space that is open. ");
        text_03.setId("text");
        text_03.setWrappingWidth(500);

        HBox hBoxMovement = new HBox();
        hBoxMovement.setAlignment(Pos.CENTER);
        ImageView imageFoxMove = new ImageView("resources/fox_move.png");
        HBox.setMargin(imageFoxMove, new Insets(0, 10, 0, 0));
        ImageView imageHoundsMove = new ImageView("resources/hounds_move.png");
        hBoxMovement.getChildren().addAll(imageFoxMove, imageHoundsMove);
        VBox.setMargin(hBoxMovement, new Insets(20, 0, 0, 20));

        Label textHeader_04 = new Label("WINNING");
        textHeader_04.setId("labelWinner");
        VBox.setMargin(textHeader_04, new Insets(20, 0, 10, 0));
        Text text_04 = new Text("If the fox is able to reach the opposite end of the board and end up in the hound???s starting row, the fox wins. " +
                "If the hounds surround the fox in such a way that it can no longer move in any direction, the hounds win.");
        text_04.setId("text");
        text_04.setWrappingWidth(500);

        HBox hBoxWinners = new HBox();
        hBoxWinners.setAlignment(Pos.CENTER);
        ImageView imageFoxWinner = new ImageView("resources/fox_winner.jpg");
        HBox.setMargin(imageFoxWinner, new Insets(0, 10, 0, 0));
        ImageView imageHoundsWinner = new ImageView("resources/hounds_winner.jpg");
        hBoxWinners.getChildren().addAll(imageFoxWinner, imageHoundsWinner);
        VBox.setMargin(hBoxWinners, new Insets(20, 0, 0, 20));

        Button backButton = new Button("BACK TO MENU");
        backButton.setId("button");
        VBox.setMargin(backButton, new Insets(20, 0, 20, 20));

        backButton.setOnAction(e -> scene.setRoot(getStartWindow(primaryStage)));

        rulesWindow.getChildren().addAll(textHeader_01, text_01, textHeader_02, text_02, textHeader_03, text_03, hBoxMovement, textHeader_04, text_04, hBoxWinners, backButton);

        scrollPane.setContent(rulesWindow);
        primaryStage.setResizable(false);
        rulesWindow.setPrefWidth(800);

        return scrollPane;
    }

    private VBox getFoxWinnerWindow(Stage primaryStage) {

        VBox getWinnerWindow = new VBox();
        getWinnerWindow.setAlignment(Pos.CENTER);

        BackgroundFill backgroundFill = new BackgroundFill(Color.FLORALWHITE, null, null);
        Background background = new Background(backgroundFill);
        getWinnerWindow.setBackground(background);

        Label labelWinner = new Label("FOX IS THE WINNER!");
        labelWinner.setId("labelWinner");
        labelWinner.setAlignment(Pos.TOP_CENTER);

        Image image = new Image("resources/fox.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);

        Button buttonPlayAgain = new Button("Play again");
        buttonPlayAgain.setId("button");
        buttonPlayAgain.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonPlayAgain, new Insets(20, 0, 0, 0));

        Button buttonBackToMenu = new Button("Back to menu");
        buttonBackToMenu.setId("button");
        VBox.setMargin(buttonBackToMenu, new Insets(10, 0, 0, 0));
        buttonPlayAgain.setAlignment(Pos.BOTTOM_CENTER);

        scene.getStylesheets().add("style.css");

        buttonPlayAgain.setOnMouseClicked(e -> {
            scene.setRoot(getBoard(primaryStage));
            timer.play();
        });

        buttonBackToMenu.setOnMouseClicked(e -> {
            scene = new Scene(getStartWindow(primaryStage), 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
            scene.getStylesheets().add("style.css");
        });

        getWinnerWindow.getChildren().addAll(labelWinner, imageView, buttonPlayAgain, buttonBackToMenu);

        return getWinnerWindow;
    }

    private VBox getHoundsWinnerWindow(Stage primaryStage) {

        VBox getWinnerWindow = new VBox();
        getWinnerWindow.setAlignment(Pos.CENTER);

        BackgroundFill backgroundFill = new BackgroundFill(Color.FLORALWHITE, null, null);
        Background background = new Background(backgroundFill);
        getWinnerWindow.setBackground(background);

        Label labelWinner = new Label("HOUNDS ARE THE WINNERS!");
        labelWinner.setId("labelWinner");
        labelWinner.setAlignment(Pos.TOP_CENTER);

        Image image = new Image("resources/hounds.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);

        scene.getStylesheets().add("style.css");

        Button buttonPlayAgain = new Button("Play again");
        buttonPlayAgain.setId("button");
        buttonPlayAgain.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonPlayAgain, new Insets(20, 0, 0, 0));

        Button buttonBackToMenu = new Button("Back to menu");
        buttonBackToMenu.setId("button");
        VBox.setMargin(buttonBackToMenu, new Insets(10, 0, 0, 0));
        buttonPlayAgain.setAlignment(Pos.BOTTOM_CENTER);

        buttonPlayAgain.setOnMouseClicked(e -> {
            scene.setRoot(getBoard(primaryStage));
            timer.play();
        });

        buttonBackToMenu.setOnMouseClicked(e -> {
            scene = new Scene(getStartWindow(primaryStage), 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
            scene.getStylesheets().add("style.css");
        });

        getWinnerWindow.getChildren().addAll(labelWinner, imageView, buttonPlayAgain, buttonBackToMenu);

        return getWinnerWindow;
    }

    private VBox getBoard(Stage primaryStage) {

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("options");
        menuBar.getMenus().addAll(menu, whoseTurn, timerLabel);
        MenuItem menuItemSave = new MenuItem("save");
        KeyCodeCombination saveKeyCode = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        menuItemSave.setAccelerator(saveKeyCode);
        MenuItem menuItemOpen = new MenuItem("open");
        KeyCodeCombination openKeyCode = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        menuItemOpen.setAccelerator(openKeyCode);
        menu.getItems().addAll(menuItemSave, menuItemOpen);

        VBox boardWithMenuBar = new VBox(menuBar);

        GridPane board = new GridPane();

        boolean[] ifLastClickOnPiece = {false};
        Piece[] tmpPiece = {null};

        for (int row = 0; row < COL_COUNT; ++row) {
            for (int col = 0; col < ROW_COUNT; ++col) {

                StackPane stackPaneField = new StackPane();
                stackPaneFields[row][col] = stackPaneField;

                BoardSquare boardSquare = getBoardSquare(row, col, stackPaneField);

                setStartBoard(stackPaneField, row, col);

                stackPaneField.setOnMouseEntered(e -> boardSquare.highlight());
                stackPaneField.setOnMouseExited(e -> boardSquare.blacken());
                stackPaneField.setOnMouseClicked(e -> movementCheck(ifLastClickOnPiece, tmpPiece, stackPaneField, boardSquare));

                board.add(stackPaneField, col, row);
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        menuItemSave.setOnAction(actionEvent -> {
            timer.stop();
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                save(file, stackPaneFields);
            }
            timer.play();
        });

        menuItemOpen.setOnAction(actionEvent -> {
            timer.stop();
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                if (file.getAbsolutePath().endsWith(".txt")) {
                    open(file, stackPaneFields, primaryStage);
                } else {
                    showErrorDialog("WRONG FILE EXTENSION!");
                }
            }
            timer.play();
        });

        checkingWinner.play();
        timer.play();

        for (int col = 0; col < COL_COUNT; ++col) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100);
            board.getColumnConstraints().add(columnConstraints);
        }

        for (int row = 0; row < ROW_COUNT; ++row) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100);
            board.getRowConstraints().add(rowConstraints);
        }

        board.setPrefSize(600, 600);
        boardWithMenuBar.getChildren().add(board);
        primaryStage.setResizable(false);

        return boardWithMenuBar;
    }

    private BoardSquare getBoardSquare(int row, int col, StackPane stackPaneField) {
        BoardSquare boardSquare;
        if ((row + col) % 2 == 0) {
            boardSquare = new BoardSquare(Color.WHITESMOKE, row, col);
        } else {
            boardSquare = new BoardSquare(Color.GRAY, row, col);
        }
        stackPaneField.getChildren().add(boardSquare);
        boardSquares[row][col] = boardSquare;
        return boardSquare;
    }

    private void movementCheck(boolean[] ifLastClickOnPiece, Piece[] tmpPiece, StackPane stackPaneField, BoardSquare boardSquare) {
        if (ifContainPiece(stackPaneField) && !ifLastClickOnPiece[0] && lastMove[0].getType() != deliverObjectPiece(stackPaneField).getType()) {
            ifLastClickOnPiece[0] = true;
            showPossibilities(boardSquares, stackPaneField, deliverObjectPiece(stackPaneField), boardSquare.getBoardSquareRow(), boardSquare.getBoardSquareCol());
            tmpPiece[0] = deliverObjectPiece(stackPaneField);
        } else if (!ifContainPiece(stackPaneField) && ifLastClickOnPiece[0]) {
            int newRow = boardSquare.getBoardSquareRow();
            int newCol = boardSquare.getBoardSquareCol();
            if (isMovementPossibleFox(tmpPiece[0], tmpPiece[0].getRowPosition(), tmpPiece[0].getColPosition(), newRow, newCol) || isMovementPossibleHounds(tmpPiece[0], tmpPiece[0].getRowPosition(), tmpPiece[0].getColPosition(), newRow, newCol)) {
                stackPaneFields[newRow][newCol].getChildren().add(tmpPiece[0]);
                tmpPiece[0].setNewPosition(newRow, newCol);
                lastMove[0] = deliverObjectPiece(stackPaneField);
                timerInput[0] = TIME_MAX;
                if (lastMove[0].getType() == PieceType.HOUNDS) {
                    whoseTurn.setText("turn: " + PieceType.FOX);
                } else {
                    whoseTurn.setText("turn: " + PieceType.HOUNDS);
                }
            }
            hidePossibilities(boardSquares);
            ifLastClickOnPiece[0] = false;
        } else if (ifLastClickOnPiece[0]) {
            hidePossibilities(boardSquares);
            ifLastClickOnPiece[0] = false;
        } else {
            if (lastMove[0].getType() == PieceType.FOX) {
                showErrorDialog("HOUNDS TURN");
            } else {
                showErrorDialog("FOX'S TURN");
            }
        }
    }

    private void save(File file, StackPane[][] stackPaneFields) {
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(file.getAbsolutePath());
            for (int row = 0; row < ROW_COUNT; row++) {
                for (int col = 0; col < COL_COUNT; col++) {
                    if (ifContainPiece(stackPaneFields[row][col])) {
                        printWriter.print(deliverObjectPiece(stackPaneFields[row][col]));
                        printWriter.print("\r\n");
                    }
                }
            }
            printWriter.print("last " + lastMove[0] + '\n');
            printWriter.print("timer " + timerInput[0]);
            printWriter.close();
        } catch (FileNotFoundException e) {
            showErrorDialog("FILE NOT FOUND!");
        }
    }

    private void open(File file, StackPane[][] stackPaneFields, Stage primaryStage) {
        try {
            deletePieces(stackPaneFields);
            Scanner lineScanner = null;
            try {
                lineScanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                showErrorDialog("File not found!");
            }
            int tmpRow;
            int tmpCol;
            String tmpType;
            String tmpLine;
            String[] tmpData;
            int houndCount = 0;
            while (lineScanner.hasNextLine()) {
                tmpLine = lineScanner.nextLine();
                tmpData = tmpLine.split(" ");
                tmpType = tmpData[0];

                if (tmpType.equals("last")) {
                    if (tmpData[1].equals("FOX")) {
                        lastMove[0] = fox;
                        whoseTurn.setText(PieceType.HOUNDS.toString());
                    } else {
                        lastMove[0] = hounds[0];
                        whoseTurn.setText(PieceType.FOX.toString());
                    }
                } else if (tmpType.equals("timer")) {
                    if (Integer.parseInt(tmpData[1]) <= TIME_MAX) {
                        timerInput[0] = Integer.parseInt(tmpData[1]);
                    } else {
                        throw new Exception();
                    }
                } else {
                    tmpRow = Integer.parseInt(tmpData[1]);
                    tmpCol = Integer.parseInt(tmpData[2]);
                    if (tmpRow < ROW_COUNT && tmpCol < COL_COUNT) {
                        if (tmpType.equals("FOX")) {
                            fox.setNewPosition(tmpRow, tmpCol);
                            stackPaneFields[tmpRow][tmpCol].getChildren().add(fox);
                        } else {
                            hounds[houndCount].setNewPosition(tmpRow, tmpCol);
                            stackPaneFields[tmpRow][tmpCol].getChildren().add(hounds[houndCount]);
                            houndCount++;
                        }
                    } else {
                        throw new Exception();
                    }
                }
            }
        } catch (Exception exception) {
            showErrorDialog("WRONG DATA FORMAT!");
            deletePieces(stackPaneFields);
            scene = new Scene(getStartWindow(primaryStage), 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
            scene.getStylesheets().add("style.css");
        }
    }

    private void showErrorDialog(String s) {
        Alert alertWrongDataEx = new Alert(Alert.AlertType.WARNING);
        alertWrongDataEx.setHeaderText(null);
        alertWrongDataEx.setContentText(s);
        alertWrongDataEx.showAndWait();
    }

    private void openStart(File file, StackPane[][] stackPaneFields, Stage primaryStage) {
        try {
            deletePieces(stackPaneFields);
            Scanner lineScanner = null;
            try {
                lineScanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                showErrorDialog("FILE NOT FOUND!");
            }
            int tmpRow;
            int tmpCol;
            String tmpType;
            String tmpLine;
            String[] tmpData;
            int houndCount = 0;
            while (lineScanner.hasNextLine()) {
                tmpLine = lineScanner.nextLine();
                tmpData = tmpLine.split(" ");
                tmpType = tmpData[0];
                if (tmpType.equals("last")) {
                    if (tmpData[1].equals("FOX")) {
                        lastMove[0] = fox;
                        whoseTurn.setText(PieceType.HOUNDS.toString());
                    } else {
                        lastMove[0] = hounds[0];
                        whoseTurn.setText(PieceType.FOX.toString());
                    }
                } else if (tmpType.equals("timer")) {
                    if (Integer.parseInt(tmpData[1]) <= TIME_MAX) {
                        timerInput[0] = Integer.parseInt(tmpData[1]);
                    } else {
                        throw new Exception();
                    }
                } else {
                    tmpRow = Integer.parseInt(tmpData[1]);
                    tmpCol = Integer.parseInt(tmpData[2]);

                    if (tmpRow < ROW_COUNT && tmpCol < COL_COUNT) {
                        if (tmpType.equals("FOX")) {
                            fox.setNewPosition(tmpRow, tmpCol);
                            stackPaneFields[tmpRow][tmpCol].getChildren().add(fox);
                        } else {
                            hounds[houndCount].setNewPosition(tmpRow, tmpCol);
                            stackPaneFields[tmpRow][tmpCol].getChildren().add(hounds[houndCount]);
                            houndCount++;
                        }
                    } else {
                        throw new Exception();
                    }
                }
            }
            scene = new Scene(getBoard(primaryStage), 600, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception exception) {
            showErrorDialog("WRONG DATA FORMAT!");
            deletePieces(stackPaneFields);
            scene = new Scene(getStartWindow(primaryStage), 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
            scene.getStylesheets().add("style.css");
        }
    }

    private void resetGame() {
        fox.setNewPosition(7, pos);
        hound_01.setNewPosition(0, 1);
        hound_02.setNewPosition(0, 3);
        hound_03.setNewPosition(0, 5);
        hound_04.setNewPosition(0, 7);
        lastMove[0] = hounds[(int) (Math.random() + 3)];
    }

    private void setStartBoard(StackPane stackPaneField, int row, int col) {
        Piece piece = null;
        if (row == hound_01.getRowPosition() && col == hound_01.getColPosition()) {
            piece = hound_01;
        }
        if (row == hound_02.getRowPosition() && col == hound_02.getColPosition()) {
            piece = hound_02;
        }
        if (row == hound_03.getRowPosition() && col == hound_03.getColPosition()) {
            piece = hound_03;
        }
        if (row == hound_04.getRowPosition() && col == hound_04.getColPosition()) {
            piece = hound_04;
        }
        if (row == fox.getRowPosition() && col == fox.getColPosition()) {
            piece = fox;
        }
        if (piece != null) {
            piece.radiusProperty().bind(
                    Bindings.when(stackPaneField.heightProperty().lessThan(stackPaneField.widthProperty())).
                            then(stackPaneField.heightProperty()).otherwise(stackPaneField.widthProperty()).subtract(10).divide(2)
            );
            stackPaneField.getChildren().add(piece);
        }

    }

    private void timer(Stage primaryStage) {
        KeyFrame keyFrameCheckWin = new KeyFrame(Duration.millis(1), e -> {
            if (areHoundsWinner()) {
                resetGame();
                timer.stop();
                checkingWinner.stop();
                scene.setRoot(getHoundsWinnerWindow(primaryStage));
            }
            if (isTheFoxWinner()) {
                resetGame();
                timer.stop();
                checkingWinner.stop();
                scene.setRoot(getFoxWinnerWindow(primaryStage));
            }
        });
        checkingWinner.getKeyFrames().add(keyFrameCheckWin);
        checkingWinner.setCycleCount(Timeline.INDEFINITE);
        whoseTurn.setText("turn: " + PieceType.FOX);
        KeyFrame keyFrameTimer = new KeyFrame(Duration.seconds(1), e -> {
            if (timerInput[0] < 10) {
                timerLabel.setText("time: 00:0" + timerInput[0]);
            } else {
                timerLabel.setText("time: 00:" + timerInput[0]);
            }
            timerInput[0] = timerInput[0] - 1;
            if (timerInput[0] == 0) {
                if (lastMove[0].getType() == PieceType.HOUNDS) {
                    lastMove[0] = fox;
                    whoseTurn.setText("turn: " + PieceType.HOUNDS);
                } else {
                    lastMove[0] = hounds[(int) (Math.random() + 3)];
                    whoseTurn.setText("turn: " + PieceType.FOX);
                }
                timerInput[0] = TIME_MAX;
            }
        });
        timer.getKeyFrames().add(keyFrameTimer);
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    private boolean ifContainPiece(StackPane stackPane) {
        for (Piece piece : pieces) {
            if (stackPane.getChildren().contains(piece)) {
                return true;
            }
        }
        return false;
    }

    private Piece deliverObjectPiece(StackPane stackPane) {
        for (Piece piece : pieces) {
            if (stackPane.getChildren().contains(piece)) {
                return piece;
            }
        }
        return null;
    }

    private void deletePieces(StackPane[][] stackPaneFields) {
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                if (stackPaneFields[row][col] != null) {
                    if (ifContainPiece(stackPaneFields[row][col])) {
                        stackPaneFields[row][col].getChildren().remove(1);
                    }
                }
            }
        }
    }

    private void showPossibilities(BoardSquare[][] boardSquares, StackPane stackPaneField, Piece piece, int row, int col) {
        if (ifContainPiece(stackPaneField)) {
            if (piece.getType() == PieceType.FOX) {
                if ((row + 1) < ROW_COUNT && (col - 1) >= 0) {
                    if (!ifContainPiece(stackPaneFields[row + 1][col - 1])) {
                        boardSquares[row + 1][col - 1].highlightPossibilities();
                    }
                }
                if ((row + 1) < ROW_COUNT && (col + 1) < COL_COUNT) {
                    if (!ifContainPiece(stackPaneFields[row + 1][col + 1])) {
                        boardSquares[row + 1][col + 1].highlightPossibilities();
                    }
                }
                if ((row - 1) >= 0 && (col - 1) >= 0) {
                    if (!ifContainPiece(stackPaneFields[row - 1][col - 1])) {
                        boardSquares[row - 1][col - 1].highlightPossibilities();
                    }
                }
                if ((col + 1) < COL_COUNT && (row - 1) >= 0) {
                    if (!ifContainPiece(stackPaneFields[row - 1][col + 1])) {
                        boardSquares[row - 1][col + 1].highlightPossibilities();
                    }
                }
            } else {
                if ((row + 1) < ROW_COUNT && (col - 1) >= 0) {
                    if (!ifContainPiece(stackPaneFields[row + 1][col - 1])) {
                        boardSquares[row + 1][col - 1].highlightPossibilities();
                    }
                }
                if ((row + 1) < ROW_COUNT && (col + 1) < COL_COUNT) {
                    if (!ifContainPiece(stackPaneFields[row + 1][col + 1])) {
                        boardSquares[row + 1][col + 1].highlightPossibilities();
                    }
                }
            }
        }
    }

    private void hidePossibilities(BoardSquare[][] boardSquares) {
        for (int row = 0; row < COL_COUNT; ++row) {
            for (int col = 0; col < ROW_COUNT; ++col) {
                if ((row + col) % 2 == 0) {
                    boardSquares[row][col].setColor(Color.WHITESMOKE);
                } else {
                    boardSquares[row][col].setColor(Color.GRAY);
                }
            }
        }
    }

    private boolean isMovementPossibleFox(Piece piece, int row, int col, int newRow, int newCol) {
        if (piece.getType() == PieceType.FOX) {
            if ((row + 1) == newRow && (col - 1) == newCol) {
                if (!ifContainPiece(stackPaneFields[row + 1][col - 1])) {
                    return true;
                }
            }
            if ((row + 1) == newRow && (col + 1) == newCol) {
                if (!ifContainPiece(stackPaneFields[row + 1][col + 1])) {
                    return true;
                }
            }
            if ((row - 1) == newRow && (col - 1) == newCol) {
                if (!ifContainPiece(stackPaneFields[row - 1][col - 1])) {
                    return true;
                }
            }
            if ((col + 1) == newCol && (row - 1) == newRow) {
                return !ifContainPiece(stackPaneFields[row - 1][col + 1]);
            }
        }
        return false;
    }

    private boolean isMovementPossibleHounds(Piece piece, int row, int col, int newRow, int newCol) {
        if (piece.getType() == PieceType.HOUNDS) {
            if ((row + 1) == newRow && (col - 1) == newCol) {
                if (!ifContainPiece(stackPaneFields[row + 1][col - 1])) {
                    return true;
                }
            }
            if ((row + 1) == newRow && (col + 1) == newCol) {
                return !ifContainPiece(stackPaneFields[row + 1][col + 1]);
            }
        }
        return false;
    }

    private boolean isTheFoxWinner() {
        Piece piece = null;
        loop_2:
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                if (stackPaneFields[row][col].getChildren().contains(pieces[4])) {
                    piece = pieces[4];
                    break loop_2;
                }
                piece = null;
            }
        }

        boolean isTheFoxWinner = false;
        if (piece != null && piece.getType() == PieceType.FOX) {
            if (piece.getRowPosition() == 0) {
                isTheFoxWinner = true;
            }
        }
        return isTheFoxWinner;
    }

    private boolean areHoundsWinner() {
        Piece piece = null;
        loop_2:
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                if (stackPaneFields[row][col].getChildren().contains(pieces[4])) {
                    piece = pieces[4];
                    break loop_2;
                }
                piece = null;
            }
        }

        boolean areHoundsWinner = true;
        loop_2:
        if (piece != null && piece.getType() == PieceType.FOX) {
            for (int i = 0; i < ROW_COUNT; i++) {
                for (int j = 0; j < COL_COUNT; j++) {
                    if (isMovementPossibleFox(piece, piece.getRowPosition(), piece.getColPosition(), i, j)) {
                        areHoundsWinner = false;
                        break loop_2;
                    }
                }
            }
        } else {
            areHoundsWinner = false;
        }
        return areHoundsWinner;
    }
}
