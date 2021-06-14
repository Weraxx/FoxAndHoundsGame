import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class BoardSquare extends Region {

    private Color color;
    private Piece piece;

    public BoardSquare(Color color) {
        this.color = color;
        setColor(color);
    }

    public void highlight() {
        setColor(Color.LIGHTBLUE);
    }

    public void blacken() {
        setColor(color);
    }

    private void setColor(Color color) {
        BackgroundFill fill = new BackgroundFill(color, null, new Insets(1));
        Background background = new Background(fill);
        setBackground(background);
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int showPossibility() {
        if (piece.getType() == PieceType.FOX) {
            return 0;
        }
        return 0;
    }
}

