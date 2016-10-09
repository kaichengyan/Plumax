package Models;

public class LocationPair {

    private Location from;
    private Location to;

    public enum ConnectionType {
        MONOLATERAL_OUT_ONLY,
        MONOLATERAL_OPEN,
        BILATERAL_OPEN,
        BILATERAL_ONE_WAY_CAN_PASS,
        BILATERAL_ONE_WAY_NO_PASS,
        MONOLATERAL_CLOSED
    }

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
