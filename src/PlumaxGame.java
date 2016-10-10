import Models.*;

import java.util.*;

public class PlumaxGame {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final int MAX_PLAYER_NUM = 6;

    private int playerNum;
    private Player[] players;
    private Board board;

    public PlumaxGame(int playerNum) {
        if (playerNum < 2 || playerNum > 6 || playerNum == 5) {
            throw new IllegalArgumentException("Player number must be 2, 3, 4 or 6.");
        }
        this.playerNum = playerNum;
        this.players = new Player[MAX_PLAYER_NUM];
    }

    public void play(Scanner input) {

        // Initialize board and players
        initialize(input);

        int i = 0;
        // Game
        System.out.println("The game starts now!");
        while (isGameOver().isEmpty()) {
            changeColor(i);
            board.printConnections();
            System.out.println(players[i].getName() + " is playing.");
            players[i].drawPiece(Player.PLAYER_PIECE_NUM - players[i].getPieces().size());
            System.out.println("\tYour pieces are: " + players[i].getPieces());
            ChessPiece.PieceType type;
            while(!players[i].getPieces().contains(type = readType(input))) {
                System.out.println("\tSorry. You don't have that piece.");
            }
            Location location;
            while (!(location = readLocation(input)).isValidLocation()) {
                System.out.println("\tSorry. That is not a valid location.");
            }
            if (!(type == ChessPiece.PieceType.TYPE_DESTROYER)) {
                if (!board.isLocationPuttable(location)) {
                    System.out.println("\tSorry. That location has been occupied.");
                    while (board.isLocationPuttable(location = readLocation(input))) {
                        System.out.println("\tSorry. That location has been occupied.");
                    }
                }
            } else {
                if (!board.isLocationRemovable(location)) {
                    System.out.println("\tSorry. That location has no piece on it.");
                    while (!board.isLocationRemovable(location = readLocation(input))) {
                        System.out.println("\tSorry. That location has been occupied.");
                    }
                }
            }
            ChessPiece.Direction direction = readDirection(input, type, location);
            if (players[i].putPiece(board, new ChessPiece(type, location, direction)))
                System.out.println("Successful! ");
            board.printConnections();
            i++;
            if (i == playerNum) {
                i = 0;
            }
        }

        // Game is over. Print winners.
        changeColor(-1);
        System.out.println("Game is over. \nWinner(s): " + isGameOver() +"\n Congratulations!");

    }


    private Set<Team> isGameOver() {
        Set<Team> winningTeams = new HashSet<>();
        if (board.isConnected(Board.RED_START, Board.RED_END)) {
            winningTeams.add(Team.TEAM_RED);
        }
        if (board.isConnected(Board.BLUE_START, Board.BLUE_END)) {
            winningTeams.add(Team.TEAM_BLUE);
        }
        if (board.isConnected(Board.GREEN_START, Board.GREEN_END)) {
            winningTeams.add(Team.TEAM_GREEN);
        }
        return winningTeams;
    }

    private void initialize(Scanner input) {
        board = new Board();
        System.out.println("The board has been created.");

        for (int i = 0; i < playerNum; i++) {
            System.out.print("Player " + (i+1) + " name: [Default: Player " + (i+1) + "] ");
            String playerName = input.nextLine();
            if (playerName.isEmpty()) {
                playerName = "Player " + (i+1);
            }
            players[i] = new Player(playerName);
        }
        assignTeam();
        for (int i = 0; i < playerNum; i++) {
            changeColor(i);
            System.out.println(players[i].getName() + " is in " + players[i].getTeam().getTeamDescription() + ".");
            changeColor(-1);
        }
        System.out.println(playerNum + " players have been initialized.");
        System.out.println();
    }

    private void assignTeam() {
        switch (playerNum) {
            case 2:
                players[0].setTeam(Team.TEAM_RED);
                players[1].setTeam(Team.TEAM_BLUE);
                break;
            case 3:
                players[0].setTeam(Team.TEAM_RED);
                players[1].setTeam(Team.TEAM_BLUE);
                players[2].setTeam(Team.TEAM_GREEN);
                break;
            case 4:
                players[0].setTeam(Team.TEAM_RED);
                players[1].setTeam(Team.TEAM_RED);
                players[2].setTeam(Team.TEAM_BLUE);
                players[3].setTeam(Team.TEAM_BLUE);
                break;
            case 6:
                players[0].setTeam(Team.TEAM_RED);
                players[1].setTeam(Team.TEAM_RED);
                players[2].setTeam(Team.TEAM_BLUE);
                players[3].setTeam(Team.TEAM_BLUE);
                players[4].setTeam(Team.TEAM_GREEN);
                players[5].setTeam(Team.TEAM_GREEN);
                break;
        }
    }

    private ChessPiece.PieceType readType(Scanner input) {
        System.out.print("\tWhat piece would you like to put? ");
        String typeStr = input.next();
        if (typeStr.toLowerCase().startsWith("s"))
            return ChessPiece.PieceType.TYPE_SINGO;
        else if (typeStr.toLowerCase().startsWith("t"))
            return ChessPiece.PieceType.TYPE_TRIGO;
        else if (typeStr.toLowerCase().startsWith("o"))
            return ChessPiece.PieceType.TYPE_ONE_WAY;
        else if (typeStr.toLowerCase().startsWith("d"))
            return ChessPiece.PieceType.TYPE_DESTROYER;
        else
            return null;
    }

    private Location readLocation(Scanner input) {
        System.out.print("\tWhat location would you like to put it? ");
        int x = input.nextInt();
        int y = input.nextInt();
        int d = input.nextInt();
        return new Location(x, y, d);
    }

    private ChessPiece.Direction readDirection(Scanner input,
                                               ChessPiece.PieceType type,
                                               Location location) {
        switch (type) {
            case TYPE_SINGO:
                System.out.println("\tWhere do you want the closed side? ");
                if (location.getD() == 1) {
                    System.out.println("\t1) Upper-right");
                    System.out.println("\t2) Upper-left");
                    System.out.println("\t3) Down");
                    System.out.print("\t");
                    switch (input.nextInt()) {
                        case 1:
                            return ChessPiece.Direction.SINGO_UP_RIGHT_CLOSED;
                        case 2:
                            return ChessPiece.Direction.SINGO_UP_LEFT_CLOSED;
                        case 3:
                            return ChessPiece.Direction.SINGO_UP_DOWN_CLOSED;
                    }
                } else {
                    System.out.println("\t1) Lower-right");
                    System.out.println("\t2) Lower-left");
                    System.out.println("\t3) Up");
                    System.out.print("\t");
                    switch (input.nextInt()) {
                        case 1:
                            return ChessPiece.Direction.SINGO_DOWN_RIGHT_CLOSED;
                        case 2:
                            return ChessPiece.Direction.SINGO_DOWN_LEFT_CLOSED;
                        case 3:
                            return ChessPiece.Direction.SINGO_DOWN_UP_CLOSED;
                    }
                }
            case TYPE_TRIGO:
                if (location.getD() == 1) {
                    return ChessPiece.Direction.TRIGO_UP;
                } else {
                    return ChessPiece.Direction.TRIGO_DOWN;
                }
            case TYPE_ONE_WAY:
                System.out.println("\tWhere do you want the one-way-in path? ");
                if (location.getD() == 1) {
                    System.out.println("\t1) Upper-right");
                    System.out.println("\t2) Upper-left");
                    System.out.println("\t3) Down");
                    System.out.print("\t");
                    switch (input.nextInt()) {
                        case 1:
                            return ChessPiece.Direction.ONE_WAY_UP_RIGHT_IN;
                        case 2:
                            return ChessPiece.Direction.ONE_WAY_UP_LEFT_IN;
                        case 3:
                            return ChessPiece.Direction.ONE_WAY_UP_DOWN_IN;
                    }
                } else {
                    System.out.println("\t1) Lower-right");
                    System.out.println("\t2) Lower-left");
                    System.out.println("\t3) Up");
                    System.out.print("\t");
                    switch (input.nextInt()) {
                        case 1:
                            return ChessPiece.Direction.ONE_WAY_DOWN_RIGHT_IN;
                        case 2:
                            return ChessPiece.Direction.ONE_WAY_DOWN_LEFT_IN;
                        case 3:
                            return ChessPiece.Direction.ONE_WAY_DOWN_UP_IN;
                    }
                }
            case TYPE_DESTROYER:
                return ChessPiece.Direction.DESTROYER;
            default:
                return null;
        }
    }

    private void changeColor(int currPlayer) {
        if (currPlayer == -1) {
            System.out.print(ANSI_RESET);
            return;
        }
        Team currPlayerTeam = players[currPlayer].getTeam();
        switch (currPlayerTeam) {
            case TEAM_RED:
                System.out.print(ANSI_RED);
                break;
            case TEAM_BLUE:
                System.out.print(ANSI_BLUE);
                break;
            case TEAM_GREEN:
                System.out.print(ANSI_GREEN);
                break;
            default:
                break;
        }
    }

}
