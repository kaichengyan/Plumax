package Models;
import java.util.*;

public class Board {

    public static final int BOARD_SIZE = 1; // Should be any positive integer.

    private Piece[][][] boardMatrix;
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
        boardMatrix = new Piece[BOARD_SIZE * 4+ 1][BOARD_SIZE * 4 + 1][2];
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

    public Piece[][][] getBoardMatrix() {
        return boardMatrix;
    }

    public void setBoardMatrix(Location location, Piece piece) {
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        boardMatrix[x][y][d] = piece;
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

    public void connect(Location from, Location to, LocationPair.ConnectionType type) {
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

    public void disconnect(Location from, Location to) {
        if (from.isValidLocation() && to.isValidLocation()) {
            pendingConnections.put(new LocationPair(from, to), LocationPair.ConnectionType.MONOLATERAL_CLOSED);
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
}
