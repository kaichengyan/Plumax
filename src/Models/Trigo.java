package Models;

import java.util.Scanner;

public class Trigo extends Piece {

    public Trigo(Location location) {
        this.location = location;
        this.direction = Direction.TRIGO;
    }

    public Trigo(Scanner input) {
        this.location = readLocation(input);
        this.direction = Direction.TRIGO;
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
        if (d == 1) {
            Location left = new Location(x, y, 0);
            Location right = new Location(x - 1, y, 0);
            Location down = new Location(x, y + 1, 0);
            board.connect(getLocation(), down, LocationPair.ConnectionType.MONOLATERAL_OPEN);
            board.connect(getLocation(), left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
            board.connect(getLocation(), right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            board.connect(getLocation(), up, LocationPair.ConnectionType.MONOLATERAL_OPEN);
            board.connect(getLocation(), left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
            board.connect(getLocation(), right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
        }
        return true;
    }

    @Override
    public Type getType() {
        return Type.TYPE_TRIGO;
    }

    public Location getLocation() {
        return location;
    }

}
