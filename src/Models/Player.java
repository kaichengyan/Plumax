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

    public boolean putPiece(Board board, Piece piece) {
        if (!pieces.contains(piece.getType())) {
            return false;
        }
        if (piece.use(board)) {
            pieces.remove(piece.getType());
            board.update();
            return true;
        }
        return false;
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

    public void setName(String name) {
        this.name = name;
    }

}
