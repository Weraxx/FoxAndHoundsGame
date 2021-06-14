public enum PieceType {
    FOX(1), HOUNDS(-1);

    final int moveDirection;

    PieceType(int moveDirection) {
        this.moveDirection = moveDirection;
    }
}
