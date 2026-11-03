package hms;

// each type sets name and price
public abstract class Room implements Payable, Bookable {
    private final String roomID;
    private final String status;

    protected Room(String roomID, String status) {
        this.roomID = roomID;
        this.status = status;
    }

    public static Room of(String type, String roomID, String status) {
        switch (type) {
            case "DeluxeSingle": return new DeluxeSingle(roomID, status);
            case "DeluxeDouble": return new DeluxeDouble(roomID, status);
            case "PremiumSuite": return new PremiumSuite(roomID, status);
            default: throw new IllegalArgumentException("Unknown room type: " + type);
        }
    }

    public String getID() { return roomID; }
    public String getStatus() { return status; }
}
