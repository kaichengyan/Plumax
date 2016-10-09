package Models;

import java.util.*;

public class Player {

    public static final int PLAYER_PIECE_NUM = 4;

    public enum Team {
        TEAM_RED,
        TEAM_GREEN,
        TEAM_BLUE
    }

    private Team team;
    private String name;
    private List<ChessPiece.PieceType> pieces;

    public Player(String name) {
        this.name = name;
        pieces = new ArrayList<>();
    }

    public void drawPiece(int num) {
        for (int i = 0; i < num; i++) {
            ChessPiece.PieceType type = randomType();
            pieces.add(type);
            System.out.println("\tYou get a " + type + " piece.");
        }
    }

    private ChessPiece.PieceType randomType() {
        Random random = new Random();
        int t = random.nextInt(100);
        if (t < 40)
            return ChessPiece.PieceType.TYPE_SINGO;
        else if (t < 70)
            return ChessPiece.PieceType.TYPE_TRIGO;
        else if (t < 90)
            return ChessPiece.PieceType.TYPE_ONE_WAY;
        else
            return ChessPiece.PieceType.TYPE_DESTROYER;
    }

    public boolean putPiece(Board board, ChessPiece piece) {
        if (!pieces.contains(piece.getType())) {
            return false;
        }
        if (board.putPiece(piece)) {
            pieces.remove(piece.getType());
            board.update();
            return true;
        }
        return false;
    }

    public List<ChessPiece.PieceType> getPieces() {
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

    public void setName(String name) {
        this.name = name;
    }

}
