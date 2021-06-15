import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class BoardSquare extends Region {

    private Color color;
    private int boardSquareRow;
    private int boardSquareCol;

    public BoardSquare(Color color, int boardSquareRow, int boardSquareCol) {
        this.color = color;
        this.boardSquareRow = boardSquareRow;
        this.boardSquareCol = boardSquareCol;
        setColor(color);
    }

    public int getBoardSquareRow() {
        return boardSquareRow;
    }

    public int getBoardSquareCol() {
        return boardSquareCol;
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

    public void highlightPossibilities() {
        setColor(Color.LAVENDER);
    }
}

