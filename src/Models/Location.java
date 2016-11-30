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

    public static class LocationPair {

        private Location from;
        private Location to;

        public LocationPair(Location from, Location to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof LocationPair
                    && this.from.equals(((LocationPair) o).from)
                    && this.to.equals(((LocationPair) o).to);
        }

        @Override
        public int hashCode() {
            return this.from.hashCode() +  (this.to.hashCode() << 16) ;
        }

    }

}
