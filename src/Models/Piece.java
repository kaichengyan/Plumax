package Models;

import java.util.Scanner;

abstract public class Piece {

    protected Location location;

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

}
