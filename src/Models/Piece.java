package Models;

import java.util.*;

abstract public class Piece {

    protected Location location;
    protected Direction direction;

    public enum Type {
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
        TRIGO,
        DESTROYER
    }

    abstract public boolean use(Board board);

    protected Location readLocation(Scanner input) {
        System.out.print("\tWhat location would you like to put it? ");
        int x = input.nextInt();
        int y = input.nextInt();
        int d = input.nextInt();
        return new Location(x, y, d);
    }

    public Location getLocation() {
        return location;
    }

    public Direction getDirection() {
        return direction;
    }

}
