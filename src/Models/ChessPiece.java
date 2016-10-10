package Models;

public class ChessPiece {

    private PieceType type;
    private Location location;
    private Direction direction;

    public enum PieceType {
        TYPE_SINGO,
        TYPE_TRIGO,
        TYPE_DESTROYER,
        TYPE_ONE_WAY;
        @Override
        public String toString() {
            switch(this) {
                case TYPE_SINGO: return "Singo";
                case TYPE_TRIGO: return "Trigo";
                case TYPE_ONE_WAY: return "Oneway";
                case TYPE_DESTROYER: return "Destroyer";
                default: throw new IllegalArgumentException();
            }
        }
    }

    public enum Direction {
        ONE_WAY_UP_RIGHT_IN,
        ONE_WAY_UP_LEFT_IN,
        ONE_WAY_UP_DOWN_IN,
        ONE_WAY_DOWN_RIGHT_IN,
        ONE_WAY_DOWN_LEFT_IN,
        ONE_WAY_DOWN_UP_IN,
        SINGO_UP_LEFT_CLOSED,
        SINGO_UP_RIGHT_CLOSED,
        SINGO_UP_DOWN_CLOSED,
        SINGO_DOWN_LEFT_CLOSED,
        SINGO_DOWN_RIGHT_CLOSED,
        SINGO_DOWN_UP_CLOSED,
        TRIGO_UP,
        TRIGO_DOWN,
        DESTROYER
    }

    public ChessPiece(PieceType type, Location location, Direction direction) {
        this.type = type;
        this.location = location;
        this.direction = direction;
    }

    public PieceType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public Direction getDirection() {
        return direction;
    }

}
