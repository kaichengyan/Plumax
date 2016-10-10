import Models.*;
import java.util.*;

public class PlumaxGame {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";

    private static final int MAX_PLAYER_NUM = 6;

    private int playerNum;
    private Player[] players;
    private Board board;

    public PlumaxGame(int playerNum) {
        if (playerNum < 2 || playerNum > 6 || playerNum == 5) {
            throw new IllegalArgumentException("Models.Player number must be 2, 3, 4 or 6.");
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
        while (!isGameOver()) {
            changeColor(i);
            board.printConnections();
            System.out.println(players[i].getName() + " is playing.");
            players[i].drawPiece(Player.PLAYER_PIECE_NUM - players[i].getPieces().size());
            players[i].putPiece(input, board);
            System.out.println("Thank you. The piece has been put on the board. ");
            System.out.println();
            i++;
            if (i == playerNum) {
                i = 0;
            }
        }
        // Game is over. Print winners.
        changeColor(-1);
        System.out.println("Game is over. \nWinner(s): " + board.winners() +"\n Congratulations!");

    }

    private boolean isGameOver() {
        return !board.winners().isEmpty();
    }

    private void initialize(Scanner input) {
        board = new Board();
        System.out.println("The board has been created.");

        for (int i = 0; i < playerNum; i++) {
            System.out.print("Models.Player " + (i+1) + " name: [Default: Models.Player " + (i+1) + "] ");
            String playerName = input.nextLine();
            if (playerName.isEmpty()) {
                playerName = "Models.Player " + (i+1);
            }
            players[i] = new Player(playerName);
        }

        assignTeam();
        for (int i = 0; i < playerNum; i++) {
            changeColor(i);
            System.out.println(players[i].getName() + " is in " + players[i].getTeam() + ".");
            changeColor(-1);
        }

        System.out.println(playerNum + " players have been initialized.");
        System.out.println();
    }

    private void assignTeam() {
        switch (playerNum) {
            case 2:
                players[0].setTeam(Player.Team.TEAM_RED);
                players[1].setTeam(Player.Team.TEAM_BLUE);
                break;
            case 3:
                players[0].setTeam(Player.Team.TEAM_RED);
                players[1].setTeam(Player.Team.TEAM_BLUE);
                players[2].setTeam(Player.Team.TEAM_GREEN);
                break;
            case 4:
                players[0].setTeam(Player.Team.TEAM_RED);
                players[1].setTeam(Player.Team.TEAM_RED);
                players[2].setTeam(Player.Team.TEAM_BLUE);
                players[3].setTeam(Player.Team.TEAM_BLUE);
                break;
            case 6:
                players[0].setTeam(Player.Team.TEAM_RED);
                players[1].setTeam(Player.Team.TEAM_RED);
                players[2].setTeam(Player.Team.TEAM_BLUE);
                players[3].setTeam(Player.Team.TEAM_BLUE);
                players[4].setTeam(Player.Team.TEAM_GREEN);
                players[5].setTeam(Player.Team.TEAM_GREEN);
                break;
        }
    }

    private void changeColor(int currPlayer) {
        if (currPlayer == -1) {
            System.out.print(ANSI_RESET);
            return;
        }
        Player.Team currPlayerTeam = players[currPlayer].getTeam();
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
