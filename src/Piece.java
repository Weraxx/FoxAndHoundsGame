import javafx.beans.binding.Bindings;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Piece extends StackPane {

    private PieceType type;
    protected int row;
    protected int col;

    public PieceType getType() {
        return type;
    }

    public Piece(PieceType type, int row, int col, StackPane square) {
        this.type = type;
        this.row = row;
        this.col = col;

        Circle circle = new Circle();
        circle.radiusProperty().bind(
                Bindings.when(square.heightProperty().lessThan(square.widthProperty())).
                        then(square.heightProperty()).otherwise(square.widthProperty()).subtract(10).divide(2)
        );

        circle.setFill(type == PieceType.FOX ? Color.BLACK : Color.PINK);
        getChildren().addAll(circle);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
