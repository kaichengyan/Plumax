package Models;

import java.util.*;

public class Player {

    public static final int PLAYER_PIECE_NUM = 4;

    private Team team;
    private String name;
    private List<Piece.Type> pieces;

    public Player(String name) {
        this.name = name;
        pieces = new ArrayList<>();
    }

    public void drawPiece(int num) {
        for (int i = 0; i < num; i++) {
            Piece.Type type = randomType();
            pieces.add(type);
            System.out.println("\tYou get a " + type + " piece.");
        }
    }

    private Piece.Type randomType() {
        Random random = new Random();
        int t = random.nextInt(100);
        if (t < 40)
            return Piece.Type.TYPE_SINGO;
        else if (t < 70)
            return Piece.Type.TYPE_TRIGO;
        else if (t < 90)
            return Piece.Type.TYPE_ONE_WAY;
        else
            return Piece.Type.TYPE_DESTROYER;
    }

    public boolean putPiece(Scanner input, Board board) {
        System.out.println("\tYour pieces are: " + getPieces());
        Piece piece;
        Piece.Type type;
        while (!getPieces().contains(type = readType(input))) {
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
        if (piece.use(board)) {
            board.update();
            return true;
        } else {
            return false;
        }
    }

    private Piece.Type readType(Scanner input) {
        System.out.print("\tWhat piece would you like to put? ");
        String typeStr = input.next();
        Piece.Type type;
        if (typeStr.toLowerCase().startsWith("s"))
            type = Piece.Type.TYPE_SINGO;
        else if (typeStr.toLowerCase().startsWith("t"))
            type = Piece.Type.TYPE_TRIGO;
        else if (typeStr.toLowerCase().startsWith("o"))
            type = Piece.Type.TYPE_ONE_WAY;
        else if (typeStr.toLowerCase().startsWith("d"))
            type = Piece.Type.TYPE_DESTROYER;
        else
            type = null;
        return type;
    }

    public List<Piece.Type> getPieces() {
        return pieces;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public enum Team {
        TEAM_RED,
        TEAM_BLUE,
        TEAM_GREEN;
    
        @Override
        public String toString() {
            switch (this) {
                case TEAM_RED: return "Player.Team RED";
                case TEAM_BLUE: return "Player.Team BLUE";
                case TEAM_GREEN: return "Player.Team GREEN";
                default: return null;
            }
        }
    }
}
