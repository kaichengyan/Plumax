package Models;

import java.util.*;

public class Board {

    private static final int BOARD_SIZE = 1; // Should be any positive integer.

    private Piece[][][] boardMatrix;
    private Map<Location.LocationPair, Boolean> connection;
    private Map<Location.LocationPair, ConnectionType> pendingConnections;
    private Set<Location> validLocations;

    private static final Location RED_START = new Location(BOARD_SIZE * 3, BOARD_SIZE * 4, 0);
    private static final Location RED_START_NEXT = new Location(BOARD_SIZE * 3, BOARD_SIZE * 4 - 1, 1);
    private static final Location RED_END = new Location(BOARD_SIZE + 1, 1, 1);
    private static final Location RED_END_NEXT = new Location(BOARD_SIZE + 1, 2, 0);
    private static final Location GREEN_START = new Location(BOARD_SIZE * 3, BOARD_SIZE + 1, 0);
    private static final Location GREEN_START_NEXT = new Location(BOARD_SIZE * 3, BOARD_SIZE + 1, 1);
    private static final Location GREEN_END = new Location(BOARD_SIZE + 1, BOARD_SIZE * 3, 1);
    private static final Location GREEN_END_NEXT = new Location(BOARD_SIZE + 1, BOARD_SIZE * 3, 0);
    private static final Location BLUE_START = new Location(1, BOARD_SIZE + 1, 0);
    private static final Location BLUE_START_NEXT = new Location(2, BOARD_SIZE + 1, 1);
    private static final Location BLUE_END = new Location(BOARD_SIZE * 4, BOARD_SIZE * 3, 1);
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
                    if (isValidLocation(location)) {
                        validLocations.add(location);
                    }
                }
            }
        }

        for (Location l1 : validLocations) {
            for (Location l2 : validLocations) {
                pendingConnections.put(new Location.LocationPair(l1, l2), ConnectionType.CLOSED);
                boolean isConnected = false;
                if (l1.equals(l2)) {
                    isConnected = true;
                }
                connection.put(new Location.LocationPair(l1, l2), isConnected);
                connection.put(new Location.LocationPair(l2, l1), isConnected);
            }
        }

        connect(RED_START, RED_START_NEXT, ConnectionType.MONOLATERAL_OPEN);
        connect(RED_END, RED_END_NEXT, ConnectionType.MONOLATERAL_OPEN);
        connect(GREEN_START, GREEN_START_NEXT, ConnectionType.MONOLATERAL_OPEN);
        connect(GREEN_END, GREEN_END_NEXT, ConnectionType.MONOLATERAL_OPEN);
        connect(BLUE_START, BLUE_START_NEXT, ConnectionType.MONOLATERAL_OPEN);
        connect(BLUE_END, BLUE_END_NEXT, ConnectionType.MONOLATERAL_OPEN);

        update();

    }

    public void setBoardMatrix(Location location, Piece piece) {
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        boardMatrix[x][y][d] = piece;
    }

    public boolean isLocationPuttable(Location location) {
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        return isValidLocation(location) && !isLocationEnds(location) && boardMatrix[x][y][d] == null;
    }

    public boolean isLocationRemovable(Location location) {
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        return isValidLocation(location) && !isLocationEnds(location) && boardMatrix[x][y][d] != null;
    }

    private boolean isConnected(Location from, Location to) {
        return connection.get(new Location.LocationPair(from, to));
    }

    public Set<Player.Team> winners() {
        Set<Player.Team> winningTeams = new HashSet<>();
        if (isConnected(Board.RED_START, Board.RED_END)) {
            winningTeams.add(Player.Team.TEAM_RED);
        }
        if (isConnected(Board.BLUE_START, Board.BLUE_END)) {
            winningTeams.add(Player.Team.TEAM_BLUE);
        }
        if (isConnected(Board.GREEN_START, Board.GREEN_END)) {
            winningTeams.add(Player.Team.TEAM_GREEN);
        }
        return winningTeams;
    }

    public void connect(Location from, Location to, ConnectionType type) {
        if (isValidLocation(from) && isValidLocation(to)) {
            ConnectionType reverseConn = pendingConnections.get(new Location.LocationPair(to, from));
            pendingConnections.put(new Location.LocationPair(from, to), type);
            if (type == ConnectionType.MONOLATERAL_OPEN) {
                if (reverseConn == ConnectionType.MONOLATERAL_OPEN) {
                    pendingConnections.put(new Location.LocationPair(from, to),
                            ConnectionType.BILATERAL_OPEN);
                    pendingConnections.put(new Location.LocationPair(to, from),
                            ConnectionType.BILATERAL_OPEN);
                } else if (reverseConn == ConnectionType.MONOLATERAL_OUT_ONLY) {
                    pendingConnections.put(new Location.LocationPair(from, to),
                            ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                    pendingConnections.put(new Location.LocationPair(to, from),
                            ConnectionType.BILATERAL_ONE_WAY_CAN_PASS);
                }
            } else if (type == ConnectionType.MONOLATERAL_OUT_ONLY) {
                if (reverseConn == ConnectionType.MONOLATERAL_OPEN) {
                    pendingConnections.put(new Location.LocationPair(from, to),
                            ConnectionType.BILATERAL_ONE_WAY_CAN_PASS);
                    pendingConnections.put(new Location.LocationPair(to, from),
                            ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                } else if (reverseConn == ConnectionType.MONOLATERAL_OUT_ONLY) {
                    connect(from, to, ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                    connect(to, from, ConnectionType.BILATERAL_ONE_WAY_NO_PASS);
                }
            }
        }
    }

    public void disconnect(Location from, Location to) {
        if (isValidLocation(from) && isValidLocation(to)) {
            pendingConnections.put(new Location.LocationPair(from, to), ConnectionType.CLOSED);
            ConnectionType reverseConn = pendingConnections.get(new Location.LocationPair(to, from));
            if (reverseConn == ConnectionType.BILATERAL_OPEN) {
                pendingConnections.put(new Location.LocationPair(to, from), ConnectionType.MONOLATERAL_OPEN);
            } else if (reverseConn == ConnectionType.BILATERAL_ONE_WAY_CAN_PASS
                    || reverseConn == ConnectionType.BILATERAL_ONE_WAY_NO_PASS) {
                pendingConnections.put(new Location.LocationPair(to, from), ConnectionType.MONOLATERAL_OUT_ONLY);
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

    private boolean isLocationEnds(Location location) {
        return location.equals(RED_START)
                || location.equals(RED_END)
                || location.equals(BLUE_START)
                || location.equals(BLUE_END)
                || location.equals(GREEN_START)
                || location.equals(GREEN_END);
    }

    private boolean isValidLocation(Location location) {
        int x = location.getX();
        int y = location.getY();
        int d = location.getD();
        int t = Board.BOARD_SIZE; // t = size of small triangles
        if (d != 0 && d != 1) return false;
        if (1 <= x && x <= t) {
            if (t+1<=y && y<=t+x-1) return true;
            if (y==t+x && d==0) return true;
        } else if (t+1<=x && x<=2*t) {
            if (x-t+1<=y && y<=3*t) return true;
            if (y==x-t && d==1) return true;
        } else if (2*t+1<=x && x<=3*t) {
            if (t+1<=y && y<=x+t-1) return true;
            if (y==x+t && d==0) return true;
        } else if (3*t+1<=x && x<= 4*t) {
            if (x-t+1 <= y && y<=3*t) return true;
            if (y==x-t && d==1) return true;
        }
        return false;
    }

    private void updateDirectConnections() {
        for (Location from : validLocations) {
            for (Location to : validLocations) {
                ConnectionType pendingConnectionType = pendingConnections.get(new Location.LocationPair(from, to));
                if (pendingConnectionType == ConnectionType.BILATERAL_ONE_WAY_CAN_PASS
                        || pendingConnectionType == ConnectionType.BILATERAL_OPEN) {
                    connection.put(new Location.LocationPair(from, to), true);
                } else {
                    connection.put(new Location.LocationPair(from, to), false);
                }
            }
        }
    }

    private void updateIndirectConnections() {
        for (Location from : validLocations) {
            for (Location to : validLocations) {
                for (Location mid : validLocations) {
                    if (isConnected(from, mid) && isConnected(mid, to)) {
                        connection.put(new Location.LocationPair(from, to), true);
                    }
                }
            }
        }
    }

    public enum ConnectionType {
        MONOLATERAL_OUT_ONLY,
        MONOLATERAL_OPEN,
        BILATERAL_OPEN,
        BILATERAL_ONE_WAY_CAN_PASS,
        BILATERAL_ONE_WAY_NO_PASS,
        CLOSED
    }
}
