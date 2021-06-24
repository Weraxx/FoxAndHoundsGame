import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

    private static final Piece hound_01 = new Piece(PieceType.HOUNDS, 0, 1);
    private static final Piece hound_02 = new Piece(PieceType.HOUNDS, 0, 3);
    private static final Piece hound_03 = new Piece(PieceType.HOUNDS, 0, 5);
    private static final Piece hound_04 = new Piece(PieceType.HOUNDS, 0, 7);
    private static final Piece fox = new Piece(PieceType.FOX, 7, 0);
    private static final Piece[] pieces = {hound_01, hound_02, hound_03, hound_04, fox};
    private static final Piece[] lastMove = {hound_01};
    private static final Piece[] hounds = {hound_01, hound_02, hound_03, hound_04};

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(getStartWindow(primaryStage), 800, 600);

        primaryStage.setTitle("Fox and Hounds");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private StackPane getStartWindow(Stage primaryStage) {
        StackPane startWindow = new StackPane();
        Button startButton = new Button("START");
        startButton.setPrefSize(150, 50);
        startWindow.setAlignment(Pos.CENTER);
        Image image = new Image("Game-of-Fox-and-Hounds.jpg");
        ImageView imageView = new ImageView(image);
        imageView.fitHeightProperty().bind(startWindow.heightProperty());
        imageView.fitWidthProperty().bind(startWindow.widthProperty());
        startWindow.getChildren().addAll(imageView, startButton);

        startButton.setOnMouseClicked(e -> {
            primaryStage.setScene(new Scene(getBoard(), 600, 600));
            primaryStage.show();
        });

        return startWindow;
    }

    StackPane[][] stackPaneFields = new StackPane[8][8];
    BoardSquare[][] boardSquares = new BoardSquare[8][8];

    private VBox getBoard() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Options");
        menuBar.getMenus().add(menu);
        MenuItem menuItemSave = new MenuItem("save");
        MenuItem menuItemOpen = new MenuItem("open");
        menu.getItems().addAll(menuItemSave, menuItemOpen);

        VBox boardWithMenuBar = new VBox(menuBar);

        GridPane board = new GridPane();

        boolean[] ifLastClickOnPiece = {false};
        Piece[] tmpPiece = {null};

        for (int row = 0; row < COL_COUNT; ++row) {
            for (int col = 0; col < ROW_COUNT; ++col) {

                StackPane stackPaneField = new StackPane();

                BoardSquare boardSquare;
                if ((row + col) % 2 == 0) {
                    boardSquare = new BoardSquare(Color.WHITESMOKE, row, col);
                } else {
                    boardSquare = new BoardSquare(Color.GRAY, row, col);
                }
                stackPaneField.getChildren().add(boardSquare);
                boardSquares[row][col] = boardSquare;
                stackPaneFields[row][col] = stackPaneField;

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


                stackPaneField.setOnMouseEntered(e -> boardSquare.highlight());
                stackPaneField.setOnMouseExited(e -> boardSquare.blacken());
                Alert movementWarning = new Alert(Alert.AlertType.WARNING);
                movementWarning.setHeaderText(null);
                stackPaneField.setOnMouseClicked(e -> {
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
                        }
                        hidePossibilities(boardSquares);
                        ifLastClickOnPiece[0] = false;
                    } else if (ifLastClickOnPiece[0]) {
                        hidePossibilities(boardSquares);
                        ifLastClickOnPiece[0] = false;
                    } else {
                        if (lastMove[0].getType() == PieceType.FOX) {
                            movementWarning.setContentText("HOUNDS TURN");
                        } else {
                            movementWarning.setContentText("FOX'S TURN");
                        }
                        movementWarning.showAndWait();
                    }
                });
                board.add(stackPaneField, col, row);
            }
        }

        Alert alertWinnerOfTheGame = new Alert(Alert.AlertType.INFORMATION);
        alertWinnerOfTheGame.setTitle("WINNER");
        alertWinnerOfTheGame.setHeaderText(null);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(1), e -> {
            if (areHoundsWinner()) {
                timeline.stop();
                alertWinnerOfTheGame.setContentText("HOUNDS ARE THE WINNER");
                alertWinnerOfTheGame.show();
            }
            if (isTheFoxWinner()) {
                timeline.stop();
                alertWinnerOfTheGame.setContentText("FOX IS THE WINNER");
                alertWinnerOfTheGame.show();
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        menuItemSave.setOnAction(actionEvent -> {
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                save(file, stackPaneFields);
            }
        });

        Alert alertWrongExtension = new Alert(Alert.AlertType.WARNING);
        alertWrongExtension.setHeaderText(null);
        alertWrongExtension.setContentText("Wrong file extension");
        menuItemOpen.setOnAction(actionEvent -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null && file.getAbsolutePath().endsWith(".txt")) {
                open(file, stackPaneFields);
            }
            else{
                alertWrongExtension.showAndWait();
            }
        });

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
        return boardWithMenuBar;
    }

    private void save(File file, StackPane[][] stackPaneFields) {
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(file.getAbsolutePath());
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (ifContainPiece(stackPaneFields[row][col])) {
                        printWriter.print(deliverObjectPiece(stackPaneFields[row][col]));
                        printWriter.print("\r\n");
                    }
                }
            }
            printWriter.print("last " + lastMove[0]);
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void open(File file, StackPane[][] stackPaneFields) {
        deletePieces(stackPaneFields);
        Scanner lineScanner = null;
        try {
            lineScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                } else {
                    lastMove[0] = hounds[0];
                }
            } else {
                tmpRow = Integer.parseInt(tmpData[1]);
                tmpCol = Integer.parseInt(tmpData[2]);
                if (tmpType.equals("FOX")) {
                    fox.setNewPosition(tmpRow, tmpCol);
                    stackPaneFields[tmpRow][tmpCol].getChildren().add(fox);
                } else {
                    hounds[houndCount].setNewPosition(tmpRow, tmpCol);
                    stackPaneFields[tmpRow][tmpCol].getChildren().add(hounds[houndCount]);
                    houndCount++;
                }
            }

        }
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
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (ifContainPiece(stackPaneFields[row][col])) {
                    stackPaneFields[row][col].getChildren().remove(1);
                }
            }
        }
    }

    private void showPossibilities(BoardSquare[][] boardSquares, StackPane stackPaneField, Piece piece, int row, int col) {
        if (ifContainPiece(stackPaneField)) {
            if (piece.getType() == PieceType.FOX) {
                if ((row + 1) < 8 && (col - 1) >= 0) {
                    if (!ifContainPiece(stackPaneFields[row + 1][col - 1])) {
                        boardSquares[row + 1][col - 1].highlightPossibilities();
                    }
                }
                if ((row + 1) < 8 && (col + 1) < 8) {
                    if (!ifContainPiece(stackPaneFields[row + 1][col + 1])) {
                        boardSquares[row + 1][col + 1].highlightPossibilities();
                    }
                }
                if ((row - 1) >= 0 && (col - 1) >= 0) {
                    if (!ifContainPiece(stackPaneFields[row - 1][col - 1])) {
                        boardSquares[row - 1][col - 1].highlightPossibilities();
                    }
                }
                if ((col + 1) < 8 && (row - 1) >= 0) {
                    if (!ifContainPiece(stackPaneFields[row - 1][col + 1])) {
                        boardSquares[row - 1][col + 1].highlightPossibilities();
                    }
                }
            } else {
                if ((row + 1) < 8 && (col - 1) >= 0) {
                    if (!ifContainPiece(stackPaneFields[row + 1][col - 1])) {
                        boardSquares[row + 1][col - 1].highlightPossibilities();
                    }
                }
                if ((row + 1) < 8 && (col + 1) < 8) {
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
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
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
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
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
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
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

    public static void main(String[] args) {
        launch(args);
    }
}
