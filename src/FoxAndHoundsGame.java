import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FoxAndHoundsGame extends Application {

    public static final int COL_COUNT = 8;
    public static final int ROW_COUNT = 8;

    public static final Piece hound_01 = new Piece(PieceType.HOUNDS, 0, 1);
    public static final Piece hound_02 = new Piece(PieceType.HOUNDS, 0, 3);
    public static final Piece hound_03 = new Piece(PieceType.HOUNDS, 0, 5);
    public static final Piece hound_04 = new Piece(PieceType.HOUNDS, 0, 7);
    public static final Piece fox = new Piece(PieceType.FOX, 7, 0);
    public static final Piece[] pieces = {hound_01, hound_02, hound_03, hound_04, fox};

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

    private GridPane getBoard() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        GridPane board = new GridPane();
        boolean[] czyPoprzednieKlikniecieNaPionku = {false};
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

                stackPaneField.setOnMouseEntered(e -> {
                    boardSquare.highlight();
                    if (areHoundsWinner()) {
                        System.out.println("HOUNDS ARE THE WINNER");
                    }
                    if (isTheFoxWinner()) {
                        System.out.println("FOX IS THE WINNER");
                    }
                });
                stackPaneField.setOnMouseExited(e -> boardSquare.blacken());
                stackPaneField.setOnMouseClicked(e -> {
                    if (czyZawieraPiece(stackPaneField) && !czyPoprzednieKlikniecieNaPionku[0]) {
                        // klikniecie w pionek i czy poprzednie nie bylo na pionku
                        czyPoprzednieKlikniecieNaPionku[0] = true;
                        showPossibilities(boardSquares, stackPaneField, zwrocObiektPiece(stackPaneField), boardSquare.getBoardSquareRow(), boardSquare.getBoardSquareCol());
                        tmpPiece[0] = zwrocObiektPiece(stackPaneField);
                    } else if (!czyZawieraPiece(stackPaneField) && czyPoprzednieKlikniecieNaPionku[0]) {
                        // nie zawiera pionka - klikniecie w pole, a poprzednie klikniecie na pionku
                        int newRow = boardSquare.getBoardSquareRow();
                        int newCol = boardSquare.getBoardSquareCol();
                        if (isMovementPossibleFox(tmpPiece[0], tmpPiece[0].getRowPosition(), tmpPiece[0].getColPosition(), newRow, newCol) || isMovementPossibleHounds(tmpPiece[0], tmpPiece[0].getRowPosition(), tmpPiece[0].getColPosition(), newRow, newCol)) {
                            stackPaneFields[newRow][newCol].getChildren().add(tmpPiece[0]);
                            tmpPiece[0].setNewPosition(newRow, newCol);
                        }
                        hidePossibilities(boardSquares);
                        czyPoprzednieKlikniecieNaPionku[0] = false;
                    } else if (czyPoprzednieKlikniecieNaPionku[0]) {
                        hidePossibilities(boardSquares);
                        czyPoprzednieKlikniecieNaPionku[0] = false;
                    }
                });
                board.add(stackPaneField, col, row);
            }
        }

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

        return board;
    }

    private boolean czyZawieraPiece(StackPane stackPane) {
        for (Piece piece : pieces) {
            if (stackPane.getChildren().contains(piece)) {
                return true;
            }
        }
        return false;
    }

    private Piece zwrocObiektPiece(StackPane stackPane) {
        for (Piece piece : pieces) {
            if (stackPane.getChildren().contains(piece)) {
                return piece;
            }
        }
        return null;
    }

    private void showPossibilities(BoardSquare[][] boardSquares, StackPane stackPaneField, Piece piece, int row, int col) {
        if (czyZawieraPiece(stackPaneField)) {
            if (piece.getType() == PieceType.FOX) {
                // PODSWIETLA MOZLIWE RUCHY DLA FOXA
                if ((row + 1) < 8 && (col - 1) >= 0) {
                    if (!czyZawieraPiece(stackPaneFields[row + 1][col - 1])) {
                        boardSquares[row + 1][col - 1].highlightPossibilities();
                    }
                }
                if ((row + 1) < 8 && (col + 1) < 8) {
                    if (!czyZawieraPiece(stackPaneFields[row + 1][col + 1])) {
                        boardSquares[row + 1][col + 1].highlightPossibilities();
                    }
                }
                if ((row - 1) >= 0 && (col - 1) >= 0) {
                    if (!czyZawieraPiece(stackPaneFields[row - 1][col - 1])) {
                        boardSquares[row - 1][col - 1].highlightPossibilities();
                    }
                }
                if ((col + 1) < 8 && (row - 1) >= 0) {
                    if (!czyZawieraPiece(stackPaneFields[row - 1][col + 1])) {
                        boardSquares[row - 1][col + 1].highlightPossibilities();
                    }
                }
            } else {
                // PODSWIETLA MOZLIWE RUCHY DLA HOUNDA
                if ((row + 1) < 8 && (col - 1) >= 0) {
                    if (!czyZawieraPiece(stackPaneFields[row + 1][col - 1])) {
                        boardSquares[row + 1][col - 1].highlightPossibilities();
                    }
                }
                if ((row + 1) < 8 && (col + 1) < 8) {
                    if (!czyZawieraPiece(stackPaneFields[row + 1][col + 1])) {
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
                if (!czyZawieraPiece(stackPaneFields[row + 1][col - 1])) {
                    return true;
                }
            }
            if ((row + 1) == newRow && (col + 1) == newCol) {
                if (!czyZawieraPiece(stackPaneFields[row + 1][col + 1])) {
                    return true;
                }
            }
            if ((row - 1) == newRow && (col - 1) == newCol) {
                if (!czyZawieraPiece(stackPaneFields[row - 1][col - 1])) {
                    return true;
                }
            }
            if ((col + 1) == newCol && (row - 1) == newRow) {
                return !czyZawieraPiece(stackPaneFields[row - 1][col + 1]);
            }
        }
        return false;
    }

    private boolean isMovementPossibleHounds(Piece piece, int row, int col, int newRow, int newCol) {
        if (piece.getType() == PieceType.HOUNDS) {
            if ((row + 1) == newRow && (col - 1) == newCol) {
                if (!czyZawieraPiece(stackPaneFields[row + 1][col - 1])) {
                    return true;
                }
            }
            if ((row + 1) == newRow && (col + 1) == newCol) {
                return !czyZawieraPiece(stackPaneFields[row + 1][col + 1]);
            }
        }
        return false;
    }

    private boolean isTheFoxWinner() {
        Piece piece = null;
        loop:
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (stackPaneFields[row][col].getChildren().contains(pieces[4])) {
                    piece = pieces[4];
                    break loop;
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
