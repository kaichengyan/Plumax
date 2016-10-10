package Models;

import java.util.*;

public class Singo extends Piece {

    public Singo(Location location, Direction direction) {
        this.location = location;
        this.direction = direction;
    }

    public Singo(Scanner input) {
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
        if (direction == Piece.Direction.SINGO_UP_DOWN_CLOSED
                || direction == Piece.Direction.SINGO_UP_LEFT_CLOSED
                || direction == Piece.Direction.SINGO_UP_RIGHT_CLOSED) {
            Location left = new Location(x, y, 0);
            Location right = new Location(x - 1, y, 0);
            Location down = new Location(x, y + 1, 0);
            switch (getDirection()) {
                case SINGO_UP_DOWN_CLOSED:
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OPEN);
                    break;
                case SINGO_UP_LEFT_CLOSED:
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, down, Board.ConnectionType.MONOLATERAL_OPEN);
                    break;
                case SINGO_UP_RIGHT_CLOSED:
                    board.connect(location, down, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OPEN);
                    break;
            }
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            switch (getDirection()) {
                case SINGO_DOWN_LEFT_CLOSED:
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, up, Board.ConnectionType.MONOLATERAL_OPEN);
                    break;
                case SINGO_DOWN_RIGHT_CLOSED:
                    board.connect(location, up, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OPEN);
                    break;
                case SINGO_DOWN_UP_CLOSED:
                    board.connect(location, left, Board.ConnectionType.MONOLATERAL_OPEN);
                    board.connect(location, right, Board.ConnectionType.MONOLATERAL_OPEN);
                    break;
            }
        }
        return true;
    }

    private Direction readDirection(Scanner input) {
        System.out.println("\tWhere do you want the closed side? ");
        if (location.getD() == 1) {
            System.out.println("\t1) Upper-right");
            System.out.println("\t2) Upper-left");
            System.out.println("\t3) Down");
            System.out.print("\t");
            switch (input.nextInt()) {
                case 1:
                    return Piece.Direction.SINGO_UP_RIGHT_CLOSED;
                case 2:
                    return Piece.Direction.SINGO_UP_LEFT_CLOSED;
                case 3:
                    return Piece.Direction.SINGO_UP_DOWN_CLOSED;
                default:
                    throw new IllegalArgumentException();
            }
        } else {
            System.out.println("\t1) Lower-right");
            System.out.println("\t2) Lower-left");
            System.out.println("\t3) Up");
            System.out.print("\t");
            switch (input.nextInt()) {
                case 1:
                    return Piece.Direction.SINGO_DOWN_RIGHT_CLOSED;
                case 2:
                    return Piece.Direction.SINGO_DOWN_LEFT_CLOSED;
                case 3:
                    return Piece.Direction.SINGO_DOWN_UP_CLOSED;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}
