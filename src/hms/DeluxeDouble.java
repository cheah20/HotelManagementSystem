package hms;

public class DeluxeDouble extends Room {
    public DeluxeDouble(String roomID, String status) {
        super(roomID, status);
    }

    public String getName() { return "Deluxe Double"; }
    public double getPriceRate() { return 59.99; }
}
