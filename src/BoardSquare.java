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

    public void highlightPossibilities() {
        setColor(Color.LAVENDER);
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

    public void showPossibilities(BoardSquare boardSquare, BoardSquare[][] boardSquares, Piece piece, int row, int col) {
        if (boardSquare.hasPiece()) {
            if (piece.getType() == PieceType.FOX) {
                // PODSWIETLA MOZLIWE RUCHY DLA FOXA
                if ((row + 1) < 8 && (col - 1) >= 0) {
                    boardSquares[row + 1][col - 1].highlightPossibilities();
                }
                if ((row + 1) < 8 && (col + 1) < 8) {
                    boardSquares[row + 1][col + 1].highlightPossibilities();
                }
                if ((row - 1) > 0 && (col - 1) >= 0) {
                    boardSquares[row - 1][col - 1].highlightPossibilities();
                }
                if ((col + 1) < 8 && (row - 1) >= 0) {
                    boardSquares[row - 1][col + 1].highlightPossibilities();
                }
            } else {
                // PODSWIETLA MOZLIWE RUCHY DLA HOUNDA
                if ((row + 1) < 8 && (col - 1) >= 0) {
                    boardSquares[row + 1][col - 1].highlightPossibilities();
                }
                if ((row + 1) < 8 && (col + 1) < 8) {
                    boardSquares[row + 1][col + 1].highlightPossibilities();
                }
            }
        }
    }

    public void hidePossibilities(BoardSquare boardSquare, BoardSquare[][] boardSquares, Piece piece, int row, int col) {
        if (boardSquare.hasPiece()) {
            if (piece.getType() == PieceType.FOX) {
                if ((row + 1) < 8 && (col - 1) >= 0) {
                    boardSquares[row + 1][col - 1].blacken();
                }
                if ((row + 1) < 8 && (col + 1) < 8) {
                    boardSquares[row + 1][col + 1].blacken();
                }
                if ((row - 1) > 0 && (col - 1) >= 0) {
                    boardSquares[row - 1][col - 1].blacken();
                }
                if ((col + 1) < 8 && (row - 1) >= 0) {
                    boardSquares[row - 1][col + 1].blacken();
                }
            } else {
                // PODSWIETLA MOZLIWE RUCHY DLA HOUNDA
                if ((row + 1) < 8 && (col - 1) >= 0) {
                    boardSquares[row + 1][col - 1].blacken();
                }
                if ((row + 1) < 8 && (col + 1) < 8) {
                    boardSquares[row + 1][col + 1].blacken();
                }
            }
        }
    }
}

