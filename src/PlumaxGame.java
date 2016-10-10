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
            Piece piece;
            do {
                System.out.println("\tYour pieces are: " + players[i].getPieces());
                Piece.Type type;
                while (!players[i].getPieces().contains(type = readType(input))) {
                    System.out.println("\tSorry. You don't have that piece.");
                }
                if (type == Piece.Type.TYPE_TRIGO) {
                    piece = new Trigo(input);
                } else if (type == Piece.Type.TYPE_SINGO) {
                    piece = new Singo(input);
                } else if (type == Piece.Type.TYPE_ONE_WAY) {
                    piece = new Oneway(input);
                } else {
                    piece = new Destroyer(input);
                }
            } while (!players[i].putPiece(board, piece));
            System.out.println("Success!");
            System.out.println();
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

    private Piece.Type readType(Scanner input) {
        System.out.print("\tWhat piece would you like to put? ");
        String typeStr = input.next();
        if (typeStr.toLowerCase().startsWith("s"))
            return Piece.Type.TYPE_SINGO;
        else if (typeStr.toLowerCase().startsWith("t"))
            return Piece.Type.TYPE_TRIGO;
        else if (typeStr.toLowerCase().startsWith("o"))
            return Piece.Type.TYPE_ONE_WAY;
        else if (typeStr.toLowerCase().startsWith("d"))
            return Piece.Type.TYPE_DESTROYER;
        else
            return null;
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
