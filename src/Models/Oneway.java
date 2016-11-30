package Models;

import java.util.Scanner;

public class Oneway extends Piece {

    private Direction direction;
    
    public Oneway(Location location, Direction direction) {
        this.location = location;
        this.direction = direction;
    }

    public Oneway(Scanner input) {
        this.location = readLocation(input);
        this.direction = readDirection(input);
    }

    @Override
    public boolean use(Board board) {
        int x = getLocation().getX();
        int y = getLocation().getY();
        int d = getLocation().getD();
        if (!board.isLocationPuttable(location)) {
            return false;
        }
        board.setBoardMatrix(location, this);
        if (direction == Direction.ONE_WAY_UP_LEFT_IN
                || direction == Direction.ONE_WAY_UP_RIGHT_IN
                || direction == Direction.ONE_WAY_UP_DOWN_IN) {
            Location left = new Location(x, y, 0);
            Location right = new Location(x - 1, y, 0);
            Location down = new Location(x, y + 1, 0);
            switch (direction) {
                case ONE_WAY_UP_LEFT_IN:
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    board.connect(location, down, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
                case ONE_WAY_UP_RIGHT_IN:
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    board.connect(location, down, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
                case ONE_WAY_UP_DOWN_IN:
                    board.connect(location, down, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
            }
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            switch (direction) {
                case ONE_WAY_DOWN_LEFT_IN:
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    board.connect(location, up, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
                case ONE_WAY_DOWN_RIGHT_IN:
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    board.connect(location, up, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
                case ONE_WAY_DOWN_UP_IN:
                    board.connect(location, up, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
            }
        }
        return true;
    }

    private Direction readDirection(Scanner input) {
        System.out.println("\tWhere do you want the one-way-in path? ");
        if (location.getD() == 1) {
            System.out.println("\t1) Upper-right");
            System.out.println("\t2) Upper-left");
            System.out.println("\t3) Down");
            System.out.print("\t");
            switch (input.nextInt()) {
                case 1:
                    return Direction.ONE_WAY_UP_RIGHT_IN;
                case 2:
                    return Direction.ONE_WAY_UP_LEFT_IN;
                case 3:
                    return Direction.ONE_WAY_UP_DOWN_IN;
                default:
                    return null;
            }
        } else {
            System.out.println("\t1) Lower-right");
            System.out.println("\t2) Lower-left");
            System.out.println("\t3) Up");
            System.out.print("\t");
            switch (input.nextInt()) {
                case 1:
                    return Direction.ONE_WAY_DOWN_RIGHT_IN;
                case 2:
                    return Direction.ONE_WAY_DOWN_LEFT_IN;
                case 3:
                    return Direction.ONE_WAY_DOWN_UP_IN;
                default:
                    return null;
            }
        }
    }

    public enum Direction {
        ONE_WAY_UP_RIGHT_IN,
        ONE_WAY_UP_DOWN_IN,
        ONE_WAY_UP_LEFT_IN,
        ONE_WAY_DOWN_UP_IN,
        ONE_WAY_DOWN_LEFT_IN,
        ONE_WAY_DOWN_RIGHT_IN
    }
}
