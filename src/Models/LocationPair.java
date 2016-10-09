package Models;

public class LocationPair {

    private Location from;
    private Location to;

    public LocationPair(Location key, Location value) {
        this.from = key;
        this.to = value;
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
