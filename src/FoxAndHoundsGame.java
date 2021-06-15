import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class FoxAndHoundsGame extends Application {

    public static final int COL_COUNT = 8;
    public static final int ROW_COUNT = 8;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(getBoard(), 600, 600);

        primaryStage.setTitle("Board game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane getBoard() {
        BoardSquare[][] boardSquares = new BoardSquare[8][8];
        GridPane board = new GridPane();
        for (int col = 0; col < COL_COUNT; ++col) {
            for (int row = 0; row < ROW_COUNT; ++row) {

                StackPane stackPaneField = new StackPane();

                BoardSquare boardSquare;
                if ((row + col) % 2 == 0) {
                    boardSquare = new BoardSquare(Color.WHITESMOKE);
                } else {
                    boardSquare = new BoardSquare(Color.GRAY);
                }
                stackPaneField.getChildren().add(boardSquare);
                boardSquares[row][col] = boardSquare;

                Piece piece = null;
                if ((row < 1) && ((row + col) % 2 != 0)) {
                    piece = makePiece(PieceType.HOUNDS, row, col);
                }
                if ((row == 7) && (col == 0)) {
                    piece = makePiece(PieceType.FOX, row, col);
                }
                boardSquare.setPiece(piece);
                if (piece != null) {
                    piece.radiusProperty().bind(
                            Bindings.when(stackPaneField.heightProperty().lessThan(stackPaneField.widthProperty())).
                                    then(stackPaneField.heightProperty()).otherwise(stackPaneField.widthProperty()).subtract(10).divide(2)
                    );
                    stackPaneField.getChildren().add(piece);
                }

                Piece finalPiece = piece;
                int finalCol = col;
                int finalRow = row;
                stackPaneField.setOnMouseEntered(e -> {
                    boardSquare.highlight();
                    boardSquare.showPossibilities(boardSquare,boardSquares,finalPiece,finalRow,finalCol);
                });
                stackPaneField.setOnMouseExited(e -> {
                    boardSquare.blacken();
                    boardSquare.hidePossibilities(boardSquare,boardSquares,finalPiece,finalRow,finalCol);
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

    private Piece makePiece(PieceType type, int row, int kol) {
        return new Piece(type, row, kol);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
