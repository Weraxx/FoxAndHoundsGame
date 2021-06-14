import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class FoxAndHoundsGame extends Application {

    public static final int COL_COUNT = 8;
    public static final int ROW_COUNT = 8;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(getBoard(), 300, 275);

        primaryStage.setTitle("Board game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane getBoard() {
        GridPane board = new GridPane();
        for (int col = 0; col < COL_COUNT; ++col) {
            for (int row = 0; row < ROW_COUNT; ++row) {

                StackPane square = new StackPane();

                BoardSquare boardSquare;
                if ((row + col) % 2 == 0) {
                    boardSquare = new BoardSquare(Color.WHITESMOKE);
                } else {
                    boardSquare = new BoardSquare(Color.GRAY);
                }

                Piece piece = null;
                if (row < 1 && (row + col) % 2 == 0) {
                    piece = makePiece(PieceType.HOUNDS, row, col, square);
                }
                if (row == 7 && col == 0) {
                    piece = makePiece(PieceType.FOX, row, col, square);
                }
                boardSquare.setPiece(piece);

                square.getChildren().addAll(boardSquare);

                square.setOnMouseEntered(e -> boardSquare.highlight());
                square.setOnMouseExited(e -> boardSquare.blacken());

                board.add(square, col, row);
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


    private Piece makePiece(PieceType type, int row, int col, StackPane square) {
        return new Piece(type, row, col, square);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
