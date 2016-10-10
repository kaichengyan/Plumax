package Models;

import java.util.Scanner;

public class Destroyer extends Piece {

    public Destroyer(Location location) {
        this.location = location;
        this.direction = Direction.DESTROYER;
    }

    public Destroyer(Scanner input) {
        this.location = readLocation(input);
    }

    @Override
    public boolean use(Board board) {
        Location location = getLocation();
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        if (board.getBoardMatrix()[x][y][d] == null) {
            return false;
        }
        if (!board.isLocationRemovable(location)) {
            return false;
        }
        board.setBoardMatrix(location, null);
        if (d == 1) {
            Location left = new Location(x, y, 0);
            Location right = new Location(x - 1, y, 0);
            Location down = new Location(x, y + 1, 0);
            board.disconnect(location, left);
            board.disconnect(location, right);
            board.disconnect(location, down);
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            board.disconnect(location, left);
            board.disconnect(location, right);
            board.disconnect(location, up);
        }
        return true;
    }

    @Override
    public Type getType() {
        return Type.TYPE_DESTROYER;
    }
}
