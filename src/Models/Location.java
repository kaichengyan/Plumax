package Models;

public class Location {

    private int x;
    private int y;
    private int d;

    public Location(int x, int y, int d) {
        this.x = x;
        this.y = y;
        this.d = d;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getD() {
        return d;
    }

    public boolean isValidLocation() {
        int t = Board.BOARD_SIZE / 4; // t = size of small triangles
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

    @Override
    public boolean equals(Object other) {
        return other instanceof Location
                && (this.x == ((Location) other).x)
                && (this.y == ((Location) other).y)
                && (this.d == ((Location) other).d);
    }

    @Override
    public int hashCode() {
        return x + (y<<8) + (d<<15);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + d + ")";
    }

}
