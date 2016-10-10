package Models;
import java.util.*;

public class Board {

    public static final int BOARD_SIZE = 1; // Should be any positive integer.

    private ChessPiece[][][] boardMatrix;
    private Map<LocationPair, Boolean> connection;
    private Map<LocationPair, LocationPair.ConnectionType> pendingConnections;
    private Set<Location> validLocations;

    public static final Location RED_START = new Location(BOARD_SIZE * 3, BOARD_SIZE * 4, 0);
    private static final Location RED_START_NEXT = new Location(BOARD_SIZE * 3, BOARD_SIZE * 4 - 1, 1);
    public static final Location RED_END = new Location(BOARD_SIZE + 1, 1, 1);
    private static final Location RED_END_NEXT = new Location(BOARD_SIZE + 1, 2, 0);
    public static final Location GREEN_START = new Location(BOARD_SIZE * 3, BOARD_SIZE + 1, 0);
    private static final Location GREEN_START_NEXT = new Location(BOARD_SIZE * 3, BOARD_SIZE + 1, 1);
    public static final Location GREEN_END = new Location(BOARD_SIZE + 1, BOARD_SIZE * 3, 1);
    private static final Location GREEN_END_NEXT = new Location(BOARD_SIZE + 1, BOARD_SIZE * 3, 0);
    public static final Location BLUE_START = new Location(1, BOARD_SIZE + 1, 0);
    private static final Location BLUE_START_NEXT = new Location(2, BOARD_SIZE + 1, 1);
    public static final Location BLUE_END = new Location(BOARD_SIZE * 4, BOARD_SIZE * 3, 1);
    private static final Location BLUE_END_NEXT = new Location(BOARD_SIZE * 4 - 1, BOARD_SIZE * 3, 0);

    public Board() {
        boardMatrix = new ChessPiece[BOARD_SIZE * 4+ 1][BOARD_SIZE * 4 + 1][2];
        validLocations = new HashSet<>();
        connection = new HashMap<>();
        pendingConnections = new HashMap<>();

        for (int x = 1; x <= BOARD_SIZE * 4; x++) {
            for (int y = 1; y <= BOARD_SIZE * 4; y++) {
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
                pendingConnections.put(new LocationPair(l1, l2), LocationPair.ConnectionType.MONOLATERAL_CLOSED);
                boolean isConnected = false;
                if (l1.equals(l2)) {
                    isConnected = true;
                }
                connection.put(new LocationPair(l1, l2), isConnected);
                connection.put(new LocationPair(l2, l1), isConnected);
            }
        }

        connect(RED_START, RED_START_NEXT, LocationPair.ConnectionType.MONOLATERAL_OPEN);
        connect(RED_END, RED_END_NEXT, LocationPair.ConnectionType.MONOLATERAL_OPEN);
        connect(GREEN_START, GREEN_START_NEXT, LocationPair.ConnectionType.MONOLATERAL_OPEN);
        connect(GREEN_END, GREEN_END_NEXT, LocationPair.ConnectionType.MONOLATERAL_OPEN);
        connect(BLUE_START, BLUE_START_NEXT, LocationPair.ConnectionType.MONOLATERAL_OPEN);
        connect(BLUE_END, BLUE_END_NEXT, LocationPair.ConnectionType.MONOLATERAL_OPEN);

        update();

    }

    private boolean isLocationEnds(Location location) {
        return location.equals(RED_START)
                || location.equals(RED_END)
                || location.equals(BLUE_START)
                || location.equals(BLUE_END)
                || location.equals(GREEN_START)
                || location.equals(GREEN_END);
    }

    public boolean isLocationPuttable(Location location) {
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        return location.isValidLocation() && !isLocationEnds(location) && boardMatrix[x][y][d] == null;
    }

    public boolean isLocationRemovable(Location location) {
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        return location.isValidLocation() && !isLocationEnds(location) && boardMatrix[x][y][d] != null;
    }

    public boolean isConnected(Location from, Location to) {
        return connection.get(new LocationPair(from, to));
    }

    private void connect(Location from, Location to, LocationPair.ConnectionType type) {
        if (from.isValidLocation() && to.isValidLocation()) {
            LocationPair.ConnectionType reverseConn = pendingConnections.get(new LocationPair(to, from));
            pendingConnections.put(new LocationPair(from, to), type);
            if (type == LocationPair.ConnectionType.MONOLATERAL_OPEN) {
                if (reverseConn == LocationPair.ConnectionType.MONOLATERAL_OPEN) {
                    pendingConnections.put(new LocationPair(from, to),
                            LocationPair.ConnectionType.BILATERAL_OPEN);
                    pendingConnections.put(new LocationPair(to, from),
                            LocationPair.ConnectionType.BILATERAL_OPEN);
                } else if (reverseConn == LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY) {
                    pendingConnections.put(new LocationPair(from, to),
                            LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                    pendingConnections.put(new LocationPair(to, from),
                            LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS);
                }
            } else if (type == LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY) {
                if (reverseConn == LocationPair.ConnectionType.MONOLATERAL_OPEN) {
                    pendingConnections.put(new LocationPair(from, to),
                            LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS);
                    pendingConnections.put(new LocationPair(to, from),
                            LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                } else if (reverseConn == LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY) {
                    connect(from, to, LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                    connect(to, from, LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                }
            }
        }
    }

    private void disconnect(Location from, Location to) {
        if (from.isValidLocation() && to.isValidLocation()) {
            pendingConnections.put(new LocationPair(from, to), LocationPair.ConnectionType.MONOLATERAL_CLOSED);
            // from closes. update pending connections to->from.
            LocationPair.ConnectionType reverseConn = pendingConnections.get(new LocationPair(to, from));
            if (reverseConn == LocationPair.ConnectionType.BILATERAL_OPEN) {
                pendingConnections.put(new LocationPair(to, from), LocationPair.ConnectionType.MONOLATERAL_OPEN);
            } else if (reverseConn == LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS
                    || reverseConn == LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS) {
                pendingConnections.put(new LocationPair(to, from), LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
            }
        }
    }

    public void printConnections() {
        for (Location from : validLocations) {
            for (Location to : validLocations) {
                if (!from.equals(to) && isConnected(from, to)) {
                    System.out.println(from + " to " + to + " is connected.");
                }
            }
        }
    }

    public void update() {
        updateDirectConnections();
        updateIndirectConnections();
    }

    private void updateDirectConnections() {
        for (Location from : validLocations) {
            for (Location to : validLocations) {
                LocationPair.ConnectionType pendingConnectionType = pendingConnections.get(new LocationPair(from, to));
                if (pendingConnectionType == LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS
                        || pendingConnectionType == LocationPair.ConnectionType.BILATERAL_OPEN) {
                    connection.put(new LocationPair(from, to), true);
                } else {
                    connection.put(new LocationPair(from, to), false);
                }
            }
        }
    }

    // Uses a simplified short-path algorithm to determine whether two nodes are indirectly connected.
    private void updateIndirectConnections() {
        for (Location from : validLocations) {
            for (Location to : validLocations) {
                for (Location mid : validLocations) {
                    if (isConnected(from, mid) && isConnected(mid, to)) {
                        connection.put(new LocationPair(from, to), true);
                    }
                }
            }
        }
    }

    public boolean putPiece(ChessPiece piece) {
        switch (piece.getType()) {
            case TYPE_TRIGO:
                if (placeTrigo(piece)) {
                    update();
                    return true;
                }
            case TYPE_DESTROYER:
                if (useDestroyer(piece)) {
                    update();
                    return true;
                }
            case TYPE_ONE_WAY:
                if (placeOneWay(piece)) {
                    update();
                    return true;
                }
            case TYPE_SINGO:
                if (placeSingo(piece)) {
                    update();
                    return true;
                }
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
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    break;
                case SINGO_UP_LEFT_CLOSED:
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, down, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    break;
                case SINGO_UP_RIGHT_CLOSED:
                    connect(location, down, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    break;
            }
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            switch (piece.getDirection()) {
                case SINGO_DOWN_LEFT_CLOSED:
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, up, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    break;
                case SINGO_DOWN_RIGHT_CLOSED:
                    connect(location, up, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    break;
                case SINGO_DOWN_UP_CLOSED:
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    break;
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
            Location right = new Location(x - 1, y, 0);
            Location down = new Location(x, y + 1, 0);
            connect(piece.getLocation(), down, LocationPair.ConnectionType.MONOLATERAL_OPEN);
            connect(piece.getLocation(), left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
            connect(piece.getLocation(), right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            connect(piece.getLocation(), up, LocationPair.ConnectionType.MONOLATERAL_OPEN);
            connect(piece.getLocation(), left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
            connect(piece.getLocation(), right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
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

            switch (piece.getDirection()) {
                case ONE_WAY_UP_LEFT_IN:
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    connect(location, down, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
                case ONE_WAY_UP_RIGHT_IN:
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    connect(location, down, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
                case ONE_WAY_UP_DOWN_IN:
                    connect(location, down, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
            }
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            switch (piece.getDirection()) {
                case ONE_WAY_DOWN_LEFT_IN:
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    connect(location, up, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
                case ONE_WAY_DOWN_RIGHT_IN:
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    connect(location, up, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
                case ONE_WAY_DOWN_UP_IN:
                    connect(location, up, LocationPair.ConnectionType.MONOLATERAL_OPEN);
                    connect(location, left, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    connect(location, right, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
                    break;
            }
        }
        return true;
    }

    private boolean useDestroyer(ChessPiece piece) {
        Location location = piece.getLocation();
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        if (boardMatrix[x][y][d] == null) {
            return false;
        }
        if (location == RED_START
                || location == RED_END
                || location == BLUE_START
                || location == BLUE_END
                || location == GREEN_START
                || location == GREEN_END) {
            return false;
        }
        boardMatrix[x][y][d] = null;
        if (d == 1) {
            Location left = new Location(x, y, 0);
            Location right = new Location(x - 1, y, 0);
            Location down = new Location(x, y + 1, 0);
            disconnect(location, left);
            disconnect(location, right);
            disconnect(location, down);
        } else {
            Location left = new Location(x + 1, y, 1);
            Location right = new Location(x, y, 1);
            Location up = new Location(x, y - 1, 1);
            disconnect(location, left);
            disconnect(location, right);
            disconnect(location, up);
        }
        return true;
    }

}