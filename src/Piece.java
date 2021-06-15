import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Piece extends Circle {

    private PieceType type;
    protected int row;
    protected int col;

    public PieceType getType() {
        return type;
    }

    public Piece(PieceType type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        setFill(type == PieceType.FOX ? Color.ORANGE : Color.BLACK);
    }

    public int getRowPosition() {
        return row;
    }

    public int getColPosition() {
        return col;
    }

    public void setNewPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return (type + " " + row + " " + col);
    }
}
