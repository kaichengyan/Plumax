package Models;
import java.util.*;

public class Board {

    public static final int BOARD_SIZE = 4;
    private ChessPiece[][][] boardMatrix;
    private Map<LocationPair, Boolean> connection;
    private Set<Location> validLocations;

    public static final Location RED_START = new Location(BOARD_SIZE / 4 * 3, BOARD_SIZE, 0);
    public static final Location RED_END = new Location(BOARD_SIZE / 4 + 1, 1, 1);
    public static final Location GREEN_START = new Location(BOARD_SIZE / 4 * 3, BOARD_SIZE / 4 + 1, 0);
    public static final Location GREEN_END = new Location(BOARD_SIZE / 4 + 1, BOARD_SIZE / 4 * 3, 1);
    public static final Location BLUE_START = new Location(1, BOARD_SIZE / 4 + 1, 0);
    public static final Location BLUE_END = new Location(BOARD_SIZE, BOARD_SIZE / 4 * 3, 1);

    public Board() {
        boardMatrix = new ChessPiece[BOARD_SIZE + 1][BOARD_SIZE + 1][2];
        validLocations = new LinkedHashSet<>();
        connection = new HashMap<>();

        for (int x = 1; x <= BOARD_SIZE; x++) {
            for (int y = 1; y <= BOARD_SIZE; y++) {
                for (int d = 0; d < 2; d++) {
                    Location location = new Location(x, y, d);
                    if (location.isValidLocation()) {
                        validLocations.add(location);
                    }
                }
            }
        }

        for (Location l1 : validLocations) {
            for (Location l2 : validLocations) {
                if (l2.isValidLocation()) {
                    boolean isConnected = false;
                    if (l1.equals(l2)) {
                        isConnected = true;
                    }
                    connection.put(new LocationPair(l1, l2), isConnected);
                    connection.put(new LocationPair(l2, l1), isConnected);
                }
            }
        }
    }

    public boolean isLocationOccupied(Location location) {
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        return location.isValidLocation() && boardMatrix[x][y][d] != null;
    }

    public boolean isConnected(Location l1, Location l2) {
        return connection.get(new LocationPair(l1, l2));
    }

    private void connect(Location l1, Location l2) {
        connection.put(new LocationPair(l1, l2), true);
        updateConnections();
    }

    private void updateConnections() {
        for (Location l1 : validLocations)
            for (Location l2 : validLocations)
                for (Location mid : validLocations)
                    if (connection.get(new LocationPair(l1, mid)) && connection.get(new LocationPair(l2, mid)))
                        connection.put(new LocationPair(l1, l2), true);
    }

    private void disconnect(Location l1, Location l2) {
        connection.put(new LocationPair(l1, l2), false);
    }

    public boolean putPiece(ChessPiece piece) {
        switch (piece.getType()) {
            case TYPE_TRIGO:
                return placeTrigo(piece);
            case TYPE_DESTROYER:
                return useDestroyer(piece);
            case TYPE_ONE_WAY:
                return placeOneWay(piece);
            case TYPE_SINGO:
                return placeSingo(piece);
            default:
                return false;
        }
    }

    private boolean placeSingo(ChessPiece piece) {
        int x = piece.getLocation().getX();
        int y = piece.getLocation().getY();
        int d = piece.getLocation().getD();
        Location location = piece.getLocation();
        ChessPiece.Direction direction = piece.getDirection();
        if (boardMatrix[x][y][d] != null) {
            return false;
        }
        boardMatrix[x][y][d] = piece;
        if (direction == ChessPiece.Direction.SINGO_UP_DOWN_CLOSED
                || direction == ChessPiece.Direction.SINGO_UP_LEFT_CLOSED
                || direction == ChessPiece.Direction.SINGO_UP_RIGHT_CLOSED) {
            Location left = new Location(x, y, 0);
            Location right = new Location(x - 1, y, 0);
            Location down = new Location(x, y + 1, 0);
            switch (piece.getDirection()) {
                case SINGO_UP_DOWN_CLOSED:
                    if (left.isValidLocation()) {
                        connect(left, location);
                        connect(location, left);
                    }
                    if (right.isValidLocation()) {
                        connect(right, location);
                        connect(location, right);
                    }
                    break;
                case SINGO_UP_LEFT_CLOSED:
                    if (right.isValidLocation()) {
                        connect(right, location);
                        connect(location, right);
                    }
                    if (down.isValidLocation()) {
                        connect(down, location);
                        connect(location, down);
                    }
                    break;
                case SINGO_UP_RIGHT_CLOSED:
                    if (down.isValidLocation()) {
                        connect(down, location);
                        connect(location, down);
                    }
                    if (left.isValidLocation()) {
                        connect(left, location);
                        connect(location, left);
                    }
                    break;
            }
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            switch (piece.getDirection()) {
                case SINGO_DOWN_LEFT_CLOSED:
                    if (right.isValidLocation()) {
                        connect(right, location);
                        connect(location, right);
                    }
                    if (up.isValidLocation()) {
                        connect(up, location);
                        connect(location, up);
                    }
                    break;
                case SINGO_DOWN_RIGHT_CLOSED:
                    if (up.isValidLocation()) {
                        connect(up, location);
                        connect(location, up);
                    }
                    if (left.isValidLocation()) {
                        connect(left, location);
                        connect(location, left);
                    }
                    break;
                case SINGO_DOWN_UP_CLOSED:
                    if (left.isValidLocation()) {
                        connect(left, location);
                        connect(location, left);
                    }
                    if (right.isValidLocation()) {
                        connect(right, location);
                        connect(location, right);
                    }
                    break;
            }
        }
        return true;
    }

    private boolean placeOneWay(ChessPiece piece) {
        int x = piece.getLocation().getX();
        int y = piece.getLocation().getY();
        int d = piece.getLocation().getD();
        Location location = piece.getLocation();
        ChessPiece.Direction direction = piece.getDirection();
        if (boardMatrix[x][y][d] != null) {
            return false;
        }
        boardMatrix[x][y][d] = piece;
        if (direction == ChessPiece.Direction.ONE_WAY_UP_LEFT_IN
                || direction == ChessPiece.Direction.ONE_WAY_UP_RIGHT_IN
                || direction == ChessPiece.Direction.ONE_WAY_UP_DOWN_IN) {
            Location left = new Location(x, y, 0);
            Location right = new Location(x - 1, y, 0);
            Location down = new Location(x, y + 1, 0);
            if (left.isValidLocation()) connect(location, left);
            if (right.isValidLocation()) connect(location, right);
            if (down.isValidLocation()) connect(location, down);
            switch (piece.getDirection()) {
                case ONE_WAY_UP_LEFT_IN:
                    if (left.isValidLocation()) connect(left, location);
                    break;
                case ONE_WAY_UP_RIGHT_IN:
                    if (right.isValidLocation()) connect(right, location);
                    break;
                case ONE_WAY_UP_DOWN_IN:
                    if (down.isValidLocation()) connect(down, location);
                    break;
            }
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            if (left.isValidLocation()) connect(location, left);
            if (right.isValidLocation()) connect(location, right);
            if (up.isValidLocation()) connect(location, up);
            switch (piece.getDirection()) {
                case ONE_WAY_DOWN_LEFT_IN:
                    if (left.isValidLocation()) connect(left, location);
                    break;
                case ONE_WAY_DOWN_RIGHT_IN:
                    if (right.isValidLocation()) connect(right, location);
                    break;
                case ONE_WAY_DOWN_UP_IN:
                    if (up.isValidLocation()) connect(up, location);
                    break;
            }
        }
        return true;
    }

    private boolean useDestroyer(ChessPiece piece) {
        int x = piece.getLocation().getX();
        int y = piece.getLocation().getY();
        int d = piece.getLocation().getD();
        if (boardMatrix[x][y][d] == null) {
            return false;
        }
        boardMatrix[x][y][d] = null;
        if (piece.getDirection().equals(ChessPiece.Direction.TRIGO_UP)) {
            Location left = new Location(x, y, 0);
            if (left.isValidLocation()) {
                disconnect(piece.getLocation(), left);
                disconnect(left, piece.getLocation());
            }
            Location right = new Location(x - 1, y, 0);
            if (right.isValidLocation()) {
                disconnect(piece.getLocation(), right);
                disconnect(right, piece.getLocation());
            }
            Location down = new Location(x, y + 1, 0);
            if (down.isValidLocation()) {
                disconnect(piece.getLocation(), down);
                disconnect(down, piece.getLocation());
            }
        } else {
            Location left = new Location(x + 1, y, 1);
            if (left.isValidLocation()) {
                disconnect(piece.getLocation(), left);
                disconnect(left, piece.getLocation());
            }
            Location right = new Location(x, y, 1);
            if (right.isValidLocation()) {
                disconnect(piece.getLocation(), right);
                disconnect(right, piece.getLocation());
            }
            Location up = new Location(x, y - 1, 1);
            if (up.isValidLocation()) {
                disconnect(piece.getLocation(), up);
                disconnect(up, piece.getLocation());
            }
        }
        return true;
    }

    private boolean placeTrigo(ChessPiece piece) {
        int x = piece.getLocation().getX();
        int y = piece.getLocation().getY();
        int d = piece.getLocation().getD();
        if (boardMatrix[x][y][d] != null) {
            return false;
        }
        boardMatrix[x][y][d] = piece;
        if (piece.getDirection().equals(ChessPiece.Direction.TRIGO_UP)) {
            Location left = new Location(x, y, 0);
            if (left.isValidLocation()) {
                connect(piece.getLocation(), left);
            }
            Location right = new Location(x - 1, y, 0);
            if (right.isValidLocation()) {
                connect(piece.getLocation(), right);
            }
            Location down = new Location(x, y + 1, 0);
            if (down.isValidLocation()) {
                connect(piece.getLocation(), down);
            }
        } else {
            Location left = new Location(x + 1, y, 1);
            if (left.isValidLocation()) {
                connect(piece.getLocation(), left);
            }
            Location right = new Location(x, y, 1);
            if (right.isValidLocation()) {
                connect(piece.getLocation(), right);
            }
            Location up = new Location(x, y - 1, 1);
            if (up.isValidLocation()) {
                connect(piece.getLocation(), up);
            }
        }
        return true;
    }

}
