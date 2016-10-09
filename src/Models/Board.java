package Models;
import java.util.*;

public class Board {

    public static final int BOARD_SIZE = 1;
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
        validLocations = new LinkedHashSet<>();
        connection = new LinkedHashMap<>();
        pendingConnections = new LinkedHashMap<>();

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

    public boolean isLocationOccupied(Location location) {
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        return location.equals(RED_START)
                || location.equals(RED_END)
                || location.equals(BLUE_START)
                || location.equals(BLUE_END)
                || location.equals(GREEN_START)
                || location.equals(GREEN_END)
                || (location.isValidLocation() && boardMatrix[x][y][d] != null);
    }

    public boolean isConnected(Location l1, Location l2) {
        return connection.get(new LocationPair(l1, l2));
    }

    private void connect(Location l1, Location l2, LocationPair.ConnectionType type) {
        if (l1.isValidLocation() && l2.isValidLocation()) {
            LocationPair.ConnectionType reverseConn = pendingConnections.get(new LocationPair(l2, l1));
            pendingConnections.put(new LocationPair(l1, l2), type);
            if (type == LocationPair.ConnectionType.MONOLATERAL_OPEN) {
                if (reverseConn == LocationPair.ConnectionType.MONOLATERAL_OPEN) {
                    pendingConnections.put(new LocationPair(l1, l2), LocationPair.ConnectionType.BILATERAL_OPEN);
                    pendingConnections.put(new LocationPair(l2, l1), LocationPair.ConnectionType.BILATERAL_OPEN);
                } else if (reverseConn == LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY) {
                    pendingConnections.put(new LocationPair(l1, l2), LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                    pendingConnections.put(new LocationPair(l2, l1), LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS);
                }
            } else if (type == LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY) {
                if (reverseConn == LocationPair.ConnectionType.MONOLATERAL_OPEN) {
                    pendingConnections.put(new LocationPair(l1, l2), LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS);
                    pendingConnections.put(new LocationPair(l2, l1), LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                } else if (reverseConn == LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY) {
                    connect(l1, l2, LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                    connect(l2, l1, LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                }
            }
        }
    }

    private void disconnect(Location l1, Location l2) {
        pendingConnections.put(new LocationPair(l1, l2), LocationPair.ConnectionType.MONOLATERAL_CLOSED);
        // l1 closes. update pending connections l2->l1.
        LocationPair.ConnectionType reverseConn = pendingConnections.get(new LocationPair(l2, l1));
        if (reverseConn == LocationPair.ConnectionType.BILATERAL_OPEN) {
            pendingConnections.put(new LocationPair(l2, l1), LocationPair.ConnectionType.MONOLATERAL_OPEN);
        } else if (reverseConn == LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS
                || reverseConn == LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS) {
            pendingConnections.put(new LocationPair(l2, l1), LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
        }
        updateDirectConnections();
    }

    public void printConnections() {
        for (Location l1 : validLocations) {
            for (Location l2 : validLocations) {
                if (l1.compareTo(l2) <= 0 && isConnected(l1, l2)) {
                    System.out.println(l1 + " to " + l2 + " is connected.");
                }
            }
        }
    }

    public void update() {
//        updatePendingBilateralConnections();
        updateDirectConnections();
        updateIndirectConnections();
    }

    private void updateDirectConnections() {
        for (Location l1 : validLocations) {
            for (Location l2 : validLocations) {
                LocationPair.ConnectionType pendingConnectionType = pendingConnections.get(new LocationPair(l1, l2));
                if (pendingConnectionType == LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS
                        || pendingConnectionType == LocationPair.ConnectionType.BILATERAL_OPEN) {
                    connection.put(new LocationPair(l1, l2), true);
                } else {
                    connection.put(new LocationPair(l1, l2), false);
                }
            }
        }
    }

    private void updateIndirectConnections() {
        for (Location l1 : validLocations) {
            for (Location l2 : validLocations) {
                for (Location mid : validLocations) {
                    if (isConnected(l1, mid) && isConnected(mid, l2)) {
                        connection.put(new LocationPair(l1, l2), true);
                    }
                }
            }
        }
    }

//    private void updatePendingBilateralConnections() {
//        for (Location l1 : validLocations) {
//            for (Location l2 : validLocations) {
//                if (!l1.equals(l2)) {
//                    LocationPair.ConnectionType connectionType1 = pendingConnections.get(new LocationPair(l1, l2));
//                    LocationPair.ConnectionType connectionType2 = pendingConnections.get(new LocationPair(l2, l1));
//                    if (connectionType1 == LocationPair.ConnectionType.MONOLATERAL_OPEN) {
//                        if (connectionType2 == LocationPair.ConnectionType.MONOLATERAL_OPEN) {
//                            connect(l1, l2, LocationPair.ConnectionType.BILATERAL_OPEN);
//                            connect(l2, l1, LocationPair.ConnectionType.BILATERAL_OPEN);
//                        } else if (connectionType2 == LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY) {
//                            connect(l1, l2, LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
//                            connect(l2, l1, LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS);
//                        }
//                    } else if (connectionType1 == LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY) {
//                        if (connectionType2 == LocationPair.ConnectionType.MONOLATERAL_OPEN) {
//                            connect(l1, l2, LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS);
//                            connect(l2, l1, LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
//                        } else if (connectionType2 == LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY) {
//                            connect(l1, l2, LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
//                            connect(l2, l1, LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
//                        }
//                    }
////                    } else if (connectionType1 == LocationPair.ConnectionType.MONOLATERAL_CLOSED) {
////                        if (connectionType2 == LocationPair.ConnectionType.BILATERAL_OPEN) {
////                            connect(l2, l1, LocationPair.ConnectionType.MONOLATERAL_OPEN);
////                        } else if (connectionType2 == LocationPair.ConnectionType.BILATERAL_ONE_WAY_CAN_PASS) {
////                            connect(l2, l1, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
////                        } else if (connectionType2 == LocationPair.ConnectionType.BILATERAL_ONE_WAY_NO_PASS) {
////                            connect(l2, l1, LocationPair.ConnectionType.MONOLATERAL_OUT_ONLY);
////                        }
////                    }
//                }
//            }
//        }
//    }


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
        int x = piece.getLocation().getX();
        int y = piece.getLocation().getY();
        int d = piece.getLocation().getD();
        if (boardMatrix[x][y][d] == null) {
            return false;
        }
        boardMatrix[x][y][d] = null;
        if (d == 1) {
            Location left = new Location(x, y, 0);
            if (left.isValidLocation()) {
                disconnect(piece.getLocation(), left);
            }
            Location right = new Location(x - 1, y, 0);
            if (right.isValidLocation()) {
                disconnect(piece.getLocation(), right);
            }
            Location down = new Location(x, y + 1, 0);
            if (down.isValidLocation()) {
                disconnect(piece.getLocation(), down);
            }
        } else {
            Location left = new Location(x + 1, y, 1);
            if (left.isValidLocation()) {
                disconnect(piece.getLocation(), left);
            }
            Location right = new Location(x, y, 1);
            if (right.isValidLocation()) {
                disconnect(piece.getLocation(), right);
            }
            Location up = new Location(x, y - 1, 1);
            if (up.isValidLocation()) {
                disconnect(piece.getLocation(), up);
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

}
